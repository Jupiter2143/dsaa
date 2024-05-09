import edu.princeton.cs.algs4.Picture;
public class Test{
    public static void main(String[] args){
        System.out.println("Hello World");
        SeamCarver seamCarver = new SeamCarver(new Picture("example.jpg"));
        Picture picture = seamCarver.picture();
        picture.show();
    }
}