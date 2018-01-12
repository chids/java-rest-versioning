[![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/)

----

:warning: Please note that this text was written late 2011 :warning:

----

#tl;dr

This text describes the reasoning and deployment layout for the sample service implemented in this project.

# Background

## A Twitter chat with Peter Krantz

The 11th October 2011 Peter Krantz [@peterkz_swe](https://twitter.com/#!/peterkz_swe) posted a link to an article on [versioning REST API](http://thereisnorightway.blogspot.com/2011/02/versioning-and-types-in-resthttp-api.html):s to which I responded and we had a lengthy chat on the pros and cons of using custom media types to version services.

## My stance on versioning and ease of use

While I like the custom media type approach for being [REST](http://en.wikipedia.org/wiki/Representational_State_Transfer) "pure" I dislike it because it makes it harder to use the API for novice programmers (of which I have a great deal using my APIs at work). Also it makes exploring the API using a browser much more awkward and while I like and use [cURL](http://curl.haxx.se/) my browser window is always more readily at hand.



# Service versioning...

## ...using the URL

The upside of sticking a version number, and possibly even a format identifier, in the URL is obviously that it makes the service very easy to use from a browser. In addition it also makes the service very easy to use for novice programmers - something that might or might not be of utter importance depending on what kind of user base you're targeting. For example:
    
    http://foo.bar/a-service/some-resource.v1.xml

## ...using custom media types

Specifying the version in the media type forces us to create a custom media type. We might, for example, chose to specify our media type as `application/vnd.baz-v1+xml` and our service invocation then has to specify this media type in the HTTP Accept header. For example:
    
    GET /a-service/some-resource HTTP/1.1
    Host: foo.bar
    Accept: application/vnd.baz-v1+xml

## Implementation considerations (Java)

The custom media type maps quite naturally into Java based services using [Jersey](http://jersey.dev.java.net/) (and possibly other JVM based languages with Java interoperability) through Jersey's ability to use custom `MessageBodyWriter` classes. Using the URL versioning with optional format file extensions does not map as cleanly however.

## Operational considerations

I think there's a point in being able to have two versions of a service run in parallel on the same server (for my current environment this means in the same [Tomcat](http://tomcat.apache.org/) instance) but isolated from each other. I also think it might be good to separate disparate versions of a service into different "projects" instead of maintaining all versions of a service within the same code base.
One way of solving this is by simply giving each service their own root context (for example by using [Tomcat's hash-sign thingy](http://tomcat.apache.org/tomcat-7.0-doc/config/context.html#Naming). But then we're back to putting versions in the URL which is considered a _bad thing_.

## Summing up the considerations

1. I want to implement versioning using custom media types since that provides a clean implementation.
2. I want to allow exploratory interaction and support less tech savvy developers by allowing them to specify the version in the URL and the format as a file extension
3. I want to separate different versions of the same service in the runtime and be able to deploy updates to a given service version without interrupting other services (and preferably without interrupting the service being deployed)

# Solution design

Achieving (1) is simple by doing a media type based implementation using Jersey.

I don't want to clutter my service with code to handle, optional, version and format specifiers in the URL. So to achieve (2) I need to make it appear as my service supports these things without actually changing my implementation - enter a HTTP proxy which in my case will be [nginx](http://nginx.org).

To achieve (3) this I will Tomcat and its #-naming support to give separate versions of each service their own root context. This will effectively put a version number in the URL of each instance. But this is a version number I don't want to force the outside world to know about - unless they choose to access the service by specifying the version in the URL. So again my HTTP proxy will have to cover my sorry ass.

## Tomcat deployment

Deploying the following two WAR files...

* rest-versioning#v1.war
* rest-versioning#v2.war

...will give us the following root contexts...

* http://127.0.0.1:8080/rest-versioning/v1
* http://127.0.0.1:8080/rest-versioning/v2

...which give us the following service endpoints...

* http://127.0.0.1:8080/rest-versioning/v1/person
* http://127.0.0.1:8080/rest-versioning/v1/group
* http://127.0.0.1:8080/rest-versioning/v2/person

## What's lacking

I want to publish the above service endpoints as versioned by media type...

* GET http://127.0.0.1:8080/rest-versioning/person
    * using `Accept: application/vnd.baz-v1+xml`
* GET http://127.0.0.1:8080/rest-versioning/person
    * using `Accept: application/vnd.baz-v1+json`
* GET http://127.0.0.1:8080/rest-versioning/person
    * using `Accept: application/vnd.baz-v2+xml`
* GET http://127.0.0.1:8080/rest-versioning/person
    * using `Accept: application/vnd.baz-v2+json`
* GET http://127.0.0.1:8080/rest-versioning/group
    * using `Accept: application/vnd.baz-v1+xml`
* GET http://127.0.0.1:8080/rest-versioning/group
    * using `Accept: application/vnd.baz-v1+json`

...as well as URL-versioned...

* http://127.0.0.1:8080/rest-versioning/person.v1.xml
* http://127.0.0.1:8080/rest-versioning/person.v1.json
* http://127.0.0.1:8080/rest-versioning/person.v2.xml
* http://127.0.0.1:8080/rest-versioning/person.v2.json
* http://127.0.0.1:8080/rest-versioning/group.v1.xml
* http://127.0.0.1:8080/rest-versioning/group.v1.json

...so that both requests to...

    http://127.0.0.1:8080/rest-versioning/person
    Accept: application/vnd.baz-v1+xml

...and to...

    http://127.0.0.1:8080/rest-versioning/person.v1.xml
    Accept: application/xml

...would end up being handled by...

    http://127.0.0.1:8080/rest-versioning/v1/person


## nginx deployment

Below is sample nginx configuration to make the above work for the sake of this text. However don't trust this configuration for your production environment.

    server {
        listen 8183;
        server_name 127.0.0.1;
        proxy_redirect      off;
        proxy_set_header    host        $http_host;
        proxy_set_header    x-real-ip   $remote_addr;

        set $apiMime "$http_accept";
        # Figure out whether we're supposed to extract information from the URI
        if ($uri ~ ^.*\.(v\d)\.(xml|json)$) {
            set $apiVersion $1;
            set $apiMime "application/$2;charset=utf-8";

            # Use the version information from the URL to proxy to the right
            # instance and strip the version and mime type information before
            # proxying to the actual service
            rewrite ^(.*)/(\w*).*$ $1/$apiVersion/$2 last;
        }
        # Figure out whether we're supposed to extract information from the Accept header
        if ($http_accept ~ ^application/vnd\.chids\.versioning-(v\d)\+(xml|json)) {
            set $apiVersion $1;
            # Use the version information from the accept header to proxy to
            # the right instance
            rewrite ^(.*)/(\w*)$ $1/$apiVersion/$2 last;
        }

        proxy_set_header        Accept $apiMime;
        location / {
            proxy_pass          http://127.0.0.1:8080;
        }
    }


## Testing

When running Tomcat on port 8080 (with the same WAR file deployed twice with different names - as specified above) and nginx with the above configuration you should be able to successfully access the service using:

    curl -v http://127.0.0.1:8183/rest-versioning/person.v1.xml

...as well as:

    curl -v --header 'Accept: application/vnd.chids.versioning-v1+json' http://127.0.0.1:8183/rest-versioning/person


# Conclusion

At the time of this writing I'm leaning into this being pragmatic. And pragmatism is my overall goal, but I'm far from sure that this is the best way to achieve it. Comments and feedback are, as always, very welcome: [marten.gustafson@gmail.com](mailto:marten.gustafson@gmail.com).

# Other resources / see also

## Completely stand-alone services in Scala (using Jersey and Jetty)

[Coda Hale](http://codahale.com/) at Yammer have published what in my eyes appears to be a most excellent framework for building HTTP services called [Dropwizard](http://github.com/codahale/dropwizard) and I encourage you to have a look at it.
The problem I currently face with the Dropwziard-style of services is that every service would have its own port number which when running multiple services on the same server might introduce some additional operational and deployment headaches most notably with the proxy configuration. It you're running in ze "cloud" (or otherwise virtualized environment) you could consider running one service per instance and having lots of small instances. Which would inevitable lead you to explore infrastructure automation using tools such as [Chef](http://www.opscode.com/) and [Puppet](http://puppetlabs.com/) if you haven't already. Which is a _good thing_. But I digress.
