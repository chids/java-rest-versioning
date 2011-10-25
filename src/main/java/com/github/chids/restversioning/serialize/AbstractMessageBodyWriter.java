package com.github.chids.restversioning.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.stax2.XMLStreamWriter2;

public abstract class AbstractMessageBodyWriter<T> implements MessageBodyWriter<T>
{
    private static final String XML = "xml";
    private static final String JSON = "json";
    public static final String X_ACCEPTABLE = "X-Acceptable";
    public static final String PERSON_XML_NS = "http://chids.github.com/person/";
    public static final String GROUP_XML_NS = "http://chids.github.com/group/";
    private static final XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
    private static final JsonFactory jsonFactory = new JsonFactory();
    private final MediaType json;
    private final MediaType xml;
    private final List<Variant> variants;
    private final Class<?> clazz;

    public AbstractMessageBodyWriter(final Class<?> type)
    {
        this.clazz = type;
        final Produces annotation = getClass().getAnnotation(Produces.class);
        if(annotation == null)
        {
            throw new InstantiationError(getClass().getSimpleName() + " is missing the @" + Produces.class.getSimpleName() + " annotation");
        }
        this.json = findMimeType(annotation, JSON);
        this.xml = findMimeType(annotation, XML);
        this.variants = Variant.VariantListBuilder.newInstance()
                .mediaTypes(this.json, this.xml)
                .encodings("utf-8")
                .build();
    }

    private MediaType findMimeType(final Produces annotation, final String qualifier)
    {
        for(final String candidate : annotation.value())
        {
            final MediaType mime = MediaType.valueOf(candidate);
            if(mime.getSubtype().endsWith(qualifier))
            {
                return mime;
            }
        }
        throw new InstantiationError(getClass().getSimpleName() + "'s @" + Produces.class.getSimpleName() + " annotation lacks type for " + qualifier);
    }

    @Override
    public final long getSize(final T entity, final Class<?> clazz, final Type type, final Annotation[] annotations, final MediaType mime)
    {
        return -1;
    }

    @Override
    public final boolean isWriteable(final Class<?> clazz, final Type type, final Annotation[] annotations, final MediaType mime)
    {
        return this.clazz.isAssignableFrom(clazz);
    }

    @Override
    public final void writeTo(
            final T entity,
            final Class<?> clazz,
            final Type type,
            final Annotation[] annotations,
            final MediaType mime,
            final MultivaluedMap<String, Object> headers,
            final OutputStream out) throws IOException, WebApplicationException
    {
        if(mime.getSubtype().endsWith(XML))
        {
            writeXml(entity, out);
        }
        else if(mime.getSubtype().endsWith(JSON))
        {
            final JsonGenerator json = jsonFactory.createJsonGenerator(out);
            writeEntity(entity, json);
            json.close();
        }
        else
        {
            throw new WebApplicationException(Response
                    .notAcceptable(this.variants)
                    .header(X_ACCEPTABLE, this.json)
                    .header(X_ACCEPTABLE, this.xml)
                    .build());
        }
    }

    private void writeXml(final T entity, final OutputStream out)
    {
        try
        {
            final XMLStreamWriter2 xml = (XMLStreamWriter2)xmlFactory.createXMLStreamWriter(out);
            xml.writeStartDocument();
            {
                xml.writeStartElement(this.clazz.getSimpleName().toLowerCase());
                {
                    xml.writeNamespace("ns", getXmlNs());
                    writeEntity(entity, xml);
                }
                xml.writeEndDocument();
            }
            xml.writeEndDocument();
            xml.close();
        }
        catch(final XMLStreamException e)
        {
            throw new WebApplicationException(Response
                    .serverError()
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build());
        }
    }

    public abstract String getXmlNs();

    public abstract void writeEntity(T entity, JsonGenerator json) throws IOException;

    public abstract void writeEntity(T entity, XMLStreamWriter2 xml) throws XMLStreamException;
}