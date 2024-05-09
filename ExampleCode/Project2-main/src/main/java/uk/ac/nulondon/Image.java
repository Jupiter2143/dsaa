package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Image {
    //List of Pixels representing the left most column of the image
    ArrayList<Pixel> leftCol = new ArrayList<>();

    //Pixel class, each with left, right, and color values
    public static class Pixel {
        Pixel right;
        Pixel left;
        Color color;
        double energy;

        //Pixel Constructor
        public Pixel(Color color){
            this.color = color;
            this.right = null;
            this.left = null;
            energy = 0;

        }

        //Overloaded Constructor
        public Pixel(Color color, Pixel right, Pixel left, double energy){
            this.color = color;
            this.right = right;
            this.left = left;
            this.energy = energy;
        }

        public Pixel(Color color, Pixel right, Pixel left){
            this.color = color;
            this.right = right;
            this.left = left;
            energy = 0.0;
        }
    }

    //Constructs a Graph object from a supplied bufferedImage
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


    //Creates temporary Pixel used to iterate without damaging data
    private Pixel getTemp(Pixel p){
        return new Pixel(p.color, p.right, p.left, p.energy);
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

    public Double br(Pixel p){
        return (int)(((p.color.getRed() + p.color.getBlue() + p.color.getGreen())/3.0)*100)/100.0;
    }


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
///Users/bwelsh/proj2/src/main/resources/beach.png
}
