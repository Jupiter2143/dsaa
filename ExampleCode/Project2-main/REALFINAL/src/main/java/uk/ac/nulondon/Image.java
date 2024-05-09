package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Image {
    //List of Pixels representing the left most column of the image
    ArrayList<Pixel> leftCol = new ArrayList<>();

    //Pixel class, each with left, right, and color values
    public static class Pixel {
        Pixel right;
        Pixel left;
        Color color;
        double energy;

        /**
         * Pixel Constructor
         * @param color: represents the color of the pixel
         */
        public Pixel(Color color){
            this.color = color;
            this.right = null;
            this.left = null;
            energy = 0.0;

        }

        /**
         * Overloaded constructor used during testing, for graphs with preset energy values
         * @param color: represents the color of the pixel
         * @param energy: represents the energy of the pixel calculated by a method later
         */
        public Pixel(Color color, double energy){
            this.color = color;
            this.right = null;
            this.left = null;
            this.energy = energy;

        }

        /**
         * Overloaded Constructor used for both testing and actual use
         * @param color: represents the color of the pixel
         * @param right: the pixel to the right of the current pixel
         * @param left: the pixel to the left of the current pixel
         * @param energy: represents the energy of the pixel calculated by a method later
         */
        public Pixel(Color color, Pixel right, Pixel left, double energy){
            this.color = color;
            this.right = right;
            this.left = left;
            this.energy = energy;
        }

        /**
         * Overloaded constructor for actual use
         * @param color: represents the color of the pixel
         * @param right: the pixel to the right of the current pixel
         * @param left: the pixel to the left of the current pixel
         */
        public Pixel(Color color, Pixel right, Pixel left){
            this.color = color;
            this.right = right;
            this.left = left;
            energy = 0.0;
        }
    }

    /**
     * Image class constructor
     * @param img: buffered image that provides all rgb values
     * Constructs a Graph object from a supplied bufferedImage
     */
    public Image (BufferedImage img){

        Pixel previous = null;
        Pixel current = null;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = img.getWidth()-1; x >= 0; x--) {
                current = new Pixel(new Color(img.getRGB(x, y)));
                current.right = previous;
                if (previous != null) {
                    previous.left = current;
                }
                previous = new Pixel(current.color, current.right, current.left);
            }

            leftCol.add(y,current);
            previous = null;
        }
    }

    /**
     * test Overloaded constructor that gives a graph preset values and the color black, used for testing functions
     * @param sampleEnergies: a set of test energy values that can be used to create an Image
     */
    public Image (double[][] sampleEnergies){
        Pixel previous = null;
        Pixel current = null;

        for (int y = 0; y < sampleEnergies.length; y++) {
            for (int x = sampleEnergies[0].length-1; x >= 0; x--) {
                double[][] testArray = sampleEnergies;
                current = new Pixel(Color.black, testArray[y][x]);
                current.right = previous;
                if (previous != null) {
                    previous.left = current;
                }
                previous = new Pixel(current.color, current.right, current.left, current.energy);
//                System.out.println("current construct: "+current.energy);
//                System.out.println("prev construct: "+previous.energy);
            }
            leftCol.add(y,current);
//            System.out.println("number of rows: "+leftCol.size());
            previous = null;
        }
    }

    /**
     * another constructor used purely to test finding the bluest Seams. The input is a list of colors.
     * @param sampleColors: a set of test colors that can be used to create an Image
     */
    public Image (Color[][] sampleColors){
        Pixel previous = null;
        Pixel current = null;

        for (int y = 0; y < sampleColors.length; y++) {
            for (int x = sampleColors[0].length-1; x >= 0; x--) {
                Color[][] testArray = sampleColors;
                current = new Pixel(testArray[y][x]);
                current.right = previous;
                if (previous != null) {
                    previous.left = current;
                }
                previous = new Pixel(current.color, current.right, current.left);
//                System.out.println("current construct: "+current.energy);
//                System.out.println("prev construct: "+previous.energy);
            }
            leftCol.add(y,current);
//            System.out.println("number of rows: "+leftCol.size());
            previous = null;
        }
    }


    /**
     * Creates temporary Pixel used to iterate without damaging data
     */
    private Pixel getTemp(Pixel p){
        return new Pixel(p.color, p.right, p.left, p.energy);
    }

    //Gets height of grid
    /**
     * @return height of grid
     */
    public int getHeight(){
        return leftCol.size();
    }

    /**
     * @return the width of grid
     */
    public int getWidth(){
        int width = 0;
        Pixel temp = getTemp(leftCol.getFirst());

        while(temp != null){
            temp = temp.right;
            width++;
        }
        return width;
    }

    /**
     * @param x - x coordinate of the chosen pixel
     * @param y - y coordinate of the chosen pixel
     * @return the chosen pixel
     */
    public Pixel getAt(int x, int y){
        Pixel temp = getTemp(leftCol.get(y));

        if(x >= getWidth()) {return null;}

        for(int i = 0; i<x; i++){temp = temp.right;}

        return temp;
    }

    /**
     * sets a pixel p to the leftmost value of a row y
     */
    public void setLeftCol(int y, Pixel p){
        leftCol.set(y,p);
    }

    /**
     * @return a BufferedImage from a graph
     */
    public BufferedImage toBuff(){
        int x;
        BufferedImage newImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        System.out.println(getWidth() + ", " + getWidth());

        for(int y = 0; y< leftCol.size(); y++){
            Pixel temp = getTemp(leftCol.get(y));
            x = 0;
            while(temp != null){
                newImg.setRGB(x,y, temp.color.getRGB());
                x++;
                temp = temp.right;
            }
        }

        return newImg;
    }

    /**
     * overloaded toBuff that highlights the provided Pixels with the provided color
     * @param highlights: Pixels that will be highlighted
     * @param color: THe Highlight color
     * @return: A buffered image with highlight
     */
    public BufferedImage toBuff(ArrayList<Pixel> highlights, Color color){
        int x;
        BufferedImage newImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y< leftCol.size(); y++){
            Pixel temp = getTemp(leftCol.get(y));
            x = 0;
            while(temp != null){

                if(highlights.contains(temp)){
                    newImg.setRGB(x,y, color.getRGB());
                }
                else{newImg.setRGB(x,y, temp.color.getRGB());}
                x++;
                temp = temp.right;
            }
        }

        return newImg;
    }

    /**
     * Assigns every the energy values to every single Pixel
     */
    public void updateEnergies(){
        double [][] pixBr = new double[getHeight()][getWidth()];
        double horizE; double vertE;

        for(int y = 0; y<getHeight(); y++){
            int x = 0;
            Pixel temp = getTemp(leftCol.get(y));
            while(temp != null) {
                pixBr[y][x] = br(temp);
                temp = temp.right;
                x++;
            }
        }

        for(int y = 0; y < getHeight(); y++){
            int x = 0;
            Pixel temp = getTemp(leftCol.get(y));
            while(temp != null){
                horizE = 0.0;
                vertE = 0.0;

                try {
                    horizE += pixBr[y-1][x-1];
                    vertE += pixBr[y-1][x-1];
                } catch (Exception e) {
                    horizE += pixBr[y][x];
                    vertE += pixBr[y][x];
                }

                try {
                    vertE += 2 * pixBr[y-1][x];
                } catch (Exception e) {
                    vertE += 2 * pixBr[y][x];
                }

                try {
                    horizE -= pixBr[y-1][x+1];
                    vertE += pixBr[y-1][x+1];
                } catch (Exception e) {
                    horizE -= pixBr[y][x];
                    vertE += pixBr[y][x];
                }

                try {
                    horizE += 2 * pixBr[y][x-1];
                } catch (Exception e) {
                    horizE += 2 * pixBr[y][x];
                }

                try {
                    horizE -= 2 * pixBr[y][x+1];
                } catch (Exception e) {
                    horizE -= 2 * pixBr[y][x];
                }

                try {
                    horizE += pixBr[y+1][x-1];
                    vertE -= pixBr[y+1][x-1];
                } catch (Exception e) {
                    horizE += pixBr[y][x];
                    vertE -= pixBr[y][x];
                }

                try {
                    vertE -= 2 * pixBr[y+1][x];
                } catch (Exception e) {
                    vertE -= 2 * pixBr[y][x];
                }

                try {
                    horizE -= pixBr[y+1][x+1];
                    vertE -= pixBr[y+1][x+1];
                } catch (Exception e) {
                    horizE -= pixBr[y][x];
                    vertE -= pixBr[y][x];
                }

                double sqrt = Math.sqrt((horizE * horizE) + (vertE * vertE));
                temp.energy = (int)(sqrt * 100)/100.0;
                if(x == 0){
                    leftCol.get(y).energy = (int)(sqrt * 100)/100.0;
                }

                temp = temp.right;
                x++;
            }
        }
    }

    /**
     * Helper method that determines brightness of a pixel
     * @param p: Pixel of focus
     * @return Brightness
     */
    public Double br(Pixel p){
        return (int)(((p.color.getRed() + p.color.getBlue() + p.color.getGreen())/3.0)*100)/100.0;
    }

    /**
     * returns string format of the grid, for testing purposes
     */
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(int y = 0; y < leftCol.size(); y++){
            Pixel temp = getTemp(leftCol.get(y));
            str.append("[").append(y).append("]").append(" ");
            while(temp!= null){
                str.append("<-> (").append("Energy: ").append(temp.energy).append(" Blue value: ").append(temp.color.getBlue()).append(") ");
                temp = temp.right;
            }
            str.append("\n");
        }

        return str.toString();
    }
}
