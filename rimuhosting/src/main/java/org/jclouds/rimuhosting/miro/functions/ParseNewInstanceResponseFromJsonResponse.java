package org.jclouds.rimuhosting.miro.functions;

import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.NewInstanceResponse;
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