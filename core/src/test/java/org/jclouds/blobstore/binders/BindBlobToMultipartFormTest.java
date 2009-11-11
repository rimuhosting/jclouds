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
package org.jclouds.blobstore.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "blobstore.BindBlobToMultipartFormTest")
public class BindBlobToMultipartFormTest {
   private static Factory blobProvider;

   public static String BOUNDRY = BindBlobToMultipartForm.BOUNDARY;
   public static final String EXPECTS;
   public static final Blob TEST_BLOB;

   static {
      blobProvider = Guice.createInjector(new BlobStoreObjectModule()).getInstance(
               Blob.Factory.class);
      StringBuilder builder = new StringBuilder("--");
      addData(BOUNDRY, "hello", builder);
      builder.append("--").append(BOUNDRY).append("--").append("\r\n");
      EXPECTS = builder.toString();
      TEST_BLOB = blobProvider.create(null);
      TEST_BLOB.getMetadata().setName("hello");
      TEST_BLOB.setData("hello");
      TEST_BLOB.getMetadata().setContentType(MediaType.TEXT_PLAIN);
   }

   public void testSinglePart() throws IOException {

      assertEquals(EXPECTS.length(), 131);

      BindBlobToMultipartForm binder = new BindBlobToMultipartForm();

      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost:8001"));
      binder.bindToRequest(request, TEST_BLOB);

      assertEquals(Utils.toStringAndClose((InputStream) request.getEntity()), EXPECTS);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), 131 + "");

      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE),
               "multipart/form-data; boundary=" + BOUNDRY);
   }

   private static void addData(String boundary, String data, StringBuilder builder) {
      builder.append(boundary).append("\r\n");
      builder.append("Content-Disposition").append(": ").append(
               "form-data; name=\"hello\"; filename=\"hello\"").append("\r\n");
      builder.append("Content-Type").append(": ").append("text/plain").append("\r\n");
      builder.append("\r\n");
      builder.append(data).append("\r\n");
   }

}
