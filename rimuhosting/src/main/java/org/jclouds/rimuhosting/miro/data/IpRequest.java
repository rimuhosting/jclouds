package org.jclouds.rimuhosting.miro.data;

import com.google.gson.annotations.SerializedName;

/**
 * Number of IPs VPS needs.&nbsp; In a separate data structure so that
  * at a later date we can add more IPs to a provisioned server.
 *
 * @author Ivan Meredith &lt;ivan@ivan.net.nz>
 */
public class IpRequest implements PostData {
   /**
    * How many IPs you need.&nbsp; Typically 1.&nbsp; Typically you
    * only need more than one IP if your server has SSL certs for more
    * than one domains.
    */
   @SerializedName("num_ips")
   private int numberOfIps = 1;
   /**
    * The reason for requiring more than one IP address.&nbsp; The
    * number of IP addresses will be limited.&nbsp; If you hit that
    * limit, then contact support to manually allocate the IPs (and in
    * the mean time just use fewer IPs).
    */
   @SerializedName("extra_ip_reason")
   private String extraIpReason = "";

   public int getNumberOfIps() {
      return numberOfIps;
   }

   public void setNumberOfIps(int numberOfIps) {
      this.numberOfIps = numberOfIps;
   }

   public String getExtraIpReason() {
      return extraIpReason;
   }

   public void setExtraIpReason(String extraIpReason) {
      this.extraIpReason = extraIpReason;
   }
	
	@Override
	public void validate() {
		assert(numberOfIps < 1 && numberOfIps > 5);
		assert(numberOfIps > 1 && extraIpReason == null || extraIpReason.length() == 0);
	}
}
