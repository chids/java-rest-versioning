package com.github.chids.restversioning.serialize;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.stax2.XMLStreamWriter2;
import org.junit.Test;

import com.github.chids.restversioning.serialize.AbstractMessageBodyWriter;

public abstract class AbstractProviderTest<T>
{
    protected abstract AbstractMessageBodyWriter<T> createWriter();

    protected abstract T createMock();
    
    protected abstract String expectedJson();
    
    protected abstract String expectedXml();

    @Test
    public final void json() throws IOException
    {
        final StringWriter result = new StringWriter();
        final JsonGenerator json = new JsonFactory().createJsonGenerator(result);
        createWriter().writeEntity(createMock(), json);
        json.close();
        assertEquals(expectedJson(), result.toString());
    }

    @Test
    public final void xml() throws XMLStreamException
    {
        final XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
        final StringWriter result = new StringWriter();
        final XMLStreamWriter2 xml = (XMLStreamWriter2)xmlFactory.createXMLStreamWriter(result);
        createWriter().writeEntity(createMock(), xml);
        xml.close();
        assertEquals(expectedXml(), result.toString());
    }
}
