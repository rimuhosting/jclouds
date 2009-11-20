package org.jclouds.rimuhosting.miro.functions;

import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.InstanceParameters;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;
import org.jclouds.http.functions.ParseJson;

import java.util.Map;
import java.util.List;
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
public class ParseDestroyResponseFromJsonResponse extends ParseJson<List<String>> {
   @Inject
   public ParseDestroyResponseFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class DestroyResponse extends RimuHostingResponse {
      private List<String> cancel_messages;

      public List<String> getCancelMessages() {
         return cancel_messages;
      }

      public void setCancelMessages(List<String> cancel_messages) {
         this.cancel_messages = cancel_messages;
      }
   }
   @Override
   protected List<String> apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, DestroyResponse>>() {
      }.getType();
      try {
         Map<String, DestroyResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next().getCancelMessages();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}