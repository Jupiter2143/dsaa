package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Graph {
    //List of Pixels representing the left most column of the image
    private final ArrayList<Pixel> leftCol = new ArrayList<>();

    //Pixel class, each with left, right, and color values
    public static class Pixel {
        Pixel right;
        Pixel left;
        Color color;
        int energy;

        //Pixel Constructor
        public Pixel(Color color){
            this.color = color;
            this.right = null;
            this.left = null;
            energy = 0;

        }

        //Overloaded Constructor
        public Pixel(Color color, Pixel right, Pixel left){
            this.color = color;
            this.right = right;
            this.left = left;
            energy = 0;
        }
    }

    //Constructs a Graph object from a supplied bufferedImage
    public Graph (BufferedImage img){
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
            leftCol.set(y,current);
            previous = null;
        }
    }

    //Creates temporary Pixel used to iterate without damaging data
    private Pixel getTemp(Pixel p){
        return new Pixel(p.color, p.right, p.left);
    }

    //Gets height of grid
    public int getHeight(){
        return leftCol.size();
    }

    //Gets the width of grid
    public int getWidth(){
        int width = 0;
        Pixel temp = getTemp(leftCol.getFirst());

        while(temp != null){
            temp = temp.right;
            width++;
        }
        return width;
    }

    //Gets Pixel at given indices
    public Pixel getAt(int x, int y){
        Pixel temp = getTemp(leftCol.get(y));

        if(x >= getWidth()) {return null;}

        for(int i = 0; i<x; i++){temp = temp.right;}

        return temp;
    }

    public void setLeftCol(int y, Pixel p){
        leftCol.set(y,p);
    }

    //Returns a BufferedImage from a graph
    public BufferedImage toBuff(){
        int x = 0;
        BufferedImage newImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y< leftCol.size(); y++){
            Pixel temp = getTemp(leftCol.get(y));
            while(temp != null){
                //if(temp.inSeam == true){newImg.setRGB(x,y,);
                newImg.setRGB(x,y, temp.color.getRGB());
                x++;
                temp = temp.right;
            }
        }

        return newImg;
    }

}
