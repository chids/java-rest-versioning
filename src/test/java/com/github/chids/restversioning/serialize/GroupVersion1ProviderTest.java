package com.github.chids.restversioning.serialize;

import com.github.chids.restversioning.model.Group;
import com.github.chids.restversioning.model.Person;
import com.github.chids.restversioning.serialize.group.GroupVersion1Provider;

public class GroupVersion1ProviderTest extends AbstractProviderTest<Group>
{
    @Override
    protected AbstractMessageBodyWriter<Group> createWriter()
    {
        return new GroupVersion1Provider();
    }

    @Override
    protected Group createMock()
    {
        return new Group("foo", new Person("a", "b"), new Person("x", "y"));
    }

    @Override
    protected String expectedJson()
    {
        return "{\"name\":\"foo\",\"members\":[{\"name\":\"a b\"},{\"name\":\"x y\"}]}";
    }

    @Override
    protected String expectedXml()
    {
        return "<foo><members><member><name>a b</name></member><member><name>x y</name></member></members></foo>";
    }
}