package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

import java.util.SortedSet;

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
