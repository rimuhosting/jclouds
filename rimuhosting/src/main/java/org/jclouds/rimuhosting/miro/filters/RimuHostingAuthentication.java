package org.jclouds.rimuhosting.miro.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;
import java.util.List;

/**
 * RimuHosting Authentication is a Authorization Header.
 *
 * Authorization: rimuhosting apikey=&lt;key>
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
