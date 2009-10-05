/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.mezeo.pcs2;

import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.options.GetOptions;
import org.jclouds.mezeo.pcs2.binders.CreateContainerBinder;
import org.jclouds.mezeo.pcs2.binders.PCSFileAsMultipartFormBinder;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.functions.AddMetadataAndParseResourceIdIntoBytes;
import org.jclouds.mezeo.pcs2.functions.AssembleBlobFromContentAndMetadataCache;
import org.jclouds.mezeo.pcs2.functions.ContainerAndFileNameToResourceId;
import org.jclouds.mezeo.pcs2.functions.ContainerNameToResourceId;
import org.jclouds.mezeo.pcs2.functions.InvalidateContainerNameCacheAndReturnTrueIf2xx;
import org.jclouds.mezeo.pcs2.functions.InvalidatePCSKeyCacheAndReturnVoidIf2xx;
import org.jclouds.mezeo.pcs2.functions.ReturnFalseIfContainerNotFound;
import org.jclouds.mezeo.pcs2.functions.ReturnTrueIfContainerAlreadyExists;
import org.jclouds.mezeo.pcs2.xml.FileListToContainerMetadataListHandler;
import org.jclouds.mezeo.pcs2.xml.FileListToFileMetadataListHandler;
import org.jclouds.mezeo.pcs2.xml.FileMetadataHandler;
import org.jclouds.rest.Endpoint;
import org.jclouds.rest.EntityParam;
import org.jclouds.rest.ExceptionParser;
import org.jclouds.rest.Headers;
import org.jclouds.rest.ParamParser;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.ResponseParser;
import org.jclouds.rest.SkipEncoding;
import org.jclouds.rest.XMLResponseParser;

/**
 * Provides access to Mezeo PCS v2 via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://developer.mezeo.com/mezeo-developer-center/documentation/howto-using-curl-to-access-api"
 *      />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(BasicAuthentication.class)
public interface PCSBlobStore extends BlobStore<ContainerMetadata, FileMetadata, PCSFile> {

   @GET
   @XMLResponseParser(FileListToContainerMetadataListHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Path("/contents")
   @Endpoint(RootContainer.class)
   SortedSet<ContainerMetadata> listContainers();

   @GET
   @ExceptionParser(ReturnFalseIfContainerNotFound.class)
   @Path("/containers/{containerResourceId}")
   @Endpoint(PCS.class)
   boolean containerExists(
            @PathParam("containerResourceId") @ParamParser(ContainerNameToResourceId.class) String containerName);

   @POST
   @Path("/contents")
   @Endpoint(RootContainer.class)
   @ExceptionParser(ReturnTrueIfContainerAlreadyExists.class)
   Future<Boolean> createContainer(@EntityParam(CreateContainerBinder.class) String container);

   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/containers/{containerResourceId}")
   @Endpoint(PCS.class)
   @ResponseParser(InvalidateContainerNameCacheAndReturnTrueIf2xx.class)
   Future<Void> deleteContainer(
            @PathParam("containerResourceId") @ParamParser(ContainerNameToResourceId.class) String containerName);

   @GET
   @XMLResponseParser(FileListToFileMetadataListHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Path("/containers/{containerResourceId}/contents")
   @Endpoint(PCS.class)
   Future<? extends SortedSet<FileMetadata>> listBlobs(
            @PathParam("containerResourceId") @ParamParser(ContainerNameToResourceId.class) String containerName);

   @POST
   @Path("/containers/{containerResourceId}/contents")
   @Endpoint(PCS.class)
   @ResponseParser(AddMetadataAndParseResourceIdIntoBytes.class)
   Future<byte[]> putBlob(
            @PathParam("containerResourceId") @ParamParser(ContainerNameToResourceId.class) String containerName,
            @EntityParam(PCSFileAsMultipartFormBinder.class) PCSFile object);

   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/files/{resourceId}")
   @PathParam("resourceId")
   @Endpoint(PCS.class)
   @ResponseParser(InvalidatePCSKeyCacheAndReturnVoidIf2xx.class)
   @ParamParser(ContainerAndFileNameToResourceId.class)
   Future<Void> removeBlob(String container, String key);

   @GET
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/files/{resourceId}/content")
   @PathParam("resourceId")
   @Endpoint(PCS.class)
   @ParamParser(ContainerAndFileNameToResourceId.class)
   @ResponseParser(AssembleBlobFromContentAndMetadataCache.class)
   Future<PCSFile> getBlob(String container, String key);

   @GET
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/files/{resourceId}/content")
   @PathParam("resourceId")
   @Endpoint(PCS.class)
   @ParamParser(ContainerAndFileNameToResourceId.class)
   @ResponseParser(AssembleBlobFromContentAndMetadataCache.class)
   Future<PCSFile> getBlob(String container, String key, GetOptions options);

   @GET
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/files/{resourceId}")
   @PathParam("resourceId")
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Endpoint(PCS.class)
   @ParamParser(ContainerAndFileNameToResourceId.class)
   @XMLResponseParser(FileMetadataHandler.class)
   FileMetadata blobMetadata(String container, String key);

}
