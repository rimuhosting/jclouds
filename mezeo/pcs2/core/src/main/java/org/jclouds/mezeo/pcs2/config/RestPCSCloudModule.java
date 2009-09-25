/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.mezeo.pcs2.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSCloud;
import org.jclouds.mezeo.pcs2.PCSCloud.Response;
import org.jclouds.mezeo.pcs2.endpoints.Contacts;
import org.jclouds.mezeo.pcs2.endpoints.Metacontainers;
import org.jclouds.mezeo.pcs2.endpoints.Projects;
import org.jclouds.mezeo.pcs2.endpoints.Recyclebin;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.endpoints.Shares;
import org.jclouds.mezeo.pcs2.endpoints.Tags;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the PCSCloudModule authentication service connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RestPCSCloudModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new JaxrsModule());
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   protected Response provideCloudResponse(RestClientFactory factory, @PCS URI authenticationUri) {
      return factory.create(PCSCloud.class).authenticate();
   }

   @Provides
   @Singleton
   @PCS
   protected URI provideAuthenticationURI(
            @Named(PCSConstants.PROPERTY_PCS2_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @Contacts
   protected URI provideContactsUrl(Response response) {
      return response.getContactsUrl();
   }

   @Provides
   @Singleton
   @Metacontainers
   protected URI provideMetacontainersUrl(Response response) {
      return response.getMetacontainersUrl();
   }

   @Provides
   @Singleton
   @Projects
   protected URI provideProjectsUrl(Response response) {
      return response.getProjectsUrl();
   }

   @Provides
   @Singleton
   @Recyclebin
   protected URI provideRecyclebinUrl(Response response) {
      return response.getRecyclebinUrl();
   }

   @Provides
   @Singleton
   @RootContainer
   protected URI provideRootContainerUrl(Response response) {
      return response.getRootContainerUrl();
   }

   @Provides
   @Singleton
   @Shares
   protected URI provideSharesUrl(Response response) {
      return response.getSharesUrl();
   }

   @Provides
   @Singleton
   @Tags
   protected URI provideTagsUrl(Response response) {
      return response.getTagsUrl();
   }

   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO retry on 401 by AuthenticateRequest.update()
   }

}