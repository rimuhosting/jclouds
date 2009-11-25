package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;


public class Image implements Comparable<Image> {
   @SerializedName("distro_code")
   private String id;
   @SerializedName("distro_desciption")
   private String description;

   @Override
   public int compareTo(Image image) {
      return 0;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
