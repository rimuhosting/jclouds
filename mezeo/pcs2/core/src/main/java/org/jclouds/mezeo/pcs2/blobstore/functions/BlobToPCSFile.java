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
package org.jclouds.mezeo.pcs2.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.mezeo.pcs2.domain.PCSFile;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToPCSFile implements Function<Blob, PCSFile> {
   private final BlobToFileInfo blob2ObjectMd;
   private final PCSFile.Factory objectProvider;

   @Inject
   BlobToPCSFile(BlobToFileInfo blob2ObjectMd, PCSFile.Factory objectProvider) {
      this.blob2ObjectMd = blob2ObjectMd;
      this.objectProvider = objectProvider;
   }

   public PCSFile apply(Blob from) {
      PCSFile object = objectProvider.create(blob2ObjectMd.apply(from.getMetadata()));
      if (from.getContentLength() != null)
         object.setContentLength(from.getContentLength());
      object.setData(from.getData());
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}