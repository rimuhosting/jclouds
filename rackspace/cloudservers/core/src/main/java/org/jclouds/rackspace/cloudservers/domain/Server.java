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
package org.jclouds.rackspace.cloudservers.domain;

import java.util.Map;

import com.google.inject.internal.Maps;

/**
 * 
 * Server.
 * 
 * @author Adrian Cole
 * @since 4.0
 */
public class Server {
   public static final Server NOT_FOUND = new Server(-1, "NOT_FOUND");
   private int id;
   private String name;

   private Map<String, String> metadata = Maps.newHashMap();

   private Addresses addresses;
   private String adminPass;
   private Integer flavorId;
   private String hostId;
   private Integer imageId;

   private Integer progress;
   private Integer sharedIpGroupId;
   private ServerStatus status;

   public Server() {
   }

   public Server(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public void setAddresses(Addresses addresses) {
      this.addresses = addresses;
   }

   public Addresses getAddresses() {
      return addresses;
   }

   public void setAdminPass(String adminPass) {
      this.adminPass = adminPass;
   }

   public String getAdminPass() {
      return adminPass;
   }

   public void setFlavorId(Integer flavorId) {
      this.flavorId = flavorId;
   }

   public Integer getFlavorId() {
      return flavorId;
   }

   public void setHostId(String hostId) {
      this.hostId = hostId;
   }

   /**
    * The Cloud Servers provisioning algorithm has an anti-affinity property that attempts to spread
    * out customer VMs across hosts. Under certain situations, VMs from the same customer may be
    * placed on the same host. hostId represents the host your cloud server runs on and can be used
    * to determine this scenario if it's relevant to your application.
    * <p/>
    * Note: hostId is unique PER ACCOUNT and is not globally unique.
    */
   public String getHostId() {
      return hostId;
   }

   public int getId() {
      return id;
   }

   public void setImageId(Integer imageId) {
      this.imageId = imageId;
   }

   public Integer getImageId() {
      return imageId;
   }

   public String getName() {
      return name;
   }

   public void setProgress(Integer progress) {
      this.progress = progress;
   }

   public Integer getProgress() {
      return progress;
   }

   public void setSharedIpGroupId(Integer sharedIpGroupId) {
      this.sharedIpGroupId = sharedIpGroupId;
   }

   public Integer getSharedIpGroupId() {
      return sharedIpGroupId;
   }

   public void setStatus(ServerStatus status) {
      this.status = status;
   }

   /**
    * Servers contain a status attribute that can be used as an indication of the current server
    * state. Servers with an ACTIVE status are available for use.
    */
   public ServerStatus getStatus() {
      return status;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
      result = prime * result + ((adminPass == null) ? 0 : adminPass.hashCode());
      result = prime * result + ((flavorId == null) ? 0 : flavorId.hashCode());
      result = prime * result + ((hostId == null) ? 0 : hostId.hashCode());
      result = prime * result + id;
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
      result = prime * result + ((progress == null) ? 0 : progress.hashCode());
      result = prime * result + ((sharedIpGroupId == null) ? 0 : sharedIpGroupId.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
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
      Server other = (Server) obj;
      if (addresses == null) {
         if (other.addresses != null)
            return false;
      } else if (!addresses.equals(other.addresses))
         return false;
      if (adminPass == null) {
         if (other.adminPass != null)
            return false;
      } else if (!adminPass.equals(other.adminPass))
         return false;
      if (flavorId == null) {
         if (other.flavorId != null)
            return false;
      } else if (!flavorId.equals(other.flavorId))
         return false;
      if (hostId == null) {
         if (other.hostId != null)
            return false;
      } else if (!hostId.equals(other.hostId))
         return false;
      if (id != other.id)
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (getName() == null) {
         if (other.getName() != null)
            return false;
      } else if (!getName().equals(other.getName()))
         return false;
      if (progress == null) {
         if (other.progress != null)
            return false;
      } else if (!progress.equals(other.progress))
         return false;
      if (sharedIpGroupId == null) {
         if (other.sharedIpGroupId != null)
            return false;
      } else if (!sharedIpGroupId.equals(other.sharedIpGroupId))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Server [addresses=" + addresses + ", isAdminPassSet=" + (adminPass != null)
               + ", flavorId=" + flavorId + ", hostId=" + hostId + ", id=" + id + ", imageId="
               + imageId + ", metadata=" + metadata + ", name=" + getName() + ", progress="
               + progress + ", sharedIpGroupId=" + sharedIpGroupId + ", status=" + status + "]";
   }

   public void setName(String name) {
      this.name = name;
   }

}
