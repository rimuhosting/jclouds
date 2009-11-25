package org.jclouds.rimuhosting.miro.binder;

import com.google.common.collect.ImmutableMap;
import org.jclouds.http.HttpRequest;

/**
 * Need to PUT a static string
 *
 * {"request":{"running_state":"RESTARTING"}
 */
public class RimuHostingRebootJsonBinder extends RimuHostingJsonBinder{
      public void bindToRequest(HttpRequest request, Object toBind) {
        super.bindToRequest(request,(Object)ImmutableMap.of("running_state", "RESTARTING"));
      }
}
