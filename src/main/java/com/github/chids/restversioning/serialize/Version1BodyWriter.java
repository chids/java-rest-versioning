package com.github.chids.restversioning.serialize;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces({ Version1BodyWriter.XML, Version1BodyWriter.JSON, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public abstract class Version1BodyWriter<T> extends AbstractMessageBodyWriter<T>
{
    public static final String XML = "application/vnd.chids.versioning-v1+xml; charset=utf-8";
    public static final String JSON = "application/vnd.chids.versioning-v1+json; charset=utf-8";

    public Version1BodyWriter(final Class<?> type)
    {
        super(type);
    }
}