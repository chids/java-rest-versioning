package com.github.chids.restversioning.configuration;

import javax.servlet.annotation.WebFilter;

import com.google.inject.servlet.GuiceFilter;

/**
 * This appears to be required in order for servlet 3 annotations to work properly with Guice and Jersey.
 * TODO: Figure out why and if really needed
 */
@WebFilter("/*")
public class Filter extends GuiceFilter
{}