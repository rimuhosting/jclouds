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
package org.jclouds.mezeo.pcs2.domain.internal;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.mezeo.pcs2.domain.ContainerList;
import org.jclouds.mezeo.pcs2.domain.ResourceInfo;
import org.joda.time.DateTime;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerListImpl extends TreeSet<ResourceInfo> implements ContainerList {

   /** The serialVersionUID */
   private static final long serialVersionUID = 8147941177495755603L;
   private final ResourceInfo info;
   private final Map<String, URI> metadataItems;

   public ContainerListImpl(URI url, String name, DateTime created, boolean inProject,
            DateTime modified, String owner, int version, boolean shared, DateTime accessed,
            long bytes, SortedSet<? extends ResourceInfo> contents, URI tags, URI metadata,
            Map<String, URI> metadataItems, URI parent) {
      this.info = new ResourceInfoImpl(ResourceType.FOLDER, url, name, created, inProject,
               modified, owner, version, shared, accessed, bytes, tags, metadata, parent);
      addAll(contents);
      this.metadataItems = metadataItems;
   }

   public Map<String, URI> getMetadataItems() {
      return metadataItems;
   }

   public DateTime getAccessed() {
      return info.getAccessed();
   }

   public Long getBytes() {
      return info.getBytes();
   }

   public DateTime getCreated() {
      return info.getCreated();
   }

   public URI getMetadata() {
      return info.getMetadata();
   }

   public DateTime getModified() {
      return info.getModified();
   }

   public String getName() {
      return info.getName();
   }

   public String getOwner() {
      return info.getOwner();
   }

   public URI getParent() {
      return info.getParent();
   }

   public URI getTags() {
      return info.getTags();
   }

   public ResourceType getType() {
      return info.getType();
   }

   public URI getUrl() {
      return info.getUrl();
   }

   public Integer getVersion() {
      return info.getVersion();
   }

   public Boolean isInProject() {
      return info.isInProject();
   }

   public Boolean isShared() {
      return info.isShared();
   }

   public int compareTo(ResourceInfo o) {
      return info.compareTo(o);
   }

}
