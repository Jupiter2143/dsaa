package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

/**
 * test images provided in submission
 */

public class ImageTest {

    @Test
    void testImage() throws IOException {
        Service testService1 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage1.png");
        Image testImage1 = testService1.getImage();
        String testResult1 = testImage1.toString();
        Assertions.assertThat(testResult1).isEqualTo("[0] <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) \n" +
                "[1] <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 153) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) \n");

        Service testService2 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage2.png");
        Image testImage2 = testService2.getImage();
        String testResult2 = testImage2.toString();
        System.out.println(testResult2);
        Assertions.assertThat(testResult2).isEqualTo("[0] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) \n" +
                "[1] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) \n");

        Service testService3 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage3.png");
        Image testImage3 = testService3.getImage();
        String testResult3 = testImage3.toString();
        System.out.println(testResult3);
        Assertions.assertThat(testResult3).isEqualTo("[0] <-> (Energy: 0.0 Blue value: 234) <-> (Energy: 0.0 Blue value: 234) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 234) <-> (Energy: 0.0 Blue value: 234) \n" +
                "[1] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n");
    }

    @Test
    void testGetWidth() {
        double[][] testArray1 = {{5.0, 6.0, 3.0, 8.0}, {4.0, 1.0, 6.0, 4.0}, {3.0, 2.0, 1.0, 3.0}, {8.0, 6.0, 5.0, 2.0}};
        Image testImage1 = new Image(testArray1);
        Assertions.assertThat(testImage1.getWidth()).isEqualTo(4);

        double[][] testArray2 = {{5.0, 6.0, 3.0}, {4.0, 1.0, 6.0}, {3.0, 2.0, 1.0}};
        Image testImage2 = new Image(testArray2);
        Assertions.assertThat(testImage2.getWidth()).isEqualTo(3);

        double[][] testArray3 = {{5.0, 6.0, 3.0, 8.0, 1.0, 3.0}, {4.0, 1.0, 6.0, 4.0, 2.0, 5.0}};
        Image testImage3 = new Image(testArray3);
        Assertions.assertThat(testImage3.getWidth()).isEqualTo(6);
    }

    @Test
    void testGetAt() {
        double[][] testArray1 = {{5.0, 6.0, 3.0, 8.0}, {4.0, 1.0, 6.0, 4.0}, {3.0, 2.0, 1.0, 3.0}, {8.0, 6.0, 5.0, 2.0}};
        Image testImage1 = new Image(testArray1);
        System.out.println(testImage1.toString());
        Image.Pixel case1 = testImage1.getAt(0,0);
        String case1ToString = ("Energy: "+case1.energy+" Blue value: "+case1.color.getBlue());
        Assertions.assertThat(case1ToString).isEqualTo("Energy: 5.0 Blue value: 0");

        Image.Pixel case2 = testImage1.getAt(3,1);
        String case2ToString = ("Energy: "+case2.energy+" Blue value: "+case2.color.getBlue());
        Assertions.assertThat(case2ToString).isEqualTo("Energy: 4.0 Blue value: 0");

        Image.Pixel case3 = testImage1.getAt(1,0);
        String case3ToString = ("Energy: "+case3.energy+" Blue value: "+case3.color.getBlue());
        Assertions.assertThat(case3ToString).isEqualTo("Energy: 6.0 Blue value: 0");
    }

    /**
     * used to test toBuff()
     * @param img1: first BufferedImage input
     * @param img2: second BufferedImage input
     * @return a boolean of the comparison
     */
    private static boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }
        int width = img1.getWidth();
        int height = img1.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    void testToBuff() throws IOException{
        Service testService1 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage1.png");
        BufferedImage testResult1 = testService1.getImage().toBuff();
        Assertions.assertThat(imagesAreEqual(testResult1, testService1.getBuffImg())).isTrue();

        Service testService2 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage2.png");
        BufferedImage testResult2 = testService2.getImage().toBuff();
        Assertions.assertThat(imagesAreEqual(testResult2, testService2.getBuffImg())).isTrue();

        Service testService3 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage3.png");
        BufferedImage testResult3 = testService3.getImage().toBuff();
        Assertions.assertThat(imagesAreEqual(testResult3, testService3.getBuffImg())).isTrue();
    }

    @Test
    void testUpdateEnergies() throws IOException{
        Service testService1 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage1.png");
        testService1.getImage().updateEnergies();
        Assertions.assertThat(testService1.getImage().toString()).isEqualTo("[0] <-> (Energy: 165.01 Blue value: 243) <-> (Energy: 291.99 Blue value: 153) <-> (Energy: 457.36 Blue value: 153) <-> (Energy: 291.99 Blue value: 153) <-> (Energy: 165.01 Blue value: 243) \n" +
                "[1] <-> (Energy: 291.99 Blue value: 153) <-> (Energy: 399.28 Blue value: 243) <-> (Energy: 340.68 Blue value: 255) <-> (Energy: 399.28 Blue value: 243) <-> (Energy: 291.99 Blue value: 153) \n" +
                "[2] <-> (Energy: 457.36 Blue value: 153) <-> (Energy: 340.68 Blue value: 255) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 340.67 Blue value: 255) <-> (Energy: 457.36 Blue value: 153) \n" +
                "[3] <-> (Energy: 291.99 Blue value: 153) <-> (Energy: 399.28 Blue value: 243) <-> (Energy: 340.67 Blue value: 255) <-> (Energy: 399.28 Blue value: 243) <-> (Energy: 291.99 Blue value: 153) \n" +
                "[4] <-> (Energy: 165.01 Blue value: 243) <-> (Energy: 291.99 Blue value: 153) <-> (Energy: 457.36 Blue value: 153) <-> (Energy: 291.99 Blue value: 153) <-> (Energy: 165.01 Blue value: 243) \n");

        Service testService2 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage2.png");
        testService2.getImage().updateEnergies();
        Assertions.assertThat(testService2.getImage().toString()).isEqualTo("[0] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 85.34 Blue value: 36) <-> (Energy: 134.93 Blue value: 243) <-> (Energy: 120.68 Blue value: 36) <-> (Energy: 60.34 Blue value: 36) \n" +
                "[1] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 60.34 Blue value: 36) <-> (Energy: 120.68 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 120.68 Blue value: 36) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 85.33 Blue value: 36) <-> (Energy: 85.34 Blue value: 36) <-> (Energy: 85.34 Blue value: 243) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 60.34 Blue value: 36) <-> (Energy: 120.68 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 120.68 Blue value: 36) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 85.34 Blue value: 36) <-> (Energy: 134.93 Blue value: 243) <-> (Energy: 120.68 Blue value: 36) <-> (Energy: 60.34 Blue value: 36) \n");

        Service testService3 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage3.png");
        testService3.getImage().updateEnergies();
        Assertions.assertThat(testService3.getImage().toString()).isEqualTo("[0] <-> (Energy: 67.45 Blue value: 234) <-> (Energy: 375.05 Blue value: 234) <-> (Energy: 190.68 Blue value: 153) <-> (Energy: 375.05 Blue value: 234) <-> (Energy: 67.45 Blue value: 234) \n" +
                "[1] <-> (Energy: 67.45 Blue value: 180) <-> (Energy: 407.74 Blue value: 180) <-> (Energy: 42.66 Blue value: 153) <-> (Energy: 407.74 Blue value: 180) <-> (Energy: 67.45 Blue value: 180) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 381.36 Blue value: 180) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 381.36 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 381.36 Blue value: 180) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 381.36 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 180) <-> (Energy: 301.49 Blue value: 180) <-> (Energy: 190.67 Blue value: 153) <-> (Energy: 301.49 Blue value: 180) <-> (Energy: 0.0 Blue value: 180) \n");
    }

    @Test
    void testToString() throws IOException{
        Service testService1 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage1.png");
        String testResult1 = testService1.getImage().toString();
        Assertions.assertThat(testResult1).isEqualTo("[0] <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) \n" +
                "[1] <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 153) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 255) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 153) <-> (Energy: 0.0 Blue value: 243) \n");

        double[][] testArray2 = {{5.0, 6.0, 3.0, 8.0}, {4.0, 1.0, 6.0, 4.0}, {3.0, 2.0, 1.0, 3.0}, {8.0, 6.0, 5.0, 2.0}};
        Image testImage2 = new Image(testArray2);
        String testResult2 = testImage2.toString();
        System.out.println(testResult2);
        Assertions.assertThat(testResult2).isEqualTo("[0] <-> (Energy: 5.0 Blue value: 0) <-> (Energy: 6.0 Blue value: 0) <-> (Energy: 3.0 Blue value: 0) <-> (Energy: 8.0 Blue value: 0) \n" +
                "[1] <-> (Energy: 4.0 Blue value: 0) <-> (Energy: 1.0 Blue value: 0) <-> (Energy: 6.0 Blue value: 0) <-> (Energy: 4.0 Blue value: 0) \n" +
                "[2] <-> (Energy: 3.0 Blue value: 0) <-> (Energy: 2.0 Blue value: 0) <-> (Energy: 1.0 Blue value: 0) <-> (Energy: 3.0 Blue value: 0) \n" +
                "[3] <-> (Energy: 8.0 Blue value: 0) <-> (Energy: 6.0 Blue value: 0) <-> (Energy: 5.0 Blue value: 0) <-> (Energy: 2.0 Blue value: 0) \n");

        Service testService3 = new Service("/Users/kmo/Documents/SCHOOL/Java/cs2510/lab10/src/images/testImage2.png");
        String testResult3 = testService3.getImage().toString();
        System.out.println(testResult3);
        Assertions.assertThat(testResult3).isEqualTo("[0] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) \n" +
                "[1] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) \n" +
                "[2] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) \n" +
                "[3] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) \n" +
                "[4] <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 243) <-> (Energy: 0.0 Blue value: 36) <-> (Energy: 0.0 Blue value: 36) \n");

    }

}
