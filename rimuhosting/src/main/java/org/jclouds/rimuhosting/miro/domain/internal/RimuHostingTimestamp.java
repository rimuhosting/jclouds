package org.jclouds.rimuhosting.miro.domain.internal;

import java.sql.Timestamp;

/**
 * Handles a timestamp JSON object from rimuhosting
 */
public class RimuHostingTimestamp {
   private Long ms_since_epoch;

   public Long getMs() {
      return ms_since_epoch;
   }

   public void setMs(Long ms_since_epoch) {
      this.ms_since_epoch = ms_since_epoch;
   }

   public Timestamp getTimestamp(){
      return new Timestamp(ms_since_epoch);
   }
}
