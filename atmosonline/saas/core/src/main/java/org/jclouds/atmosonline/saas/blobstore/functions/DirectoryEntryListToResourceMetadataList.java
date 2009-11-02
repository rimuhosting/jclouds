package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.BlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.ListContainerResponseImpl;
import org.jclouds.blobstore.domain.internal.ResourceMetadataImpl;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class DirectoryEntryListToResourceMetadataList
         implements
         Function<BoundedSortedSet<? extends DirectoryEntry>, ListContainerResponse<? extends ResourceMetadata>> {

   public ListContainerResponse<? extends ResourceMetadata> apply(
            BoundedSortedSet<? extends DirectoryEntry> from) {

      return new ListContainerResponseImpl<ResourceMetadata>(Iterables.transform(from,
               new Function<DirectoryEntry, ResourceMetadata>() {

                  public ResourceMetadata apply(DirectoryEntry from) {
                     ResourceType type = from.getType() == FileType.DIRECTORY ? ResourceType.FOLDER
                              : ResourceType.BLOB;
                     if (type == ResourceType.FOLDER)
                        return new ResourceMetadataImpl(type, from.getObjectID(), from
                                 .getObjectName(), null, null, null, null, Maps
                                 .<String, String> newHashMap());
                     else
                        return new BlobMetadataImpl(from.getObjectID(), from.getObjectName(), null,
                                 null, null, null, Maps.<String, String> newHashMap(), null, null);
                  }

               }), null, from.getToken(),

      null, from.getToken() != null);

   }
}