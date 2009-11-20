package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Attributes about a running VPS.&nbsp; Implicit with any changes to
 * these attributes on a VPS is that we'd need to restart the VPS for
 * the changes to happen.&nbsp; At least at this point in time.

 * Copyright RimuHosting 2009
 * @author Ivan Meredith <ivan@ivan.net.nz>
 */
public class InstanceParameters {
   /**
    * File system image size.
    */
   @SerializedName("disk_space_mb")
   private Integer primaryDisk;
   /**
    * Some VPSs have a secondary partition.&nbsp; One that is not part
    * of the regular backup setups.&nbsp; Mostly not used.
    */
   @SerializedName("disk_space_2_mb")
   private Integer secondaryDisk;
   /**
    * Memory size.
    */
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
