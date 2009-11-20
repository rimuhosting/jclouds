package org.jclouds.rimuhosting.miro.functions;

import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;
import org.jclouds.http.functions.ParseJson;

import java.util.Map;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.inject.Singleton;
import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 2:46:03 PM
 * To change this template use File | Settings | File Templates.
 */
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