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
package org.jclouds.vcloud.terremark.domain;

import java.net.InetAddress;
import java.net.URI;

/**
 * @author Adrian Cole
 */
public class Node implements Comparable<Node> {
   private final String id;
   private final String name;
   private final URI location;
   private final InetAddress ipAddress;
   private final int port;
   private final boolean enabled;
   private final String description;

   public Node(String id, String name, URI location, InetAddress ipAddress, int port,
            boolean enabled, String description) {
      this.id = id;
      this.name = name;
      this.location = location;
      this.ipAddress = ipAddress;
      this.port = port;
      this.enabled = enabled;
      this.description = description;
   }

   public int getPort() {
      return port;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public String getDescription() {
      return description;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public URI getLocation() {
      return location;
   }

   public int compareTo(Node that) {
      return (this == that) ? 0 : this.getId().compareTo(that.getId());
   }

   public InetAddress getIpAddress() {
      return ipAddress;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + port;
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
      Node other = (Node) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (enabled != other.enabled)
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (ipAddress == null) {
         if (other.ipAddress != null)
            return false;
      } else if (!ipAddress.equals(other.ipAddress))
         return false;
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
      if (port != other.port)
         return false;
      return true;
   }
}