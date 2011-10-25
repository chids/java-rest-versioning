package com.github.chids.restversioning.model;

import java.util.Arrays;

public final class Group
{
    public final String name;
    public final Iterable<Person> members;

    public Group(final String name, final Person... members)
    {
        this.name = name;
        this.members = Arrays.asList(members);
    }
}
