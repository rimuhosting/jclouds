package org.jclouds.rimuhosting.miro.domain.internal;

import com.google.gson.annotations.SerializedName;


public class RimuHostingError {
   @SerializedName("error_class")
   private String errorClass;

   @SerializedName("full_error_message")
   private String errorMessage;

   @SerializedName("error_title")
   private String error;

   public String getErrorClass() {
      return errorClass;
   }

   public void setErrorClass(String errorClass) {
      this.errorClass = errorClass;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public String getError() {
      return error;
   }

   public void setError(String error) {
      this.error = error;
   }
}
