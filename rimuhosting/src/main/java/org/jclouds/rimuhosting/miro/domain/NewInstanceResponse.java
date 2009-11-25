package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.data.NewInstance;

/**
 * Wrapper object to get back all data from a Instance create. The Password has been populated the NewInstance
 * object.
 */
public class NewInstanceResponse implements Comparable<NewInstanceResponse> {
   @SerializedName("about_order")
   private Instance instance;

   @SerializedName("new_order_request")
   private NewInstance newInstanceRequest;

   @SerializedName("running_vps_info")
   private InstanceInfo instanceInfo;

   public Instance getInstance() {
      return instance;
   }

   public void setInstance(Instance instaince) {
      this.instance = instaince;
   }

   public NewInstance getNewInstanceRequest() {
      return newInstanceRequest;
   }

   public void setNewInstanceRequest(NewInstance newInstanceRequest) {
      this.newInstanceRequest = newInstanceRequest;
   }

   public InstanceInfo getInstanceInfo() {
      return instanceInfo;
   }

   public void setInstanceInfo(InstanceInfo instanceInfo) {
      this.instanceInfo = instanceInfo;
   }

   @Override
   public int compareTo(NewInstanceResponse instance) {
      return this.instance.getId().compareTo(instance.getInstance().getId());     
   }
}
