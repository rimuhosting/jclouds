package org.jclouds.mezeo.pcs2.options;

import static org.jclouds.mezeo.pcs2.options.PutBlockOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * Tests possible uses of PutBlockOptions and PutBlockOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "mezeo.PutBlockOptionsTest")
public class PutBlockOptionsTest {

   @Test
   public void testRange() {
      PutBlockOptions options = new PutBlockOptions();
      options.range(0, 1024);
      bytes1to1024(options);
   }

   private void bytes1to1024(PutBlockOptions options) {
      assertEquals(options.getRange(), "bytes 0-1024/*");
   }

   @Test
   public void testRangeZeroToFive() {
      PutBlockOptions options = new PutBlockOptions();
      options.range(0, 5);
      assertEquals(options.getRange(), "bytes 0-5/*");
   }

   @Test
   public void testRangeOverride() {
      PutBlockOptions options = new PutBlockOptions();
      options.range(0, 5).range(10, 100);
      assertEquals(options.getRange(), "bytes 10-100/*");
   }

   @Test
   public void testNullRange() {
      PutBlockOptions options = new PutBlockOptions();
      assertNull(options.getRange());
   }

   @Test
   public void testRangeStatic() {
      PutBlockOptions options = range(0, 1024);
      bytes1to1024(options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative1() {
      range(-1, 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative2() {
      range(0, -1);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative() {
      range(-1, -1);
   }

}
