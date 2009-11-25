package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * TODO: test
 */
public class DataTransferAllowance implements Comparable<DataTransferAllowance>{
   @SerializedName("data_transfer_gb")
   private Integer dataTransferGb;

   public Integer getDataTransferGb() {
      return dataTransferGb;
   }

   public void setDataTransferGb(Integer dataTransferGb) {
      this.dataTransferGb = dataTransferGb;
   }

   @Override
   public int compareTo(DataTransferAllowance dataTransferAllowance) {
      return dataTransferGb - dataTransferAllowance.getDataTransferGb();
   }
}
