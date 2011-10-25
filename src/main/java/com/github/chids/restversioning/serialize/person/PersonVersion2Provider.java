package com.github.chids.restversioning.serialize.person;

import java.io.IOException;

import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLStreamException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.stax2.XMLStreamWriter2;

import com.github.chids.restversioning.model.Person;
import com.github.chids.restversioning.serialize.Version2BodyWriter;

@Provider
public final class PersonVersion2Provider extends Version2BodyWriter<Person>
{
    public PersonVersion2Provider()
    {
        super(Person.class);
    }

    @Override
    public void writeEntity(final Person person, final JsonGenerator json) throws IOException
    {
        json.writeStartObject();
        {
            json.writeObjectFieldStart("name");
            {
                json.writeStringField("first", person.givenName);
                json.writeStringField("last", person.surname);
            }
            json.writeEndObject();
        }
        json.writeEndObject();
    }

    @Override
    public void writeEntity(final Person person, final XMLStreamWriter2 xml) throws XMLStreamException
    {
        xml.writeStartElement("name");
        {
            xml.writeStartElement("first");
            {
                xml.writeCharacters(person.givenName);
            }
            xml.writeEndElement();
            xml.writeStartElement("last");
            {
                xml.writeCharacters(person.surname);
            }
            xml.writeEndElement();
        }
        xml.writeEndElement();
    }

    @Override
    public String getXmlNs()
    {
        return PERSON_XML_NS;
    }
}