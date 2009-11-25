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
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.inject.*;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.AddressReachable;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "rimuhosting.RimuHostingComputeClientLiveTest")
public class RimuHostingComputeClientLiveTest {
   RimuHostingComputeClient client;
   RimuHostingClient rhClient;

   private Long id;


   private InetAddress publicIp;
   private Predicate<InetAddress> addressTester;

   @Test
   public void testPowerOn() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String imageId = "lenny";
      String serverName = "test.jclouds.org";
      String planId = "MIRO1";

      id = client.start(serverName, planId, imageId);
      Instance instance = rhClient.getInstance(id);
      assertEquals(imageId, instance.getImageId());
      assertEquals(serverName, instance.getName());
      assertEquals(new Integer(160), instance.getInstanceParameters().getRam());
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (id != null)
         client.destroy(id);
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder(
               account, key).relaxSSLHostname().build()).withModules(new Log4JLoggingModule(),
               new JschSshClientModule(), new AbstractModule() {

                  @Override
                  protected void configure() {
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  private Predicate<InetSocketAddress> socketTester(SocketOpen open) {
                     return new RetryablePredicate<InetSocketAddress>(open, 130, 10,
                              TimeUnit.SECONDS);// make it longer then
                     // default internet
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  private Predicate<InetAddress> addressTester(AddressReachable reachable) {
                     return new RetryablePredicate<InetAddress>(reachable, 60, 5, TimeUnit.SECONDS);
                  }

             
               }).buildInjector();
      client = injector.getInstance(RimuHostingComputeClient.class);
      rhClient = injector.getInstance(RimuHostingClient.class);
      addressTester = injector.getInstance(Key.get(new TypeLiteral<Predicate<InetAddress>>() {
      }));
   }

}