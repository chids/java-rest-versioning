package com.github.chids.restversioning;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.github.chids.restversioning.model.Group;
import com.github.chids.restversioning.model.Person;

@Path("")
public class Service
{
    @GET
    @Path("person")
    public Person getPerson()
    {
        return new Person("mårten", "gustafson");
    }

    @GET
    @Path("group")
    public Group getGroup()
    {
        return new Group("authors", getPerson(), getPerson());
    }
}