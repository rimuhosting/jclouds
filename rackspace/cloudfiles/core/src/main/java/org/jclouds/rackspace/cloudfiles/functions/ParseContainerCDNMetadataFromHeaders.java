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
package org.jclouds.rackspace.cloudfiles.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;

import com.google.common.base.Function;

/**
 * This parses {@link AccountMetadata} from HTTP headers.
 * 
 * @author James Murty
 */
public class ParseContainerCDNMetadataFromHeaders implements
         Function<HttpResponse, ContainerCDNMetadata> {

   /**
    * parses the http response headers to create a new {@link ContainerCDNMetadata} object.
    */
   public ContainerCDNMetadata apply(final HttpResponse from) {
      // TODO: The container name is not returned as a header, hopefully one day it will be
      String containerName = null;
      
      String cdnUri = checkNotNull(
            from.getFirstHeaderOrNull(CloudFilesHeaders.CDN_URI),
               CloudFilesHeaders.CDN_URI);
      String cdnTTL = checkNotNull(
            from.getFirstHeaderOrNull(CloudFilesHeaders.CDN_TTL),
               CloudFilesHeaders.CDN_TTL);
      String cdnEnabled = checkNotNull(
            from.getFirstHeaderOrNull(CloudFilesHeaders.CDN_ENABLED),
               CloudFilesHeaders.CDN_ENABLED);
      if (cdnUri == null) {
         // CDN is not, and has never, been enabled for this container.
         return ContainerCDNMetadata.NOT_FOUND;
      } else {
         return new ContainerCDNMetadata(
               containerName,
               Boolean.parseBoolean(cdnEnabled),
               Long.parseLong(cdnTTL), 
               cdnUri);
      }
   }
}