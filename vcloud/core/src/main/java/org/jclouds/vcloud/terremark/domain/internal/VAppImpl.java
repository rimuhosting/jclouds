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
package org.jclouds.vcloud.terremark.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.rest.domain.Link;
import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.vcloud.terremark.domain.VApp;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VAppImpl extends NamedLinkImpl implements VApp {

   private final int status;
   private final int size;
   private final Link vDC;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public VAppImpl(String name, String type, URI location, int status, int size, Link vDC) {
      super(name, type, location);
      this.status = status;
      this.size = size;
      this.vDC = checkNotNull(vDC, "vDC");
   }

   public int getStatus() {
      return status;
   }

   public int getSize() {
      return size;
   }

   public Link getVDC() {
      return vDC;
   }

}