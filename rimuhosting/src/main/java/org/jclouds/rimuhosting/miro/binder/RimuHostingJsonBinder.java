package org.jclouds.rimuhosting.miro.binder;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonEntity;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class RimuHostingJsonBinder extends BindToJsonEntity {
  public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      bindToRequest(request, (Object) postParams);
   }

   public void bindToRequest(HttpRequest request, Object toBind) {
       checkState(gson != null, "Program error: gson should have been injected at this point");
       Map<String, Object> test = new HashMap<String, Object>();
       test.put("request", toBind);
       super.bindToRequest(request, test);
   }
}
