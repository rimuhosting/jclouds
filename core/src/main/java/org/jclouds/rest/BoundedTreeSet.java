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
package org.jclouds.rest;

import java.util.SortedSet;
import java.util.TreeSet;

public class BoundedTreeSet<T> extends TreeSet<T> implements BoundedSortedSet<T> {

   /** The serialVersionUID */
   private static final long serialVersionUID = -7133632087734650835L;
   protected final String prefix;
   protected final String marker;
   protected final int maxResults;

   public BoundedTreeSet(SortedSet<T> contents, String prefix, String marker, int maxResults) {
      this.addAll(contents);
      this.prefix = prefix;
      this.marker = marker;
      this.maxResults = maxResults;
   }

   public String getPrefix() {
      return prefix;
   }

   public String getMarker() {
      return marker;
   }

   public int getMaxResults() {
      return maxResults;
   }

}