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
package org.jclouds.atmosonline.saas.domain;

import org.joda.time.DateTime;

/**
 * Metadata of a Atmos Online object
 * 
 * @author Adrian Cole
 */
public class SystemMetadata extends DirectoryEntry {

   private final DateTime atime;
   private final DateTime ctime;
   private final String gid;
   private final DateTime itime;
   private final DateTime mtime;
   private final int nlink;
   private final String policyname;
   private final long size;
   private final String uid;

   public SystemMetadata(DateTime atime, DateTime ctime, String gid, DateTime itime,
            DateTime mtime, int nlink, String objectid, String objname, String policyname,
            long size, FileType type, String uid) {
      super(objectid, type, objname);
      this.atime = atime;
      this.ctime = ctime;
      this.gid = gid;
      this.itime = itime;
      this.mtime = mtime;
      this.nlink = nlink;
      this.policyname = policyname;
      this.size = size;
      this.uid = uid;
   }

   public String getGroupID() {
      return gid;
   }

   public int getHardLinkCount() {
      return nlink;
   }

   public DateTime getInceptionTime() {
      return itime;
   }

   public DateTime getLastAccessTime() {
      return atime;
   }

   public DateTime getLastMetadataModification() {
      return mtime;
   }

   public DateTime getLastUserDataModification() {
      return ctime;
   }

   public String getPolicyName() {
      return policyname;
   }

   public long getSize() {
      return size;
   }

   public String getUserID() {
      return uid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((atime == null) ? 0 : atime.hashCode());
      result = prime * result + ((ctime == null) ? 0 : ctime.hashCode());
      result = prime * result + ((gid == null) ? 0 : gid.hashCode());
      result = prime * result + ((itime == null) ? 0 : itime.hashCode());
      result = prime * result + ((mtime == null) ? 0 : mtime.hashCode());
      result = prime * result + nlink;
      result = prime * result + ((policyname == null) ? 0 : policyname.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((uid == null) ? 0 : uid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      SystemMetadata other = (SystemMetadata) obj;
      if (atime == null) {
         if (other.atime != null)
            return false;
      } else if (!atime.equals(other.atime))
         return false;
      if (ctime == null) {
         if (other.ctime != null)
            return false;
      } else if (!ctime.equals(other.ctime))
         return false;
      if (gid == null) {
         if (other.gid != null)
            return false;
      } else if (!gid.equals(other.gid))
         return false;
      if (itime == null) {
         if (other.itime != null)
            return false;
      } else if (!itime.equals(other.itime))
         return false;
      if (mtime == null) {
         if (other.mtime != null)
            return false;
      } else if (!mtime.equals(other.mtime))
         return false;
      if (nlink != other.nlink)
         return false;
      if (policyname == null) {
         if (other.policyname != null)
            return false;
      } else if (!policyname.equals(other.policyname))
         return false;
      if (size != other.size)
         return false;
      if (uid == null) {
         if (other.uid != null)
            return false;
      } else if (!uid.equals(other.uid))
         return false;
      return true;
   }

}