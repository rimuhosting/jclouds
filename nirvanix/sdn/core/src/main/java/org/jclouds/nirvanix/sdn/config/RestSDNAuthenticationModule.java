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
package org.jclouds.nirvanix.sdn.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.nirvanix.sdn.SDN;
import org.jclouds.nirvanix.sdn.SDNAuthentication;
import org.jclouds.nirvanix.sdn.SDNConnection;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the SDN authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RestSDNAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   @SDN
   protected URI provideAuthenticationURI(@Named(SDNConstants.PROPERTY_SDN_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @SessionToken
   protected String provideSessionToken(RestClientFactory factory,
            @Named(SDNConstants.PROPERTY_SDN_APPKEY) String appKey,
            @Named(SDNConstants.PROPERTY_SDN_USERNAME) String username,
            @Named(SDNConstants.PROPERTY_SDN_PASSWORD) String password) {
      return factory.create(SDNAuthentication.class).authenticate(appKey, username, password);
   }

   @Provides
   protected SDNConnection provideConnection(RestClientFactory factory) {
      return factory.create(SDNConnection.class);
   }
   
   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO retry on 401 by AuthenticateRequest.update()
   }

}