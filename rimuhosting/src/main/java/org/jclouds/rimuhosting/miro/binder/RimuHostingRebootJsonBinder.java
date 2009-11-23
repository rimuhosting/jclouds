package org.jclouds.rimuhosting.miro.binder;

import com.google.common.collect.ImmutableMap;
import org.jclouds.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Ivan MEredith
 *         Date: 23/11/2009
 *         Time: 9:04:50 PM
 */
public class RimuHostingRebootJsonBinder extends RimuHostingJsonBinder{
      public void bindToRequest(HttpRequest request, Object toBind) {
        super.bindToRequest(request, ImmutableMap.of("running_state", "REBOOTING"));
        }
}
