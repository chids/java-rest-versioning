package com.github.chids.restversioning.serialize.person;

import java.io.IOException;

import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLStreamException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.stax2.XMLStreamWriter2;

import com.github.chids.restversioning.model.Person;
import com.github.chids.restversioning.serialize.Version1BodyWriter;

@Provider
public final class PersonVersion1Provider extends Version1BodyWriter<Person>
{
    public PersonVersion1Provider()
    {
        super(Person.class);
    }

    @Override
    public void writeEntity(final Person person, final JsonGenerator json) throws IOException
    {
        json.writeStartObject();
        {
            json.writeStringField("name", person.givenName + ' ' + person.surname);
        }
        json.writeEndObject();
    }

    @Override
    public void writeEntity(final Person person, final XMLStreamWriter2 xml) throws XMLStreamException
    {
        xml.writeStartElement("name");
        {
            xml.writeCharacters(person.givenName);
            xml.writeCharacters(" ");
            xml.writeCharacters(person.surname);
        }
        xml.writeEndElement();
    }

    @Override
    public String getXmlNs()
    {
        return PERSON_XML_NS;
    }
}