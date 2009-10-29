package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class AzureBlobToBlob implements Function<AzureBlob, Blob> {
   private final Blob.Factory blobFactory;
   private final BlobPropertiesToBlobMetadata blobPr2BlobMd;

   @Inject
   AzureBlobToBlob(Factory blobFactory, BlobPropertiesToBlobMetadata blobPr2BlobMd) {
      this.blobFactory = blobFactory;
      this.blobPr2BlobMd = blobPr2BlobMd;
   }

   public Blob apply(AzureBlob from) {
      Blob blob = blobFactory.create(blobPr2BlobMd.apply(from.getProperties()));
      if (from.getContentLength() != null)
         blob.setContentLength(from.getContentLength());
      blob.setData(from.getData());
      blob.setAllHeaders(from.getAllHeaders());
      return blob;
   }
}
