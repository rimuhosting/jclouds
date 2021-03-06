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
public class ParseResizeResponseFromJsonResponse extends ParseJson<ParseResizeResponseFromJsonResponse.ResizeResponse> {
   @Inject
   public ParseResizeResponseFromJsonResponse(Gson gson) {
      super(gson);
   }

   public static class ResizeResponse extends RimuHostingResponse {
      private Instance about_order;
      private ResizeResponse resource_change_result;

      public ResizeResponse getResourceChangeResult() {
         return resource_change_result;
      }

      public void setResourceChangeResult(ResizeResponse resource_change_result) {
         this.resource_change_result = resource_change_result;
      }

      public Instance getAboutOrder() {
         return about_order;
      }

      public void setAboutOrder(Instance about_orders) {
         this.about_order = about_orders;
      }
   }
   @Override
   protected ResizeResponse apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, ResizeResponse>>() {
      }.getType();
      try {
         Map<String, ResizeResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}