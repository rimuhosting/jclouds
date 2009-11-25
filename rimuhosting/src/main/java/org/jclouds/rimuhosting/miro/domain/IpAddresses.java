package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

import java.util.SortedSet;

/**
 * IpAddresses assigned to an Interface. Not rimuhosting doesnt have private IPs.
 */
public class IpAddresses {
   @SerializedName("primary_ip")
   private String primaryIp;
   @SerializedName("secondary_ips")
   private SortedSet<String> secondaryIps;

   public String getPrimaryIp() {
      return primaryIp;
   }

   public void setPrimaryIp(String primaryIp) {
      this.primaryIp = primaryIp;
   }
}
