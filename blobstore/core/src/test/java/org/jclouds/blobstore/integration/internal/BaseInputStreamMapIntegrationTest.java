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
package org.jclouds.blobstore.integration.internal;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
public class BaseInputStreamMapIntegrationTest<S, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseMapIntegrationTest<S, C, M, B, InputStream> {

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         map.putAll(this.fiveInputs);
         // this will cause us to block until the bucket updates.
         assertEventuallyKeySize(map, 5);
         Collection<InputStream> values = map.values();
         assertEquals(values.size(), 5);
         Set<String> valuesAsString = new HashSet<String>();
         for (InputStream stream : values) {
            valuesAsString.add(Utils.toStringAndClose(stream));
         }
         valuesAsString.removeAll(fiveStrings.values());
         assert valuesAsString.size() == 0;
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         putString(map, "one", "two");
         InputStream old = map.remove("one");
         assertEquals(Utils.toStringAndClose(old), "two");
         old = map.remove("one");
         assert old == null;
         old = map.get("one");
         assert old == null;
         assertEventuallyKeySize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   @Test(groups = { "integration", "live" })
   public void testEntrySet() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         ((InputStreamMap) map).putAllStrings(this.fiveStrings);
         // this will cause us to block until the bucket updates.
         assertEventuallyKeySize(map, 5);
         Set<Entry<String, InputStream>> entries = map.entrySet();
         assertEquals(entries.size(), 5);
         for (Entry<String, InputStream> entry : entries) {
            assertEquals(fiveStrings.get(entry.getKey()), IOUtils.toString(entry.getValue()));
            entry.setValue(IOUtils.toInputStream(""));
         }
         assertEventuallyMapSize(map, 5);
         for (InputStream value : map.values()) {
            assertEquals(IOUtils.toString(value), "");
         }
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testContainsStringValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         ((InputStreamMap) map).putString("one", "apple");
         assertEventuallyContainsValue(map, fiveStrings.get("one"));
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testContainsFileValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         ((InputStreamMap) map).putString("one", "apple");
         assertEventuallyContainsValue(map, fiveFiles.get("one"));
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testContainsInputStreamValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         ((InputStreamMap) map).putString("one", "apple");
         assertEventuallyContainsValue(map, this.fiveInputs.get("one"));
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testContainsBytesValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         ((InputStreamMap) map).putString("one", "apple");
         assertEventuallyContainsValue(map, this.fiveBytes.get("one"));
      } finally {
         returnContainer(bucketName);
      }
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);
         map.putAll(this.fiveInputs);
         assertEventuallyMapSize(map, 5);
         assertEventuallyKeySetEquals(map, new TreeSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testPutAllBytes() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         ((InputStreamMap) map).putAllBytes(this.fiveBytes);
         assertEventuallyMapSize(map, 5);
         assertEventuallyKeySetEquals(map, new TreeSet<String>(fiveBytes.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testPutAllFiles() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         ((InputStreamMap) map).putAllFiles(this.fiveFiles);
         assertEventuallyMapSize(map, 5);
         assertEventuallyKeySetEquals(map, new TreeSet<String>(fiveFiles.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testPutAllStrings() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         ((InputStreamMap) map).putAllStrings(this.fiveStrings);
         assertEventuallyMapSize(map, 5);
         assertEventuallyKeySetEquals(map, new TreeSet<String>(fiveStrings.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testPutString() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         InputStream old = ((InputStreamMap) map).putString("one", "apple");
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = ((InputStreamMap) map).putString("one", "bear");
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(bucketName);
      }
   }

   void getOneReturnsAppleAndOldValueIsNull(Map<String, InputStream> map, InputStream old)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
      assert old == null;
      assertEquals(Utils.toStringAndClose(map.get("one")), "apple");
      assertEventuallyMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(Map<String, InputStream> map, InputStream oldValue)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
      assertEquals(Utils.toStringAndClose(map.get("one")), "bear");
      assertEquals(Utils.toStringAndClose(oldValue), "apple");
      assertEventuallyMapSize(map, 1);
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testPutFile() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         InputStream old = ((InputStreamMap) map).putFile("one", fiveFiles.get("one"));
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = ((InputStreamMap) map).putFile("one", fiveFiles.get("two"));
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "integration", "live" })
   public void testPutBytes() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         InputStream old = ((InputStreamMap) map).putBytes("one", "apple".getBytes());
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = ((InputStreamMap) map).putBytes("one", "bear".getBytes());
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, bucketName);

         InputStream old = map.put("one", IOUtils.toInputStream("apple"));
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = map.put("one", IOUtils.toInputStream("bear"));
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(bucketName);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void putString(Map<String, InputStream> map, String key, String value)
            throws InterruptedException, ExecutionException, TimeoutException {
      ((InputStreamMap) map).putString(key, value);
   }

   @SuppressWarnings("unchecked")
   protected Map<String, InputStream> createMap(BlobStoreContext context, String bucket) {
      InputStreamMap map = context.createInputStreamMap(bucket);
      return (Map<String, InputStream>) map;
   }
}
