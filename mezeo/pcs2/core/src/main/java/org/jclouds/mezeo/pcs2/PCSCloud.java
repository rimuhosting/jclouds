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
package org.jclouds.mezeo.pcs2;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.endpoints.Contacts;
import org.jclouds.mezeo.pcs2.endpoints.Metacontainers;
import org.jclouds.mezeo.pcs2.endpoints.Projects;
import org.jclouds.mezeo.pcs2.endpoints.Recyclebin;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.endpoints.Shares;
import org.jclouds.mezeo.pcs2.endpoints.Tags;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler;
import org.jclouds.rest.Endpoint;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.XMLResponseParser;

/**
 * Provides URIs to PCS services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Endpoint(PCS.class)
public interface PCSCloud {

   public interface Response {
      @RootContainer
      URI getRootContainerUrl();

      @Contacts
      URI getContactsUrl();

      @Shares
      URI getSharesUrl();

      @Projects
      URI getProjectsUrl();

      @Metacontainers
      URI getMetacontainersUrl();

      @Recyclebin
      URI getRecyclebinUrl();

      @Tags
      URI getTagsUrl();
   }

   @GET
   @XMLResponseParser(CloudXlinkHandler.class)
   @Path("/")
   Response authenticate();
}
