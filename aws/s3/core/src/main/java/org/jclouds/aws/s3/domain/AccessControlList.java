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
package org.jclouds.aws.s3.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * An Access Control List (ACL) describes the access control settings for a bucket or object in S3.
 * 
 * ACL settings comprise a set of {@link Grant}s, each of which specifies a {@link Permission} that
 * has been granted to a specific {@link Grantee}. If an entity tries to access or modify an item in
 * S3, the operation will be denied unless the item has ACL settings that explicitly permit that
 * entity to perform that action.
 * 
 * 
 * @author James Murty
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
 */
public class AccessControlList {

   private CanonicalUser owner;
   private final List<Grant> grants = new ArrayList<Grant>();

   public void setOwner(CanonicalUser owner) {
      this.owner = owner;
   }

   public CanonicalUser getOwner() {
      return owner;
   }

   /**
    * @return an unmodifiable set of grants represented by this ACL.
    */
   public List<Grant> getGrants() {
      return Collections.unmodifiableList(grants);
   }

   /**
    * @return an unmodifiable set of grantees who have been assigned permissions in this ACL.
    */
   public Set<Grantee> getGrantees() {
      SortedSet<Grantee> grantees = new TreeSet<Grantee>();
      for (Grant grant : getGrants()) {
         grantees.add(grant.getGrantee());
      }
      return Collections.unmodifiableSet(grantees);
   }

   /**
    * Add a permission for the given grantee.
    * 
    * @param grantee
    * @param permission
    */
   public AccessControlList addPermission(Grantee grantee, Permission permission) {
      Grant grant = new Grant(grantee, permission);
      grants.add(grant);
      return this;
   }

   /**
    * Add a permission for the given group grantee.
    * 
    * @param groupGranteeURI
    * @param permission
    */
   public AccessControlList addPermission(GroupGranteeURI groupGranteeURI, Permission permission) {
      return addPermission(new GroupGrantee(groupGranteeURI), permission);
   }

   /**
    * Revoke a permission for the given grantee, if this specific permission was granted.
    * 
    * Note that you must be very explicit about the permissions you revoke, you cannot revoke
    * partial permissions and expect this class to determine the implied remaining permissions. For
    * example, if you revoke the {@link Permission#READ} permission from a grantee with
    * {@link Permission#FULL_CONTROL} access, <strong>the revocation will do nothing</strong> and
    * the grantee will retain full access. To change the access settings for this grantee, you must
    * first remove the {@link Permission#FULL_CONTROL} permission the add back the
    * {@link Permission#READ} permission.
    * 
    * @param grantee
    * @param permission
    */
   public AccessControlList revokePermission(Grantee grantee, Permission permission) {
      Collection<Grant> grantsForGrantee = findGrantsForGrantee(grantee.getIdentifier());
      for (Grant grant : grantsForGrantee) {
         if (grant.getPermission().equals(permission)) {
            grants.remove(grant);
         }
      }
      return this;
   }

   /**
    * Revoke a permission for the given group grantee, if this specific permission was granted.
    * 
    * Note that you must be very explicit about the permissions you revoke, you cannot revoke
    * partial permissions and expect this class to determine the implied remaining permissions. For
    * example, if you revoke the {@link Permission#READ} permission from a grantee with
    * {@link Permission#FULL_CONTROL} access, <strong>the revocation will do nothing</strong> and
    * the grantee will retain full access. To change the access settings for this grantee, you must
    * first remove the {@link Permission#FULL_CONTROL} permission the add back the
    * {@link Permission#READ} permission.
    * 
    * @param groupGranteeURI
    * @param permission
    */
   public AccessControlList revokePermission(GroupGranteeURI groupGranteeURI, Permission permission) {
      return revokePermission(new GroupGrantee(groupGranteeURI), permission);
   }

   /**
    * Revoke all the permissions granted to the given grantee.
    * 
    * @param grantee
    */
   public AccessControlList revokeAllPermissions(Grantee grantee) {
      Collection<Grant> grantsForGrantee = findGrantsForGrantee(grantee.getIdentifier());
      grants.removeAll(grantsForGrantee);
      return this;
   }

   /**
    * @param granteeId
    * @return the permissions assigned to a grantee, as identified by the given ID.
    */
   public Collection<Permission> getPermissions(String granteeId) {
      Collection<Grant> grantsForGrantee = findGrantsForGrantee(granteeId);
      return Collections2.transform(grantsForGrantee, new Function<Grant, Permission>() {
         public Permission apply(Grant g) {
            return g.getPermission();
         }
      });
   }

   /**
    * @param grantee
    * @return the permissions assigned to a grantee.
    */
   public Collection<Permission> getPermissions(Grantee grantee) {
      return getPermissions(grantee.getIdentifier());
   }

   /**
    * @param granteeURI
    * @return the permissions assigned to a group grantee.
    */
   public Collection<Permission> getPermissions(GroupGranteeURI granteeURI) {
      return getPermissions(granteeURI.getIdentifier());
   }

   /**
    * @param granteeId
    * @param permission
    * @return true if the grantee has the given permission.
    */
   public boolean hasPermission(String granteeId, Permission permission) {
      return getPermissions(granteeId).contains(permission);
   }

   /**
    * @param grantee
    * @param permission
    * @return true if the grantee has the given permission.
    */
   public boolean hasPermission(Grantee grantee, Permission permission) {
      return hasPermission(grantee.getIdentifier(), permission);
   }

   /**
    * @param granteeURI
    * @param permission
    * @return true if the grantee has the given permission.
    */
   public boolean hasPermission(GroupGranteeURI granteeURI, Permission permission) {
      return getPermissions(granteeURI.getIdentifier()).contains(permission);
   }

   /**
    * Find all the grants for a given grantee, identified by an ID which allows all Grantee types to
    * be searched.
    * 
    * @param granteeId
    *           identifier of a canonical user, email address user, or group.
    */
   protected Collection<Grant> findGrantsForGrantee(final String granteeId) {
      return Collections2.filter(grants, new Predicate<Grant>() {
         public boolean apply(Grant g) {
            return g.getGrantee().getIdentifier().equals(granteeId);
         }
      });
   }

   /**
    * Converts a canned access control policy into the equivalent access control list.
    * 
    * @param cannedAP
    * @param ownerId
    */
   public static AccessControlList fromCannedAccessPolicy(CannedAccessPolicy cannedAP,
            String ownerId) {
      AccessControlList acl = new AccessControlList();
      acl.setOwner(new CanonicalUser(ownerId));

      // Canned access policies always allow full control to the owner.
      acl.addPermission(new CanonicalUserGrantee(ownerId), Permission.FULL_CONTROL);

      if (CannedAccessPolicy.PRIVATE == cannedAP) {
         // No more work to do.
      } else if (CannedAccessPolicy.AUTHENTICATED_READ == cannedAP) {
         acl.addPermission(GroupGranteeURI.AUTHENTICATED_USERS, Permission.READ);
      } else if (CannedAccessPolicy.PUBLIC_READ == cannedAP) {
         acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
      } else if (CannedAccessPolicy.PUBLIC_READ_WRITE == cannedAP) {
         acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
         acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.WRITE);
      }
      return acl;
   }

   // /////////////////////////////////////////////////////////////////////////////
   // Class and Enum declarations to represent Grants, Grantees and Permissions //
   // /////////////////////////////////////////////////////////////////////////////

   public static enum Permission {
      READ, WRITE, READ_ACP, WRITE_ACP, FULL_CONTROL;
   };

   public static class Grant implements Comparable<Grant> {

      private Grantee grantee;
      private final Permission permission;

      public Grant(Grantee grantee, Permission permission) {
         this.grantee = grantee;
         this.permission = permission;
      }

      public Grantee getGrantee() {
         return grantee;
      }

      @VisibleForTesting
      public void setGrantee(Grantee grantee) {
         this.grantee = grantee;
      }

      public Permission getPermission() {
         return permission;
      }

      @Override
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append("Grant");
         sb.append("{grantee=").append(grantee);
         sb.append(", permission=").append(permission);
         sb.append('}');
         return sb.toString();
      }

      public int compareTo(org.jclouds.aws.s3.domain.AccessControlList.Grant o) {
         if (this == o) {
            return 0;
         } else {
            String myGranteeAndPermission = grantee.getIdentifier() + "\n" + permission;
            String otherGranteeAndPermission = o.grantee.getIdentifier() + "\n" + o.permission;
            return myGranteeAndPermission.compareTo(otherGranteeAndPermission);
         }
      }
   }

   public abstract static class Grantee implements Comparable<Grantee> {
      private final String identifier;

      protected Grantee(String identifier) {
         this.identifier = identifier;
      }

      public String getIdentifier() {
         return identifier;
      }

      @Override
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append("Grantee");
         sb.append("{identifier='").append(identifier).append('\'');
         sb.append('}');
         return sb.toString();
      }

      public int compareTo(org.jclouds.aws.s3.domain.AccessControlList.Grantee o) {
         return (this == o) ? 0 : getIdentifier().compareTo(o.getIdentifier());
      }
   }

   public static class EmailAddressGrantee extends Grantee {
      public EmailAddressGrantee(String emailAddress) {
         super(emailAddress);
      }

      public String getEmailAddress() {
         return getIdentifier();
      }
   }

   public static class CanonicalUserGrantee extends Grantee {
      private final String displayName;

      public CanonicalUserGrantee(String id, String displayName) {
         super(id);
         this.displayName = displayName;
      }

      public CanonicalUserGrantee(String id) {
         this(id, null);
      }

      public String getDisplayName() {
         return displayName;
      }

      public String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append("CanonicalUserGrantee");
         sb.append("{displayName='").append(displayName).append('\'');
         sb.append(", identifier='").append(getIdentifier()).append('\'');

         sb.append('}');
         return sb.toString();
      }
   }

   public enum GroupGranteeURI {
      ALL_USERS("http://acs.amazonaws.com/groups/global/AllUsers"), AUTHENTICATED_USERS(
               "http://acs.amazonaws.com/groups/global/AuthenticatedUsers"), LOG_DELIVERY(
               "http://acs.amazonaws.com/groups/s3/LogDelivery");

      private final String uri;

      GroupGranteeURI(String uri) {
         this.uri = uri;
      }

      public String getIdentifier() {
         return this.uri;
      }

      public static GroupGranteeURI fromURI(String uri) {
         if (ALL_USERS.uri.equals(uri)) {
            return ALL_USERS;
         } else if (AUTHENTICATED_USERS.uri.equals(uri)) {
            return AUTHENTICATED_USERS;
         } else if (LOG_DELIVERY.uri.equals(uri)) {
            return LOG_DELIVERY;
         } else {
            throw new IllegalArgumentException("No GroupGranteeURI constant matches " + uri);
         }
      }
   }

   public static class GroupGrantee extends Grantee {

      public GroupGrantee(GroupGranteeURI groupURI) {
         super(groupURI.getIdentifier());
      }
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("AccessControlList");
      sb.append("{owner=").append(owner);
      sb.append(", grants=").append(grants);
      sb.append('}');
      return sb.toString();
   }
}
