package org.jclouds.rimuhosting.miro.functions;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * On non 2xx we have an error. RimuHosting using the same json base object.
 *
 * TODO: map exceptions out into something that suits jclouds.
 */
@Singleton
public class ParseRimuHostingException implements Function<Exception, Object> {
   private Gson gson;
   @Inject
   public ParseRimuHostingException(Gson gson) {
      this.gson = gson;
   }

   @Override
   public Object apply(Exception e) {
      if(e instanceof HttpResponseException){
         HttpResponseException responseException = (HttpResponseException)e;
         Type setType = new TypeToken<Map<String, RimuHostingResponse>>() {
         }.getType();

         Map<String, RimuHostingResponse> responseMap = gson.fromJson(responseException.getContent(), setType);
         throw new RuntimeException(responseMap.values().iterator().next().getErrorInfo().getErrorClass());     
       }
      return null;
   }
}
