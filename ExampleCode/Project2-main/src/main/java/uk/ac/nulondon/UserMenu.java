package uk.ac.nulondon;

import java.awt.*;
import java.io.IOException;
import java.util.Scanner;
public class UserMenu {

    //represents the image to be operated on
    private static Service img;

    //Choice of the user (random, blue, undo, quit)
    private static String choice = "invalid";

    //Boolean value to track whether a removal can be made
    private boolean readyToRemove = false;

    /**
     * Prints the options the user chooses from
     */
    public static void printMenu() {
        System.out.println("Please enter a command");
        System.out.println("b - Highlight the bluest column");
        System.out.println("e - Highlight the seam with lowest energy");
        System.out.println("d - Delete the highlighted seam");
        System.out.println("u - Undo a previous edit");
        System.out.println("q - Quit");
    }

    /**
     * Print a response to the user, given their selection
     * @param selection the String value of the user's choice
     */
    public static void printResponse(String selection) throws IOException{
        switch(selection) {
            case "b":
                img.findSeam(true);
                img.highlight(Color.BLUE);
                System.out.println("Remove the bluest seam. Select (d) to confirm, any other key to cancel");
                break;
            case "e":
                img.findSeam(false);
                img.highlight(Color.RED);
                System.out.println("Remove the lowest energy seam. Select (d) to confirm, any other key to cancel");
                break;
            case "d":
                System.out.println("Please make a selection first.");
                break;
            case "u":
                //img.undo();
                System.out.println("Last edit restored.");
                break;
            case "q":
                System.out.println("Thanks for playing.");
                break;
            default:
                System.out.println("That is not a valid option.");
                choice = "invalid";
                break;
        }
    }

    /**
     * Main method where other methods in UserMenu and Image are called
     * @throws IOException  Makes sure the ImageIO class can operate properly within function
     */
    public static void main(String[] args) throws IOException{
        // keep track of if we want to keep the program running
        boolean shouldQuit = false;
        //Reads user input
        Scanner scan = new Scanner(System.in);
        //Keeps track if file path works
        boolean validFile = false;

        //prompts user to enter file path, retrying until valid path is entered
        while(!validFile) {
            System.out.println("Welcome! Enter file path");
            try {
                //img = new Service(scan.nextLine());
                img = new Service("/Users/bwelsh/proj2/src/main/resources/beach.png");
                validFile = true;
            } catch (Exception e) {

                System.out.println("Input should be a valid file path");
            }
        }

        //Cycle through options & confirmations until told to quit
        while(!shouldQuit){
            choice = "invalid";
            while(choice.equals("invalid")) {
                // display options to the user
                printMenu();
                //reads next input as choice
                choice = scan.next().toLowerCase();
                //prints responses to the choice
                printResponse(choice);
            }

            if(choice.equals("e") || choice.equals("b")){
                choice = scan.next().toLowerCase();
                if(choice.equals("d")){
                    img.removeSeam();
                    System.out.println("Seam Removed.");
                }
                else{
                    System.out.println("Operation canceled");
                }
            }

            //If choice is quit, terminate function
            if(choice.equals("q")){
                shouldQuit = true;
            }
        }
        img.finalImg();
        scan.close();
    }
}
