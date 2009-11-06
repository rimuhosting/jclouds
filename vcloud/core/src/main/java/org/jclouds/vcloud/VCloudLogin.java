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
package org.jclouds.vcloud;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.vcloud.domain.Link;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.functions.ParseLoginResponseFromHeaders;

/**
 * Establishes a context with a VCloud endpoint.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@Endpoint(VCloud.class)
@RequestFilters(BasicAuthentication.class)
public interface VCloudLogin {

   public interface VCloudSession {
      @VCloudToken
      String getVCloudToken();

      @Org
      Map<String, Link> getOrgs();
   }

   /**
    * This request returns a token to use in subsequent requests. After ten minutes of inactivity,
    * the token expires and you have to request a new token with this call.
    */
   @POST
   @ResponseParser(ParseLoginResponseFromHeaders.class)
   @Path("/login")
   @Consumes(MediaType.APPLICATION_XML)
   VCloudSession login();
}
