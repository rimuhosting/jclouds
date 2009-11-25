package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.NewInstanceResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;


@Singleton
public class ParseNewInstanceResponseFromJsonResponse extends ParseJson<NewInstanceResponse> {
   @Inject
   public ParseNewInstanceResponseFromJsonResponse(Gson gson) {
      super(gson);
   }

   @Override
   protected NewInstanceResponse apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, NewInstanceResponse>>() {
      }.getType();
      try {
         Map<String, NewInstanceResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}