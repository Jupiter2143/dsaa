package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;


public class Graph {
    //List of Pixels representing the left most column of the image
    private final ArrayList<Pixel> leftCol = new ArrayList<>();

    //Pixel class, each with left, right, and color values
    public static class Pixel {
        Pixel right;
        Pixel left;
        Color color;
        double energy;

        //Pixel Constructor
        public Pixel(Color color, double energy){
            this.color = color;
            this.right = null;
            this.left = null;
            this.energy = energy;

        }

        //Overloaded Constructor
        public Pixel(Color color, Pixel right, Pixel left, double energy){
            this.color = color;
            this.right = right;
            this.left = left;
            this.energy = energy;
        }
    }

    //Constructs a Graph object from a supplied bufferedImage
    public Graph (){
        Pixel previous = null;
        Pixel current = null;

        for (int y = 0; y <= 3; y++) {
            for (int x = 3; x >= 0; x--) {
                double[][] testArray = {{5.0, 6.0, 3.0, 8.0}, {4.0, 1.0, 6.0, 4.0}, {3.0, 2.0, 1.0, 3.0}, {8.0, 6.0, 5.0, 2.0}};
                current = new Pixel(Color.black, testArray[y][x]);
                current.right = previous;
                if (previous != null) {
                    previous.left = current;
                }
                previous = new Pixel(current.color, current.right, current.left, current.energy);
                System.out.println("current construct: "+current.energy);
                System.out.println("prev construct: "+previous.energy);
            }
            leftCol.add(y,current);
            System.out.println("number of rows: "+leftCol.size());
            previous = null;
        }
    }

    public ArrayList<Graph.Pixel> findSeam(){
        //not sure if I should use Collections.min or use these current if elif else statements
        ArrayList<Double> prevValues = new ArrayList<>();
        for (Graph.Pixel pixel: getFirstRowPixels()){
            prevValues.add(pixel.energy);
        }
        System.out.println(prevValues);
        ArrayList<Graph.Pixel> prevPixels = new ArrayList<>(getFirstRowPixels());
        ArrayList<ArrayList<Graph.Pixel>> prevSeams = new ArrayList<>();
        for (Pixel pixel: getFirstRowPixels()){
            ArrayList placeholder = new ArrayList<>();
            placeholder.add(pixel);
            prevSeams.add(placeholder);
        }
        ArrayList<Double> currentValues = new ArrayList<>();
        ArrayList<ArrayList<Graph.Pixel>> currentSeams = new ArrayList<>();
        for (int i=1; i< leftCol.size(); i++){
            Graph.Pixel rowStart = leftCol.get(i);
            Graph.Pixel current = rowStart;
            int nodeIndex = 0;

            while (current !=null){
                System.out.println("Pixel: " + current);
                if (current.left == null){
                    //not sure if to leave in equal
                    System.out.println(prevValues);
                    if (prevValues.get(nodeIndex) <= prevValues.get(nodeIndex + 1)) {
                        System.out.println(prevValues.get(nodeIndex) + " is the smallest");
                        currentValues.add(current.energy + prevValues.get(nodeIndex));
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                    else {
                        System.out.println(prevValues.get(nodeIndex + 1) + " is the smallest");
                        currentValues.add(current.energy + prevValues.get(nodeIndex+1));

                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex+1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                }
                else if (current.right == null){
                    if (prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex)) {
                        System.out.println(prevValues.get(nodeIndex - 1) + " is the smallest");
                        currentValues.add(current.energy + prevValues.get(nodeIndex-1));
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex-1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                    else {
                        System.out.println(prevValues.get(nodeIndex) + " is the smallest");
                        currentValues.add(current.energy + prevValues.get(nodeIndex));
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                }
                else {
                    //not sure if to leave in equal
                    if ((prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex)) && (prevValues.get(nodeIndex - 1) <= prevValues.get(nodeIndex + 1))){
                        System.out.println(prevValues.get(nodeIndex - 1) + " is the smallest (left is small)");
                        currentValues.add(current.energy + prevValues.get(nodeIndex-1));
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex-1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                    else if ((prevValues.get(nodeIndex) <= prevValues.get(nodeIndex - 1)) && (prevValues.get(nodeIndex) <= prevValues.get(nodeIndex + 1))){
                        System.out.println(prevValues.get(nodeIndex) + " is the smallest (middle small)");
                        currentValues.add(current.energy + prevValues.get(nodeIndex));
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                    else{
                        System.out.println(prevValues.get(nodeIndex+1) + " is the smallest (right small)");
                        currentValues.add(current.energy + prevValues.get(nodeIndex+1));
                        ArrayList<Pixel> prevSeam = new ArrayList<>(prevSeams.get(nodeIndex+1));
                        prevSeam.add(current);
                        currentSeams.add(prevSeam);
                        System.out.println("[Loop Seam start]");
                        System.out.println("prev same size: "+prevSeam.size());
                        System.out.println("seamer size: "+currentSeams.get(nodeIndex).size());
                        for (Pixel pixel: currentSeams.get(nodeIndex)){
                            System.out.println(pixel.energy);
                        }
                        System.out.println("actual list: "+prevSeams.get(nodeIndex));
                        System.out.println("[Loop Seam end]");
                    }
                }

                if (current.right == null) {
                    System.out.println("Last pixel in the row");
                }
                current = current.right;

                nodeIndex++;
            }
            prevSeams = (ArrayList)currentSeams.clone();
            currentSeams.clear();
            prevValues = (ArrayList)currentValues.clone();
            currentValues.clear();
            System.out.println(prevValues);
            System.out.println("[all the seams recorded]");
            for (ArrayList<Pixel> list: prevSeams){
                System.out.print("[");
                for (Pixel pixel: list){
                    System.out.print(pixel.energy + ", ");
                }
                System.out.println("]");
            }
            System.out.println("[End]");
        }
        //if I used Collections.min then there is no need for loop
//        int smallest = prevValues.get(0).intValue();
//        System.out.println(smallest);
//        for(int i=0; i<prevValues.size();i++){
//            System.out.println("smallest: "+smallest);
//            System.out.println("next comparison: "+prevValues.get(i));
//            if (prevValues.get(i) <= smallest) {
//                smallest = prevValues.get(i);
//                System.out.println("current smallest:"+smallest);
//            }
//        }
        int smallest = prevValues.indexOf(Collections.min(prevValues));
        double smallestReal = Collections.min(prevValues);
        System.out.println("smallest: "+smallestReal);
        System.out.println("index of the smallest: "+smallest);
        System.out.println("[Seam start]");
        for (Pixel pixel: prevSeams.get(smallest)){
            System.out.println(pixel.energy);
        }
        System.out.println("[Seam end]");
        return prevSeams.get(smallest);
    }

    public ArrayList<Graph.Pixel> getFirstRowPixels() {
        ArrayList<Graph.Pixel> firstRowPixels = new ArrayList<>();
        Graph.Pixel firstPixel = leftCol.get(0);
        while (firstPixel!= null){
            System.out.println("current one: " + firstPixel.energy);
            firstRowPixels.add(firstPixel);
            firstPixel = firstPixel.right;
        }
        return firstRowPixels;
    }

    //Creates temporary Pixel used to iterate without damaging data
//    private Pixel getTemp(Pixel p){
//        return new Pixel(p.color, p.right, p.left);
//    }

    //Gets height of grid
//    public int getHeight(){
//        return leftCol.size();
//    }
//
//    //Gets the width of grid
//    public int getWidth(){
//        int width = 0;
//        Pixel temp = getTemp(leftCol.getFirst());
//
//        while(temp != null){
//            temp = temp.right;
//            width++;
//        }
//        return width;
//    }
//
//    //Gets Pixel at given indices
//    public Pixel getAt(int x, int y){
//        Pixel temp = getTemp(leftCol.get(y));
//
//        if(x >= getWidth()) {return null;}
//
//        for(int i = 0; i<x; i++){temp = temp.right;}
//
//        return temp;
//    }
//
//    public void setLeftCol(int y, Pixel p){
//        leftCol.set(y,p);
//    }
//
//    //Returns a BufferedImage from a graph
//    public BufferedImage toBuff(){
//        int x = 0;
//        BufferedImage newImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//
//        for(int y = 0; y< leftCol.size(); y++){
//            Pixel temp = getTemp(leftCol.get(y));
//            while(temp != null){
//                //if(temp.inSeam == true){newImg.setRGB(x,y,);
//                newImg.setRGB(x,y, temp.color.getRGB());
//                x++;
//                temp = temp.right;
//            }
//        }
//
//        return newImg;
//    }

}
