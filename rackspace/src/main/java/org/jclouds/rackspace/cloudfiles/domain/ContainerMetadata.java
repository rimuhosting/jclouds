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
package org.jclouds.rackspace.cloudfiles.domain;


/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerMetadata implements Comparable<ContainerMetadata> {
   /** The serialVersionUID */
   private static final long serialVersionUID = 2925863715029426128L;
   private String name;
   private long count;
   private long bytes;

   public ContainerMetadata() {
   }

   public ContainerMetadata(String name, long count, long bytes) {
      setName(name);
      setBytes(bytes);
      setCount(count);
   }

   public long getCount() {
      return count;
   }

   public void setCount(long count) {
      this.count = count;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setBytes(long bytes) {
      this.bytes = bytes;
   }

   public long getBytes() {
      return bytes;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (bytes ^ (bytes >>> 32));
      result = prime * result + (int) (count ^ (count >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ContainerMetadata other = (ContainerMetadata) obj;
      if (bytes != other.bytes)
         return false;
      if (count != other.count)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   public int compareTo(ContainerMetadata o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

}
