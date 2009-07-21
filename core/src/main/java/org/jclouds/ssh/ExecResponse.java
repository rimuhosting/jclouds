/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.ssh;

/**
 * @author Adrian Cole
 */
public class ExecResponse {

   private final String error;
   private final String output;

   public ExecResponse(String output, String error) {
      this.output = output;
      this.error = error;
   }

   public String getError() {
      return error;
   }

   public String getOutput() {
      return output;
   }

   @Override
   public String toString() {
      return "ExecResponse [error=" + error + ", output=" + output + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((error == null) ? 0 : error.hashCode());
      result = prime * result + ((output == null) ? 0 : output.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ExecResponse other = (ExecResponse) obj;
      if (error == null) {
         if (other.error != null)
            return false;
      } else if (!error.equals(other.error))
         return false;
      if (output == null) {
         if (other.output != null)
            return false;
      } else if (!output.equals(other.output))
         return false;
      return true;
   }

}