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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all PutObject commands.
 * <p/>
 * Each test uses a different container name, so it should be perfectly fine to run in parallel.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live" }, testName = "blobstore.BlobLiveTest")
public class BaseBlobLiveTest<A, S> extends BaseBlobStoreIntegrationTest<A, S> {

   private static final String sysHttpStreamUrl = System
            .getProperty("jclouds.blobstore.httpstream.url");
   private static final String sysHttpStreamETag = System
            .getProperty("jclouds.blobstore.httpstream.md5");

   @Test
   @Parameters( { "jclouds.blobstore.httpstream.url", "jclouds.blobstore.httpstream.md5" })
   public void testCopyUrl(@Optional String httpStreamUrl, @Optional String httpStreamETag)
            throws Exception {
      httpStreamUrl = checkNotNull(httpStreamUrl != null ? httpStreamUrl : sysHttpStreamUrl,
               "httpStreamUrl");

      httpStreamETag = checkNotNull(httpStreamETag != null ? httpStreamETag : sysHttpStreamETag,
               "httpStreamMd5");

      String key = "hello";

      URL url = new URL(httpStreamUrl);
      byte[] md5 = HttpUtils.fromHexString(httpStreamETag);

      URLConnection connection = url.openConnection();
      int length = connection.getContentLength();
      InputStream input = connection.getInputStream();

      Blob object = newBlob(key);
      object.setData(input);
      object.setContentLength(length);
      object.getMetadata().setContentMD5(md5);
      object.getMetadata().setSize(new Long(length));
      String bucketName = getContainerName();
      try {
         context.getBlobStore().putBlob(bucketName, object);
         assertEquals(context.getBlobStore().blobMetadata(bucketName, key).getContentMD5(), md5);
      } finally {
         returnContainer(bucketName);
      }
   }
}