package uk.ac.nulondon;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

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

    @Test
    void testUpdateEnergies(){

    }



}
