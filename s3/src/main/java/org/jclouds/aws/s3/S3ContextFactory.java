/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import static org.jclouds.aws.s3.S3Constants.*;

/**
 * // TODO: Adrian: Document return getConnection!
 * 
 * @author Adrian Cole
 */
public class S3ContextFactory {

    public static final Properties DEFAULT_PROPERTIES;

    static {
	DEFAULT_PROPERTIES = new Properties();
	DEFAULT_PROPERTIES.setProperty(PROPERTY_HTTP_ADDRESS,
		"s3.amazonaws.com");
	DEFAULT_PROPERTIES.setProperty(PROPERTY_HTTP_PORT, "443");
	DEFAULT_PROPERTIES.setProperty(PROPERTY_HTTP_SECURE, "true");
	DEFAULT_PROPERTIES
		.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, "75");
	DEFAULT_PROPERTIES.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, "2");
	DEFAULT_PROPERTIES.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS,
		"1");
	DEFAULT_PROPERTIES.setProperty(PROPERTY_POOL_IO_WORKER_THREADS, "2");
	DEFAULT_PROPERTIES.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, "12");
    }

    public static S3Context createS3Context(String awsAccessKeyId,
	    String awsSecretAccessKey, Module... modules) {
	Properties properties = new Properties(DEFAULT_PROPERTIES);
	properties.setProperty(PROPERTY_AWS_ACCESSKEYID, awsAccessKeyId);
	properties
		.setProperty(PROPERTY_AWS_SECRETACCESSKEY, awsSecretAccessKey);
	return createS3Context(properties, modules);
    }

    public static S3Context createS3Context(String awsAccessKeyId,
	    String awsSecretAccessKey, boolean isSecure, Module... modules) {
	Properties properties = new Properties(DEFAULT_PROPERTIES);
	properties.setProperty(PROPERTY_AWS_ACCESSKEYID, awsAccessKeyId);
	properties
		.setProperty(PROPERTY_AWS_SECRETACCESSKEY, awsSecretAccessKey);
	properties
		.setProperty(PROPERTY_HTTP_SECURE, Boolean.toString(isSecure));
	if (!isSecure)
	    properties.setProperty(PROPERTY_HTTP_PORT, "80");
	return createS3Context(properties, modules);
    }

    public static S3Context createS3Context(String awsAccessKeyId,
	    String awsSecretAccessKey, boolean isSecure, String server,
	    Module... modules) {
	Properties properties = new Properties(DEFAULT_PROPERTIES);
	properties.setProperty(PROPERTY_AWS_ACCESSKEYID, awsAccessKeyId);
	properties
		.setProperty(PROPERTY_AWS_SECRETACCESSKEY, awsSecretAccessKey);
	properties
		.setProperty(PROPERTY_HTTP_SECURE, Boolean.toString(isSecure));
	properties.setProperty(PROPERTY_HTTP_ADDRESS, server);
	if (!isSecure)
	    properties.setProperty(PROPERTY_HTTP_PORT, "80");
	return createS3Context(properties, modules);
    }

    public static S3Context createS3Context(String awsAccessKeyId,
	    String awsSecretAccessKey, boolean isSecure, String server,
	    int port, Module... modules) {
	Properties properties = new Properties(DEFAULT_PROPERTIES);
	properties.setProperty(PROPERTY_AWS_ACCESSKEYID, awsAccessKeyId);
	properties
		.setProperty(PROPERTY_AWS_SECRETACCESSKEY, awsSecretAccessKey);
	properties
		.setProperty(PROPERTY_HTTP_SECURE, Boolean.toString(isSecure));
	properties.setProperty(PROPERTY_HTTP_ADDRESS, server);
	properties.setProperty(PROPERTY_HTTP_PORT, port + "");
	return createS3Context(properties, modules);
    }

    public static S3Context createS3Context(Properties properties,
	    Module... modules) {
	return getInjector(properties, modules).getInstance(S3Context.class);
    }

    /**
     * Bind the given properties and install the list of modules. If no modules
     * are specified, install the default
     * {@link JavaUrlHttpFutureCommandClientModule}
     * 
     * @param properties
     *            - contains constants used by jclouds
     *            {@link #DEFAULT_PROPERTIES}
     * @param httpModules
     *            - modules that must bind {@link HttpFutureCommandClient} if
     *            specified
     * */
    public static Injector getInjector(final Properties properties,
	    Module... httpModules) {
	final List<? extends Module> modules = httpModules.length != 0 ? Arrays
		.asList(httpModules) : Collections
		.singletonList(new JavaUrlHttpFutureCommandClientModule());
	return Guice.createInjector(new AbstractModule() {
	    @Override
	    protected void configure() {
		Names.bindProperties(binder(), properties);
		for (Module module : modules)
		    install(module);
	    }
	}, new S3ContextModule());
    }
}
