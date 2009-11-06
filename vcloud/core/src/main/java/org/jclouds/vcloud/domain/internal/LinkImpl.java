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
package org.jclouds.vcloud.domain.internal;

import java.net.URI;

import org.jclouds.vcloud.domain.Link;

/**
 * Location of a vCloud resource
 * 
 * @author Adrian Cole
 * 
 */
public class LinkImpl implements Link {
   private final String name;
   private final String type;
   private final URI location;

   public LinkImpl(String name, String type, URI location) {
      this.name = name;
      this.type = type;
      this.location = location;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public URI getLocation() {
      return location;
   }

   @Override
   public String toString() {
      return "Link [name=" + name + ", type=" + type + ", location=" + location + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      LinkImpl other = (LinkImpl) obj;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }
}