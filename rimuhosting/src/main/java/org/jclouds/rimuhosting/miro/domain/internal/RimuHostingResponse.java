package org.jclouds.rimuhosting.miro.domain.internal;

/**
 * Object that the payload on requests is wrapped in.
 */
public class RimuHostingResponse {
   private String status_message;
   private Integer status_code;
   private RimuHostingError error_info;
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


   public RimuHostingError getErrorInfo() {
      return error_info;
   }

   public void setErrorInfo(RimuHostingError error_info) {
      this.error_info = error_info;
   }
}
