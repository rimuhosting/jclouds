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
package org.jclouds.rackspace.cloudfiles.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;

import com.google.common.base.Function;

/**
 * This parses {@link AccountMetadata} from HTTP headers.
 * 
 * @author James Murty
 */
public class ParseAccountMetadataResponseFromHeaders implements
         Function<HttpResponse, AccountMetadata> {

   /**
    * parses the http response headers to create a new {@link AccountMetadata} object.
    */
   public AccountMetadata apply(final HttpResponse from) {
      String bytesString = checkNotNull(
            from.getFirstHeaderOrNull(CloudFilesHeaders.ACCOUNT_BYTES_USED),
               CloudFilesHeaders.ACCOUNT_BYTES_USED);
      String containersCountString = checkNotNull(
            from.getFirstHeaderOrNull(CloudFilesHeaders.ACCOUNT_CONTAINER_COUNT),
               CloudFilesHeaders.ACCOUNT_CONTAINER_COUNT);
      return new AccountMetadata(
            Long.parseLong(containersCountString), 
            Long.parseLong(bytesString));
   }
}