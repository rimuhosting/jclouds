package org.jclouds.rimuhosting.miro.domain.internal;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class RimuHostingTimestamp {
   private Long ms_since_epoch;

   public Long getMs_since_epoch() {
      return ms_since_epoch;
   }

   public void setMs_since_epoch(Long ms_since_epoch) {
      this.ms_since_epoch = ms_since_epoch;
   }

   public Timestamp getTimestamp(){
      return new Timestamp(ms_since_epoch);
   }
}
