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
package org.jclouds.rackspace.cloudservers.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindRebootTypeToJsonEntity}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.BindRebootTypeToJsonEntityTest")
public class BindRebootTypeToJsonEntityTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPostIsIncorrect() {
      BindRebootTypeToJsonEntity binder = new BindRebootTypeToJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, ImmutableMap.of("adminPass", "foo"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeRebootType() {
      BindRebootTypeToJsonEntity binder = new BindRebootTypeToJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test
   public void testHard() {
      BindRebootTypeToJsonEntity binder = new BindRebootTypeToJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, RebootType.HARD);
      assertEquals("{\"reboot\":{\"type\":\"HARD\"}}", request.getEntity());
   }

   @Test
   public void testSoft() {
      BindRebootTypeToJsonEntity binder = new BindRebootTypeToJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, RebootType.SOFT);
      assertEquals("{\"reboot\":{\"type\":\"SOFT\"}}", request.getEntity());
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindRebootTypeToJsonEntity binder = new BindRebootTypeToJsonEntity();
      injector.injectMembers(binder);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, null);
   }
}
