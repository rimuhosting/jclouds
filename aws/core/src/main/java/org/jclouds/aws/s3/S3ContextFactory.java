/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.net.URI;
import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;

import com.google.inject.Module;

/**
 * Creates {@link S3Context} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see S3AsyncClient
 */
public class S3ContextFactory {

   public static RestContext<S3AsyncClient, S3Client> createContext(Properties properties,
            Module... modules) {
      return new S3ContextBuilder(new S3PropertiesBuilder(properties).build()).withModules(modules)
               .buildContext();
   }

   public static RestContext<S3AsyncClient, S3Client> createContext(String awsAccessKeyId,
            String awsSecretAccessKey, Module... modules) {
      return new S3ContextBuilder(new S3PropertiesBuilder(awsAccessKeyId, awsSecretAccessKey)
               .build()).withModules(modules).buildContext();
   }

   public static RestContext<S3AsyncClient, S3Client> createContext(URI endpoint, String awsAccessKeyId,
            String awsSecretAccessKey, Module... modules) {
      return new S3ContextBuilder(new S3PropertiesBuilder(awsAccessKeyId, awsSecretAccessKey)
               .withEndpoint(endpoint).build()).withModules(modules).buildContext();
   }
}
