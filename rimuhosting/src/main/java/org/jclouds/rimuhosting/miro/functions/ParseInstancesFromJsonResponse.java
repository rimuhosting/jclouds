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
import java.util.SortedSet;


@Singleton
public class ParseInstancesFromJsonResponse extends ParseJson<SortedSet<Instance>> {
   @Inject
   public ParseInstancesFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class OrderResponse extends RimuHostingResponse {
      private SortedSet<Instance> about_orders;
      public SortedSet<Instance> getAboutOrders() {
         return about_orders;
      }

      public void setAboutOrders(SortedSet<Instance> about_orders) {
         this.about_orders = about_orders;
      }
   }
   @Override
   protected SortedSet<Instance> apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, OrderResponse>>() {
      }.getType();
      try {
         Map<String, OrderResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next().getAboutOrders();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}
