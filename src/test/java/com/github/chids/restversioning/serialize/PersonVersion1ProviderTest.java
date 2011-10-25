package com.github.chids.restversioning.serialize;

import com.github.chids.restversioning.model.Person;
import com.github.chids.restversioning.serialize.AbstractMessageBodyWriter;
import com.github.chids.restversioning.serialize.person.PersonVersion1Provider;

public class PersonVersion1ProviderTest extends AbstractProviderTest<Person>
{
    @Override
    protected AbstractMessageBodyWriter<Person> createWriter()
    {
        return new PersonVersion1Provider();
    }

    @Override
    protected Person createMock()
    {
        return new Person("bar", "baz");
    }

    @Override
    protected String expectedJson()
    {
        return "{\"name\":\"bar baz\"}";
    }

    @Override
    protected String expectedXml()
    {
        return "<name>bar baz</name>";
    }
}