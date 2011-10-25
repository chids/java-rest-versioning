package com.github.chids.restversioning.serialize.group;

import java.io.IOException;

import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLStreamException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.stax2.XMLStreamWriter2;

import com.github.chids.restversioning.model.Group;
import com.github.chids.restversioning.model.Person;
import com.github.chids.restversioning.serialize.Version1BodyWriter;
import com.github.chids.restversioning.serialize.person.PersonVersion1Provider;

@Provider
public final class GroupVersion1Provider extends Version1BodyWriter<Group>
{
    private static final PersonVersion1Provider personWriter = new PersonVersion1Provider();

    public GroupVersion1Provider()
    {
        super(Group.class);
    }

    @Override
    public void writeEntity(final Group group, final JsonGenerator json) throws IOException
    {
        json.writeStartObject();
        {
            json.writeStringField("name", group.name);
            json.writeArrayFieldStart("members");
            {
                for(final Person person : group.members)
                {
                    personWriter.writeEntity(person, json);
                }
            }
            json.writeEndArray();
        }
        json.writeEndObject();
    }

    @Override
    public void writeEntity(final Group group, final XMLStreamWriter2 xml) throws XMLStreamException
    {
        xml.writeStartElement(group.name);
        {
            xml.writeStartElement("members");
            {
                for(final Person person : group.members)
                {
                    xml.writeStartElement("member");
                    {
                        personWriter.writeEntity(person, xml);
                    }
                    xml.writeEndElement();
                }
            }
            xml.writeEndElement();
        }
        xml.writeEndElement();
    }

    @Override
    public String getXmlNs()
    {
        return GROUP_XML_NS;
    }
}