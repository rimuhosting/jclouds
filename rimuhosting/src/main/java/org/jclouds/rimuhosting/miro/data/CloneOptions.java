package org.jclouds.rimuhosting.miro.data;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Ivan Meredith <ivan@ivan.net.nz>
 */
public class CloneOptions implements PostData {
   /**
    * Select this if you want the newly setup VPS to be a clone of
    * another VPS you have with us.&nbsp; We will need to pause (but
    * not restart) the clone source VPS for a few seconds to a few
    * minutes to take the snapshot.
    */
   @SerializedName("vps_order_oid_to_clone")
   private Long instanceId;
   /**
    * The label you want to give the server.&nbsp; It will need to be a
    * fully qualified domain name (FQDN).&nbsp; e.g. example.com. Will
    * default to the domain name used on the order id provided.
    */
   @SerializedName("domain_name")
   private String name;

   public long getInstanceId() {
      return instanceId;
   }

   public void setInstanceId(long instanceId) {
      this.instanceId = instanceId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   
   public void validate(){
	   assert(instanceId == null || instanceId < 0);
   }
}
