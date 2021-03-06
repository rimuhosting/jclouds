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
package org.jclouds.mezeo.pcs2.internal;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;

import org.jclouds.mezeo.pcs2.PCSAsyncClient;
import org.jclouds.mezeo.pcs2.domain.ContainerList;
import org.jclouds.mezeo.pcs2.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.options.PutBlockOptions;

/**
 * Implementation of {@link PCSBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubPCSAsyncClient implements PCSAsyncClient {
   public Future<? extends ContainerList> list() {
      throw new UnsupportedOperationException();
   }

   public Future<? extends ContainerList> list(URI container) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> createContainer(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> createContainer(URI parent, String container) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> deleteContainer(URI container) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> deleteFile(URI file) {
      throw new UnsupportedOperationException();
   }

   public Future<InputStream> downloadFile(URI file) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> uploadFile(URI container, PCSFile object) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> createFile(URI container, PCSFile object) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> uploadBlock(URI file, PCSFile object, PutBlockOptions... options) {
      throw new UnsupportedOperationException();
   }

   public PCSFile newFile() {
      throw new UnsupportedOperationException();
   }

   public Future<FileInfoWithMetadata> getFileInfo(URI file) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> addMetadataItemToMap(URI resource, String key, Map<String, String> map) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> putMetadataItem(URI resource, String key, String value) {
      throw new UnsupportedOperationException();
   }

}
