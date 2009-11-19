package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 12:58:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class InstanceParameters {
   @SerializedName("disk_space_mb")
   private Integer primaryDisk;
   @SerializedName("disk_space_2_mb")
   private Integer secondaryDisk;
   @SerializedName("memory_mb")
   private Integer ram;

   public Integer getPrimaryDisk() {
      return primaryDisk;
   }

   public void setPrimaryDisk(Integer primaryDisk) {
      this.primaryDisk = primaryDisk;
   }

   public Integer getSecondaryDisk() {
      return secondaryDisk;
   }

   public void setSecondaryDisk(Integer secondaryDisk) {
      this.secondaryDisk = secondaryDisk;
   }

   public Integer getRam() {
      return ram;
   }

   public void setRam(Integer ram) {
      this.ram = ram;
   }
}
