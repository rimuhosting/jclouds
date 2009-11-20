package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 12:58:11 PM
 * To change this template use File | Settings | File Templates.
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
