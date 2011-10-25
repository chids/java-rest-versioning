package com.github.chids.restversioning.serialize;

import com.github.chids.restversioning.model.Person;
import com.github.chids.restversioning.serialize.AbstractMessageBodyWriter;
import com.github.chids.restversioning.serialize.person.PersonVersion2Provider;

public class PersonVersion2ProviderTest extends AbstractProviderTest<Person>
{
    @Override
    protected AbstractMessageBodyWriter<Person> createWriter()
    {
        return new PersonVersion2Provider();
    }

    @Override
    protected Person createMock()
    {
        return new Person("bar", "baz");
    }

    @Override
    protected String expectedJson()
    {
        return "{\"name\":{\"first\":\"bar\",\"last\":\"baz\"}}";
    }

    @Override
    protected String expectedXml()
    {
        return "<name><first>bar</first><last>baz</last></name>";
    }
}