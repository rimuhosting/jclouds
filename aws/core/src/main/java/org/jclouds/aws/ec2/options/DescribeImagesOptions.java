package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.aws.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the DescribeImages operation. <h2>
 * Usage</h2> The recommended way to instantiate a DescribeImagesOptions object is to statically
 * import DescribeImagesOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * Future<SortedSet<ImageMetadata>> images = connection.describeImages(executableBy("123125").imageIds(1000, 1004));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-DescribeImages.html"
 *      />
 */
public class DescribeImagesOptions extends BaseEC2RequestOptions {
   public static final DescribeImagesOptions NONE = new DescribeImagesOptions();

   /**
    * AMIs for which the specified user has explicit launch permissions.
    * 
    */
   public DescribeImagesOptions executableBy(String accountId) {
      formParameters.put("ExecutableBy", checkNotNull(accountId, "accountId"));
      return this;
   }

   public String getExecutableBy() {
      return getFirstFormOrNull("ExecutableBy");
   }

   /**
    * AMI IDs to describe.
    */
   public DescribeImagesOptions imageIds(String... imageIds) {
      indexFormValuesWithPrefix("ImageId", imageIds);
      return this;
   }

   public Set<String> getImageIds() {
      return getFormValuesWithKeysPrefixedBy("ImageId.");
   }

   /**
    * Returns AMIs owned by the specified owner. Multiple owners can be specified.
    */
   public DescribeImagesOptions ownedBy(String... owners) {
      indexFormValuesWithPrefix("Owner", owners);
      return this;
   }

   public Set<String> getOwners() {
      return getFormValuesWithKeysPrefixedBy("Owner.");
   }

   public static class Builder {

      /**
       * @see DescribeImagesOptions#executableBy(String )
       */
      public static DescribeImagesOptions executableBy(String accountId) {
         DescribeImagesOptions options = new DescribeImagesOptions();
         return options.executableBy(accountId);
      }

      /**
       * @see DescribeImagesOptions#imageIds(String[] )
       */
      public static DescribeImagesOptions imageIds(String... imageIds) {
         DescribeImagesOptions options = new DescribeImagesOptions();
         return options.imageIds(imageIds);
      }

      /**
       * @see DescribeImagesOptions#ownedBy(String[] )
       */
      public static DescribeImagesOptions ownedBy(String... owners) {
         DescribeImagesOptions options = new DescribeImagesOptions();
         return options.ownedBy(owners);
      }

   }
}
