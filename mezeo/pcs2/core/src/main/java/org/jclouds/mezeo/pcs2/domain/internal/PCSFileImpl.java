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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.mezeo.pcs2.domain.MutableFileInfo;
import org.jclouds.mezeo.pcs2.domain.PCSFile;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link PCSFile}.
 * 
 * @author Adrian Cole
 */
public class PCSFileImpl implements PCSFile, Comparable<PCSFile> {

   private final CalculateSize calculateSize;
   private final MutableFileInfo metadata;
   private Object data;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();
   private Long contentLength;

   @Inject
   public PCSFileImpl(CalculateSize calculateSize, MutableFileInfo metadata) {
      this.calculateSize = calculateSize;
      this.metadata = metadata;
   }

   /**
    * {@inheritDoc}
    */
   public Object getData() {
      return data;
   }

   /**
    * {@inheritDoc}
    */
   public void setData(Object data) {
      this.data = checkNotNull(data, "data");
      if (getContentLength() == null) {
         Long size = calculateSize.apply(data);
         if (size != null)
            this.setContentLength(size);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * {@inheritDoc}
    */
   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * {@inheritDoc}
    */
   public MutableFileInfo getMetadata() {
      return metadata;
   }

   /**
    * {@inheritDoc}
    */
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   /**
    * {@inheritDoc}
    */
   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(PCSFile o) {
      if (getMetadata().getName() == null)
         return -1;
      return (this == o) ? 0 : getMetadata().getName().compareTo(o.getMetadata().getName());
   }
}
