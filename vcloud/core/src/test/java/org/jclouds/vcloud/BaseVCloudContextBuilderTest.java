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
package org.jclouds.vcloud;

import static org.easymock.classextension.EasyMock.createMock;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.config.BaseVCloudContextModule;
import org.jclouds.vcloud.config.BaseVCloudRestClientModule;
import org.jclouds.vcloud.endpoints.Org;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in BaseVCloudContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BaseVCloudContextBuilderTest")
public class BaseVCloudContextBuilderTest {
   VCloudClient connection = createMock(VCloudClient.class);

   @ConfiguresRestClient
   private final class StubClientModule extends AbstractModule {
      @Override
      protected void configure() {
         bind(URI.class).annotatedWith(Org.class).toInstance(URI.create("http://org"));
         bind(VCloudClient.class).toInstance(connection);
      }
   }

   public void testNewBuilder() {
      RestContextBuilder<VCloudClient> builder = builder();

      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_ENDPOINT),
               "http://localhost");
      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_USER), "id");
      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_KEY), "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_SESSIONINTERVAL), "540");
   }

   public void testBuildContext() {
      RestContextBuilder<VCloudClient> builder = builder();
      RestContext<VCloudClient> context = builder.buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getApi(), connection);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("http://org"));
   }

   public BaseVCloudContextBuilder builder() {
      return new BaseVCloudContextBuilder(new VCloudPropertiesBuilder(URI
               .create("http://localhost"), "id", "secret").build())
               .withModules(new StubClientModule());
   }

   public void testBuildInjector() {
      RestContextBuilder<VCloudClient> builder = builder();
      Injector i = builder.buildInjector();
      assert i.getInstance(Key.get(URI.class, Org.class)) != null;
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<VCloudClient>>() {
      })) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      BaseVCloudContextBuilder builder = builder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), BaseVCloudContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      BaseVCloudContextBuilder builder = builder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), BaseVCloudRestClientModule.class);
   }

}
