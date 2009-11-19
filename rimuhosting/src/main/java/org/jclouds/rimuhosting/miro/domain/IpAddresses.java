package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

import java.util.SortedSet;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 12:58:02 PM
 * To change this template use File | Settings | File Templates.
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
