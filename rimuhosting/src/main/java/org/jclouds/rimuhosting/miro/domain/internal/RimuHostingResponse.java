package org.jclouds.rimuhosting.miro.domain.internal;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 12:24:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RimuHostingResponse {
   private String status_message;
   private Integer status_code;

   public String getStatusMessage() {
      return status_message;
   }

   public void setStatusMessage(String status_message) {
      this.status_message = status_message;
   }

   public Integer getStatusCode() {
      return status_code;
   }

   public void setStatusCode(Integer status_code) {
      this.status_code = status_code;
   }


}
