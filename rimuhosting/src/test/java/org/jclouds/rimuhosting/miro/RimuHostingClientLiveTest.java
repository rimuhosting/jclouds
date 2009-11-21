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
import static org.testng.Assert.assertEquals;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import org.jclouds.rimuhosting.miro.data.NewInstance;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.Instance;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code RimuHostingClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "rimuhosting.RimuHostingClientLiveTest")
public class RimuHostingClientLiveTest {

   private RimuHostingClient connection;

   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = RimuHostingContextFactory.createContext(user, password, new Log4JLoggingModule())
              .getApi();
   }

   @Test
   public void testListImages() {
	   assertEquals(connection.getImageList().first().getId(), "lenny");
   }

   @Test
   public void testListInstances() {
	   assertEquals(connection.getInstanceList().first().getName(), "deltacloud.rimuhosting.com");  
   }
  
   @Test
   public void testLifeCycle() {
	   //Get the first image, we dont really care what it is in this test.
	   Image image = connection.getImageList().first();
	   CreateOptions createOptions = new CreateOptions("test.jclouds.org", "testpassword123", image);
	   //New instance data. Using miro1 becuase we want it to go to the right datacenter.
	   NewInstance newInstance = new NewInstance(createOptions, "MIRO1");
	   Instance instance = connection.createInstance(newInstance);
	   assertEquals(instance.getName(), "test.jclouds.org");
	   
   }
}