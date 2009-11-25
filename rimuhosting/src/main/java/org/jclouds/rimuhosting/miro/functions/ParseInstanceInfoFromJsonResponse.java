package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.InstanceInfo;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

@Singleton
public class ParseInstanceInfoFromJsonResponse extends ParseJson<InstanceInfo> {
   @Inject
   public ParseInstanceInfoFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class OrderResponse extends RimuHostingResponse {
      private InstanceInfo running_vps_info;

      public InstanceInfo getInstanceInfo() {
         return running_vps_info;
      }

      public void setInstanceInfo(InstanceInfo running_vps_info) {
         this.running_vps_info = running_vps_info;
      }
   }
   @Override
   protected InstanceInfo apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, OrderResponse>>() {
      }.getType();
      try {
         Map<String, OrderResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next().getInstanceInfo();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}