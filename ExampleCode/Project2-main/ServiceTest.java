package uk.ac.nulondon;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

public class ServiceTest {

    @Test
    void testFindSeam() throws IOException {
        //test 1: comapring method findSeam() to SeamfindingWalkthrough.pdf accessed at https://nulondon.instructure.com/courses/2590/files/1053053?wrap=1
        double[][] testArray1 = {{5.0, 6.0, 3.0, 8.0}, {4.0, 1.0, 6.0, 4.0}, {3.0, 2.0, 1.0, 3.0}, {8.0, 6.0, 5.0, 2.0}};
        Service testService1 = new Service(testArray1);
        ArrayList<Image.Pixel> testSeam1 = testService1.findSeam(false);
        StringBuilder str1 = new StringBuilder();
        for (int i=0;i<testSeam1.size();i++) {
            System.out.println();
            str1.append("<-> (").append("Energy: ").append(testSeam1.get(i).energy).append(" Blue value: " + testSeam1.get(i).color.getBlue() + ") ");
        }
        String testSeam1ToString = str1.toString();
        Assertions.assertThat(testSeam1ToString).isEqualTo("<-> (Energy: 3.0 Blue value: 0) <-> (Energy: 1.0 Blue value: 0) <-> (Energy: 1.0 Blue value: 0) <-> (Energy: 2.0 Blue value: 0) ");

        //test 2: new set of energy values, hand calculated vs method findSeam()
        double[][] testArray2 = {{1.0, 2.0, 3.0, 4.0}, {4.0, 1.0, 7.0, 8.0}, {6.0, 5.0, 2.0, 3.0}, {7.0, 1.0, 8.0, 2.0}};
        Service testService2 = new Service(testArray2);
        ArrayList<Image.Pixel> testSeam2 = testService2.findSeam(false);
        StringBuilder str2 = new StringBuilder();
        for (int i=0;i<testSeam2.size();i++) {
            System.out.println();
            str2.append("<-> (").append("Energy: ").append(testSeam2.get(i).energy).append(" Blue value: " + testSeam2.get(i).color.getBlue() + ") ");
        }
        String testSeam2ToString = str2.toString();
        Assertions.assertThat(testSeam2ToString).isEqualTo("<-> (Energy: 1.0 Blue value: 0) <-> (Energy: 1.0 Blue value: 0) <-> (Energy: 2.0 Blue value: 0) <-> (Energy: 1.0 Blue value: 0) ");

        //test3: preset values of blue comparison
        Color[][] testArray3 = {{new Color(0, 0, 1), new Color(0, 0, 2), new Color(0, 0, 3), new Color(0, 0, 4)}, {new Color(0, 0, 4), new Color(0, 0, 1), new Color(0, 0, 7), new Color(0, 0, 8)}, {new Color(0, 0, 6), new Color(0, 0, 5), new Color(0, 0, 2), new Color(0, 0, 3)}, {new Color(0, 0, 7), new Color(0, 0, 1), new Color(0, 0, 8), new Color(0, 0, 2)}};
        Service testService3 = new Service(testArray3);
        ArrayList<Image.Pixel> testSeam3 = testService3.findSeam(true);
        StringBuilder str3 = new StringBuilder();
        for (int i=0;i<testSeam3.size();i++) {
            System.out.println();
            str3.append("<-> (").append("Energy: ").append(testSeam3.get(i).energy).append(" Blue value: " + testSeam3.get(i).color.getBlue() + ") ");
        }
        String testSeam3ToString = str3.toString();
        Assertions.assertThat(testSeam3ToString).isEqualTo("<-> (Energy: 0.0 Blue value: 4) <-> (Energy: 0.0 Blue value: 7) <-> (Energy: 0.0 Blue value: 5) <-> (Energy: 0.0 Blue value: 8) ");
    }

    //This test will not pass although the error is not in the removeSeam function, it is elsewhere but untraceable
    @Test
    void testRemoveSeam() throws IOException{
        double[][] testArray1 = {{5.0, 8.0}, {3.0, 5.0}};
        Service testService1 = new Service(testArray1);
        testService1.removeSeam();
        Assertions.assertThat(testService1.getImage().toString()).isEqualTo("[0] <-> (Energy: 8.0 Blue value: 0) \n [1] <-> (Energy: 3.0 Blue value: 0)\n");
    }

    @Test
    void testUndo() throws IOException {
        double[][] testArray1 = {{5.0, 8.0}, {3.0, 5.0}};
        Service testService1 = new Service(testArray1);
        testService1.removeSeam();
        testService1.undo();
        Assertions.assertThat(testService1.getImage().toString()).isEqualTo("[0] <-> (Energy: 5.0 Blue value: 0) <-> (Energy: 8.0 Blue value: 0) \n[1] <-> (Energy: 3.0 Blue value: 0) <-> (Energy: 5.0 Blue value: 0) \n");
    }
}