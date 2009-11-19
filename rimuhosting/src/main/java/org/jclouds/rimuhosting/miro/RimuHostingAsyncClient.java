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
package org.jclouds.rimuhosting.miro;

import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.MatrixParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.functions.ParseImagesFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParseInstancesFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParsePricingPlansFromJsonResponse;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.SortedSet;
import java.util.concurrent.Future;

/**
 * Provides asynchronous access to RimuHosting via their REST API.
 * <p/>
 *
 * @author Ivan Meredith
 * @see RimuHostingClient
 * @see <a href="TODO: insert URL of client documentation" />
 */
@RequestFilters(RimuHostingAuthentication.class)
@Endpoint(RimuHosting.class)
public interface RimuHostingAsyncClient {

   @GET
   @ResponseParser(ParseImagesFromJsonResponse.class)
   @Path("/distributions")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   Future<SortedSet<Image>> getImageList();

   @GET
   @ResponseParser(ParseInstancesFromJsonResponse.class)
   @MatrixParams(keys = "include_inactive", values = "N")
   @Path("/orders")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   Future<SortedSet<Instance>> getInstanceList();

   @GET @Path("/pricing-plans")
   @MatrixParams(keys = "server-type", values = "VPS")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParsePricingPlansFromJsonResponse.class)
   Future<SortedSet<PricingPlan>> getPricingPlanList();

}
