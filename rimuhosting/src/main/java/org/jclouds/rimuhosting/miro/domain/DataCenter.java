package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * TODO: test
 */
public class DataCenter implements Comparable<DataCenter> {
   @SerializedName("data_center_location_code")
   private String id;
   @SerializedName("data_center_location_name")
   private String name;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public int compareTo(DataCenter dataCenter) {
     return id.compareTo(dataCenter.getId());
   }
}
