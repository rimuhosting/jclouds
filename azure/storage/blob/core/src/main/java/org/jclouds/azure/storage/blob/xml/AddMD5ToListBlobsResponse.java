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
package org.jclouds.azure.storage.blob.xml;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.AzureBlobUtil;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.util.DateService;

/**
 * adds the Content-MD5 value to the blob metadata.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135734.aspx#samplerequestandresponse" />
 * @author Adrian Cole
 */
public class AddMD5ToListBlobsResponse extends ContainerNameEnumerationResultsHandler {

   private final AzureBlobUtil util;

   @Inject
   public AddMD5ToListBlobsResponse(AzureBlobUtil util, DateService dateParser) {
      super(dateParser);
      this.util = util;
   }

   @Override
   public ListBlobsResponse getResult() {
      ListBlobsResponse response = super.getResult();
      checkNotNull(response.getContainerUrl(), "containerUrl");
      for (BlobMetadata md : response) {
         checkNotNull(md.getName(), "key");
         md.setContentMD5(util.getMD5(response.getContainerUrl(), md.getName()));
      }
      return response;
   }

}
