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
package org.jclouds.aws.ec2.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.http.functions.config.ParserModule.CDateTimeAdapter;
import org.jclouds.http.functions.config.ParserModule.DateTimeAdapter;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the EC2 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class EC2ContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(DateTimeAdapter.class).to(CDateTimeAdapter.class);
   }

   @Provides
   @Singleton
   RestContext<EC2AsyncClient, EC2Client> provideContext(Closer closer, EC2AsyncClient defaultApi,
            EC2Client synchApi, @EC2 URI endPoint,
            @Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String account) {
      return new RestContextImpl<EC2AsyncClient, EC2Client>(closer, defaultApi, synchApi, endPoint,
               account);
   }

}