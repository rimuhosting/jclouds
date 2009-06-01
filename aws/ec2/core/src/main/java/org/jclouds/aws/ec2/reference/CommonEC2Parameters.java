package org.jclouds.aws.ec2.reference;

/**
 * Configuration properties and constants used in EC2 connections.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/Query-Common-Parameters.html"/>
 * @author Adrian Cole
 */
public interface CommonEC2Parameters {

   /**
    * Indicates the action to perform. Example: RunInstances
    */
   public static final String ACTION = "Action";

   /**
    * The API version to use, as specified in the WSDL. Example: 2009-04-04
    */
   public static final String VERSION = "Version";

   /**
    * The Access Key ID for the request sender. This identifies the account which will be charged
    * for usage of the service. The account with which the Access Key ID is associated must be
    * signed up for Amazon EC2, or requests will not be accepted. AKIADQKE4SARGYLE
    */
   public static final String AWS_ACCESS_KEY_ID = "AWSAccessKeyId";

   /**
    * The date and time at which the request is signed, in the format YYYY-MM-DDThh:mm:ssZ. For more
    * information, go to ISO 8601. Example: 2006-07-07T15:04:56Z
    */
   public static final String TIMESTAMP = "Timestamp";

   /**
    * The date and time at which the signature included in the request expires, in the format
    * YYYY-MM-DDThh:mm:ssZ. Example: 2006-07-07T15:04:56Z
    */
   public static final String EXPIRES = "Expires";
   /**
    * The request signature. For more information, go to the Amazon Elastic Compute Cloud Developer
    * Guide. Example: Qnpl4Qk/7tINHzfXCiT7VbBatDA=
    */
   public static final String SIGNATURE = "Signature";
   /**
    * The hash algorithm you use to create the request signature. Valid values: HmacSHA256 |
    * HmacSHA1. For more information, go to the Amazon Elastic Compute Cloud Developer Guide.
    * Example: HmacSHA256
    */
   public static final String SIGNATURE_METHOD = "SignatureMethod";
   /**
    * The signature version you use to sign the request. Set this value to 2. For more information,
    * go to the Amazon Elastic Compute Cloud Developer Guide. Example: 2
    * 
    */
   public static final String SIGNATURE_VERSION = "SignatureVersion";
}
