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
package org.jclouds.http;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.http.options.GetOptions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests for functionality all HttpCommandExecutorServices must express. These tests will operate
 * against an in-memory http engine, so as to ensure end-to-end functionality works.
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10)
public abstract class BaseHttpCommandExecutorServiceTest extends BaseJettyTest {

   @Test(invocationCount = 50, timeOut = 5000)
   public void testRequestFilter() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.downloadFilter("", "filterme").trim(), "test");
   }

   // TODO: filtering redirect test

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetStringWithHeader() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.download("", "test").trim(), "test");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetString() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.download("").trim(), XML);
   }

   @DataProvider(name = "gets")
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "sp ace" }, { "unic₪de" },
               { "qu?stion" } };
   }

   @Test(invocationCount = 50, timeOut = 5000, dataProvider = "gets")
   public void testGetStringSynch(String uri) throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // TODO why need trim?
      assertEquals(client.synch(uri).trim(), XML);
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetException() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.downloadException("", GetOptions.Builder.tail(1)).trim(), "foo");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetSynchException() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.synchException("", "").trim(), "foo");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetStringRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.download("redirect").trim(), XML2);
   }

   @Test(enabled = false, invocationCount = 50, timeOut = 5000)
   public void testGetStringPermanentRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // GetString get = factory.createGetString("permanentredirect");
      // assert get != null;
      // client.submit(get);
      // assertEquals(get.get(10, TimeUnit.SECONDS).trim(), XML2);
      // TODO assert misses are only one, as permanent redirects paths should be remembered.
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testPost() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assertEquals(client.post("", "foo").trim(), "fooPOST");
   }

   @Test(invocationCount = 50, timeOut = 10000)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      try {
         assertEquals(client.postAsInputStream("", "foo").trim(), "fooPOST");
      } catch (Exception e) {
         postFailures.incrementAndGet();
      }
   }

   protected AtomicInteger postFailures = new AtomicInteger();

   @BeforeTest
   void resetCounters() {
      postFailures.set(0);
   }

   @Test(dependsOnMethods = "testPostAsInputStream")
   public void testPostResults() {
      // failures happen when trying to replay inputstreams
      assert postFailures.get() > 0;
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testPostBinder() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.postJson("", "foo").trim(), "{\"key\":\"foo\"}POST");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testPut() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assertEquals(client.upload("", "foo").trim(), "fooPUT");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testPutRedirect() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.upload("redirect", "foo").trim(), "fooPUTREDIRECT");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testKillRobotSlowly() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.action("robot", "kill", ImmutableMap.of("death", "slow")).trim(),
               "robot->kill:{death=slow}");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testHead() throws MalformedURLException, ExecutionException, InterruptedException,
            TimeoutException {
      assert client.exists("");
   }

   @Test(invocationCount = 50, timeOut = 5000)
   public void testGetAndParseSax() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      assertEquals(client.downloadAndParse(""), "whoppers");
   }
}
