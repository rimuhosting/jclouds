package org.jclouds.rimuhosting.miro.filters;

import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpUtils;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 2:58:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class RimuHostingAuthentication implements HttpRequestFilter {
   private List<String> credentialList;

   public RimuHostingAuthentication(String apikey){
    this.credentialList = Collections.singletonList(String.format("rimuhosting apikey=%s", checkNotNull(apikey, "apikey")));
   }
   
   @Override
   public void filter(HttpRequest request) throws HttpException {
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION, credentialList);
   }
}
