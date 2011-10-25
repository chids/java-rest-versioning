package com.github.chids.restversioning.serialize;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces({ Version2BodyWriter.XML, Version2BodyWriter.JSON, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public abstract class Version2BodyWriter<T> extends AbstractMessageBodyWriter<T>
{
    public static final String XML = "application/vnd.chids.versioning-v2+xml; charset=utf-8";
    public static final String JSON = "application/vnd.chids.versioning-v2+json; charset=utf-8";

    public Version2BodyWriter(final Class<?> type)
    {
        super(type);
    }
}