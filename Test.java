import edu.princeton.cs.algs4.Picture;

public class Test {

  private static final int XADD = 0b00;
  private static final int YADD = 0b01;
  private static final int XSUB = 0b11;
  private static final int YSUB = 0b10;

  public static void main(String[] args) {
    System.out.println("Hello World");
    SeamCarver seamCarver = new SeamCarver(new Picture("example.jpg"));
    seamCarver.picture().show();
    for (int i = 0; i < 100; i++) {
      seamCarver.operate(XSUB);
      seamCarver.operate(YSUB);
    }
    seamCarver.picture().show();
    // for (int i = 0; i < 400; i++) seamCarver.operate(YADD);
    // seamCarver.picture().show();
    for (int i = 0; i < 200; i++) seamCarver.undo(true);
    seamCarver.picture().show();
    for (int i = 0; i < 200; i++) seamCarver.undo(false);
    seamCarver.picture().show();
  }
}
