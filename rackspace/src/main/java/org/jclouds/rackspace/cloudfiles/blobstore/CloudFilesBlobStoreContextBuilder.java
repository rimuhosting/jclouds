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
package org.jclouds.rackspace.cloudfiles.blobstore;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_METADATA_PREFIX;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_RETRY;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_TIMEOUT;

import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.config.CloudFilesBlobStoreContextModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.rackspace.config.RackspaceAuthenticationRestModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link CloudFilesBlobStoreContext} or {@link Injector} instances based on the most
 * commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see CloudFilesBlobStoreContext
 */
public class CloudFilesBlobStoreContextBuilder extends
         BlobStoreContextBuilder<CloudFilesAsyncClient, CloudFilesClient> {

   public CloudFilesBlobStoreContextBuilder(Properties props) {
      super(new TypeLiteral<CloudFilesAsyncClient>() {
      }, new TypeLiteral<CloudFilesClient>() {
      }, convert(props));
   }

   private static Properties convert(Properties props) {
      for (Entry<String, String> entry : ImmutableMap.of(PROPERTY_CLOUDFILES_METADATA_PREFIX,
               PROPERTY_USER_METADATA_PREFIX, PROPERTY_CLOUDFILES_RETRY, PROPERTY_BLOBSTORE_RETRY,
               PROPERTY_CLOUDFILES_TIMEOUT, PROPERTY_USER_METADATA_PREFIX).entrySet()) {
         if (props.containsKey(entry.getKey()))
            props.setProperty(entry.getValue(), props.getProperty(entry.getKey()));
      }
      return props;
   }

   @Override
   public CloudFilesBlobStoreContextBuilder withExecutorService(ExecutorService service) {
      return (CloudFilesBlobStoreContextBuilder) super.withExecutorService(service);
   }

   @Override
   public CloudFilesBlobStoreContextBuilder withModules(Module... modules) {
      return (CloudFilesBlobStoreContextBuilder) super.withModules(modules);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new CloudFilesBlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new RackspaceAuthenticationRestModule());
      modules.add(new CloudFilesRestClientModule());
   }
}
