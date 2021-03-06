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
package org.jclouds.azure.storage.blob.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;

import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableBlobProperties;
import org.jclouds.azure.storage.blob.domain.internal.ListableBlobPropertiesImpl;
import org.jclouds.azure.storage.blob.domain.internal.TreeSetListBlobsResponse;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code ContainerNameEnumerationResultsHandlerTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.ContainerNameEnumerationResultsHandlerTest")
public class ContainerNameEnumerationResultsHandlerTest extends BaseHandlerTest {
   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/blob/test_list_blobs.xml");
      SortedSet<ListableBlobProperties> contents = Sets.newTreeSet();
      contents.add(new ListableBlobPropertiesImpl("blob1.txt", URI
               .create("http://myaccount.blob.core.windows.net/mycontainer/blob1.txt"), dateService
               .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"), "0x8CAE7D55D050B8B", 8,
               "text/plain; charset=UTF-8", null, null));
      contents.add(new ListableBlobPropertiesImpl("blob2.txt", URI
               .create("http://myaccount.blob.core.windows.net/mycontainer/blob2.txt"), dateService
               .rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"), "0x8CAE7D55CF6C339", 14,
               "text/plain; charset=UTF-8", null, null));
      contents.add(new ListableBlobPropertiesImpl("newblob1.txt", URI
               .create("http://myaccount.blob.core.windows.net/mycontainer/newblob1.txt"),
               dateService.rfc822DateParse("Thu, 18 Sep 2008 18:41:57 GMT"), "0x8CAE7D55CF6C339",
               25, "text/plain; charset=UTF-8", null, null));

      ListBlobsResponse list = new TreeSetListBlobsResponse(contents, URI
               .create("http://myaccount.blob.core.windows.net/mycontainer"),

      "myfolder/", null, 4, "newblob2.txt", "/", Sets.<String> newTreeSet());

      ListBlobsResponse result = (ListBlobsResponse) factory.create(
               injector.getInstance(ContainerNameEnumerationResultsHandler.class)).parse(is);

      assertEquals(result, list);
   }
}
