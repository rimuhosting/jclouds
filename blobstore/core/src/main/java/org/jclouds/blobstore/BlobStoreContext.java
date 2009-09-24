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
package org.jclouds.blobstore;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.cloud.CloudContext;

/**
 * Represents a cloud that has key-value storage functionality.
 * 
 * 
 * @author Adrian Cole
 * 
 */
public interface BlobStoreContext<S extends BlobStore<?, M, B>, M extends BlobMetadata, B extends Blob<M>>
         extends CloudContext<S> {

   /**
    * Creates a <code>Map<String,InputStream></code> view of the specified container.
    * 
    * @param container
    */
   InputStreamMap<M> createInputStreamMap(String container);

   /**
    * Creates a <code>Map<String,B></code> view of the specified container.
    * 
    * @param container
    */
   BlobMap<M, B> createBlobMap(String container);

}