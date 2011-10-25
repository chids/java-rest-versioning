package com.github.chids.restversioning.model;

public final class Person
{
    public final String surname;
    public final String givenName;

    public Person(final String givenName, final String surname)
    {
        this.givenName = givenName.trim();
        this.surname = surname.trim();
    }
}