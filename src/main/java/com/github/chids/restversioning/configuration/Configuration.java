package com.github.chids.restversioning.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebListener;

import com.github.chids.restversioning.Service;
import com.github.chids.restversioning.serialize.group.GroupVersion1Provider;
import com.github.chids.restversioning.serialize.person.PersonVersion1Provider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

@WebListener
public final class Configuration extends GuiceServletContextListener
{
    @Override
    protected Injector getInjector()
    {
        return Guice.createInjector(
                new ServletModule()
                {
                    @Override
                    protected void configureServlets()
                    {
                        serve("/*").with(GuiceContainer.class, createServletParams());
                    }
                });
    }

    @SuppressWarnings("serial")
    static Map<String, String> createServletParams()
    {
        return new HashMap<String, String>()
        {
            {
                put(PackagesResourceConfig.PROPERTY_PACKAGES, joinPackageNames(Service.class, PersonVersion1Provider.class, GroupVersion1Provider.class));
                put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, GZIPContentEncodingFilter.class.getName());
                put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, GZIPContentEncodingFilter.class.getName());
                put(ResourceConfig.FEATURE_DISABLE_WADL, Boolean.toString(false));
            }
        };
    }

    static String joinPackageNames(final Class<?>... classes)
    {
        final StringBuilder sb = new StringBuilder();
        for(final Class<?> clazz : classes)
        {
            sb.append(clazz.getPackage().getName()).append(';');
        }
        return sb.toString();
    }
}