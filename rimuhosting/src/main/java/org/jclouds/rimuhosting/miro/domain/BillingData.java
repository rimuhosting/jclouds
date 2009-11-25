package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class BillingData {
   @SerializedName("cancellation_date")
   private Timestamp dateCancelled;
   @SerializedName("monthly_recurring_fee")
   private Double monthlyCharge;
   @SerializedName("order_date")
   private Timestamp dateOrdered;
   @SerializedName("prepaid_until")
   private Timestamp dataPrepaidUntil;
   @SerializedName("suspended_date")
   private Timestamp dateSuspended;

   public Timestamp getDateCancelled() {
      return dateCancelled;
   }

   public void setDateCancelled(Timestamp dateCancelled) {
      this.dateCancelled = dateCancelled;
   }

   public Double getMonthlyCharge() {
      return monthlyCharge;
   }

   public void setMonthlyCharge(Double monthlyCharge) {
      this.monthlyCharge = monthlyCharge;
   }

   public Timestamp getDateOrdered() {
      return dateOrdered;
   }

   public void setDateOrdered(Timestamp dateOrdered) {
      this.dateOrdered = dateOrdered;
   }

   public Timestamp getDataPrepaidUntil() {
      return dataPrepaidUntil;
   }

   public void setDataPrepaidUntil(Timestamp dataPrepaidUntil) {
      this.dataPrepaidUntil = dataPrepaidUntil;
   }

   public Timestamp getDateSuspended() {
      return dateSuspended;
   }

   public void setDateSuspended(Timestamp dateSuspended) {
      this.dateSuspended = dateSuspended;
   }
}
