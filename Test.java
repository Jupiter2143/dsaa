import edu.princeton.cs.algs4.Picture;

public class Test {
  public static void main(String[] args) {
    System.out.println("Hello World");
    SeamCarver seamCarver = new SeamCarver(new Picture("example.jpg"));
    Picture picture = seamCarver.picture();
    picture.show();
    for (int i = 0; i < 1; i++) {
      seamCarver.operate(0b00);
    }
    Picture newPicture = seamCarver.picture();
    newPicture.show();
  }
}
