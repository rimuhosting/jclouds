package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;


@Singleton
public class ParseInstanceFromJsonResponse extends ParseJson<Instance> {
   @Inject
   public ParseInstanceFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class OrderResponse extends RimuHostingResponse {
      private Instance about_order;
      public Instance getAboutOrder() {
         return about_order;
      }

      public void setAboutOrder(Instance about_orders) {
         this.about_order = about_orders;
      }
   }
   @Override
   protected Instance apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, OrderResponse>>() {
      }.getType();
      try {
         Map<String, OrderResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next().getAboutOrder();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}