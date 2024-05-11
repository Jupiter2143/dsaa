import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
// import edu.princeton.cs.algs4.StdOut;
import java.util.PriorityQueue;
import java.util.Stack;

public class SeamCarver {
  private Picture picture; // current pictured
  private Picture originPicture; // original picture
  private double[] energyMap; // energy map, 2d array to store the energy of the pixel
  private double[] Vcost; // 2d cumulative energy matrix to store the cost of the vertical seam
  private double[] Hcost; // 2d cumulative energy matrix to store the cost of the horizontal seam
  private int[] traceMatrix; // 2d matrix to store the trace of the seam
  private int HstrechLog = 0;
  private int VstrechLog = 0;
  private final Stack<int[]> undoSeamsStack = new Stack<>();
  private final Stack<int[]> redoSeamsStack = new Stack<>();
  private final Stack<Color[]> undoPixelsStack = new Stack<>();
  private final Stack<Color[]> redoPixelsStack = new Stack<>();
  // The oprands for the operation
  private static final int XADD = 0b00;
  private static final int YADD = 0b01;
  private static final int XSUB = 0b11;
  private static final int YSUB = 0b10;
  // operand stack for undo and redo
  private final Stack<Integer> undoStack = new Stack<>();
  private final Stack<Integer> redoStack = new Stack<>();

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    this.picture = picture;
    // init energy map
    this.energyMap = new double[picture.width() * picture.height()];
  }

  // current picture
  public Picture picture() {
    if (this.picture == null) {
      throw new IllegalArgumentException("Picture is null");
    }
    return this.picture;
  }

  // width of current picture
  public int width() {
    return this.picture.width();
  }

  // height of current picture
  public int height() {
    return this.picture.height();
  }

  // energy of pixel at column x and row y
  private double energy(int x, int y) {
    // return 0;
    if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
      return 1000;
    }

    Color top = this.picture.get(x, y + 1);
    Color bottom = this.picture.get(x, y - 1);
    Color left = this.picture.get(x - 1, y);
    Color right = this.picture.get(x + 1, y);

    return Math.sqrt(deltaSquare(top, bottom) + deltaSquare(left, right));
  }

  private static double deltaSquare(Color a, Color b) {
    return Math.pow(a.getRed() - b.getRed(), 2)
        + Math.pow(a.getGreen() - b.getGreen(), 2)
        + Math.pow(a.getBlue() - b.getBlue(), 2);
  }

  private void calEnergyMap() {
    for (int i = 0; i < picture.height(); i++) {
      for (int j = 0; j < picture.width(); j++) {
        energyMap[i * picture.width() + j] = energy(j, i);
      }
    }
  }

  // calculate Vcost matrix
  private void calVcost() {
    int width = picture.width();
    int height = picture.height();
    Vcost = new double[width * height];
    traceMatrix = new int[width * height];
    for (int i = 0; i < width; i++) {
      Vcost[i] = energyMap[i];
    }
    for (int i = 1; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (j == 0) {
          traceMatrix[i * width] = Vcost[(i - 1) * width] < Vcost[(i - 1) * width + 1] ? 0 : 1;
          Vcost[i * width] = energyMap[i * width] + Vcost[(i - 1) * width + traceMatrix[i * width]];
        } else if (j == width - 1) {
          traceMatrix[i * width + j] =
              Vcost[(i - 1) * width + j - 1] < Vcost[(i - 1) * width + j] ? -1 : 0;
          Vcost[i * width + j] =
              energyMap[i * width + j] + Vcost[(i - 1) * width + j + traceMatrix[i * width + j]];
        } else {
          traceMatrix[i * width + j] =
              minIndex(
                  Vcost[(i - 1) * width + j - 1],
                  Vcost[(i - 1) * width + j],
                  Vcost[(i - 1) * width + j + 1]);
          Vcost[i * width + j] =
              energyMap[i * width + j] + Vcost[(i - 1) * width + j + traceMatrix[i * width + j]];
        }
      }
    }
  }

  // calculate Hcost matrix
  private void calHcost() {
    int width = picture.width();
    int height = picture.height();
    Hcost = new double[width * height];
    traceMatrix = new int[width * height];
    for (int i = 0; i < height; i++) {
      Hcost[i * width] = energyMap[i * width];
    }
    for (int i = 1; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (j == 0) {
          traceMatrix[j * width + i] =
              Hcost[j * width + i - 1] < Hcost[(j + 1) * width + i - 1] ? 0 : 1;
          Hcost[j * width + i] =
              energyMap[j * width + i] + Hcost[j * width + i - 1 + traceMatrix[j * width + i]];
        } else if (j == height - 1) {
          traceMatrix[j * width + i] =
              Hcost[(j - 1) * width + i - 1] < Hcost[j * width + i - 1] ? -1 : 0;
          Hcost[j * width + i] =
              energyMap[j * width + i] + Hcost[j * width + i - 1 + traceMatrix[j * width + i]];
        } else {
          traceMatrix[j * width + i] =
              minIndex(
                  Hcost[(j - 1) * width + i - 1],
                  Hcost[j * width + i - 1],
                  Hcost[(j + 1) * width + i - 1]);
          Hcost[j * width + i] =
              energyMap[j * width + i] + Hcost[j * width + i - 1 + traceMatrix[j * width + i]];
        }
      }
    }
  }

  private static int minIndex(double a, double b, double c) {
    if (a < b) {
      if (a < c) {
        return -1;
      } else {
        return 1;
      }
    } else {
      if (b < c) {
        return 0;
      } else {
        return 1;
      }
    }
  }

  // trace back the Vcost matrix to find the kth energy-lowest seam
  public int[] findVseam(int k) {
    // return null;
    int height = picture.height();
    int width = picture.width();
    int[] seam = new int[height];
    // find the kth minimum number in the last row of Vcost
    PriorityQueue<Double> pq = new PriorityQueue<>();
    for (int i = 0; i < width; i++) {
      pq.add(Vcost[(height - 1) * width + i]);
    }
    for (int i = 0; i < k - 1; i++) {
      pq.poll();
    }
    for (int i = 0; i < width; i++)
      if (Vcost[(height - 1) * width + i] == pq.peek()) {
        seam[height - 1] = i;
        break;
      }
    for (int i = height - 2; i >= 0; i--)
      seam[i] = seam[i + 1] + traceMatrix[i * width + seam[i + 1]];
    return seam;
  }

  // trace back the Hcost matrix to find the kth energy-lowest seam
  public int[] findHseam(int k) {
    // return null;
    int height = picture.height();
    int width = picture.width();
    int[] seam = new int[width];
    // find the kth minimum number in the lasth column of Hcost
    PriorityQueue<Double> pq = new PriorityQueue<>();
    for (int i = 0; i < height; i++) {
      pq.add(Hcost[i * width + width - 1]);
    }
    for (int i = 0; i < k - 1; i++) {
      pq.poll();
    }
    for (int i = 0; i < height; i++)
      if (Hcost[i * width + width - 1] == pq.peek()) {
        seam[width - 1] = i;
        break;
      }
    for (int i = width - 2; i >= 0; i--)
      seam[i] = seam[i + 1] + traceMatrix[seam[i + 1] * width + i];
    return seam;
  }

  // direction: show the direction of the seam, true for horizontal, false for vertical
  private void insertPixels(boolean direction, int[] seam, Color[] pixels) {
    if (direction) {
      Picture newPicture = new Picture(picture.width(), picture.height() + 1);
      for (int x = 0; x < picture.width(); x++) {
        for (int y = 0; y < picture.height(); y++) {
          if (y < seam[x]) {
            newPicture.set(x, y, picture.get(x, y));
          } else if (y == seam[x]) {
            newPicture.set(x, y, pixels[x]);
            newPicture.set(x, y + 1, picture.get(x, y));
          } else {
            newPicture.set(x, y + 1, picture.get(x, y));
          }
        }
      }
      picture = newPicture;
    } else {
      Picture newPicture = new Picture(picture.width() + 1, picture.height());
      for (int y = 0; y < picture.height(); y++) {
        for (int x = 0; x < picture.width(); x++) {
          if (x < seam[y]) {
            newPicture.set(x, y, picture.get(x, y));
          } else if (x == seam[y]) {
            newPicture.set(x, y, pixels[y]);
            newPicture.set(x + 1, y, picture.get(x, y));
          } else {
            newPicture.set(x + 1, y, picture.get(x, y));
          }
        }
      }
      picture = newPicture;
    }
  }

  // direction: show the direction of the seam, true for horizontal, false for vertical
  private Color[] removePixels(boolean direction, int[] seam) {
    Color[] pixels = new Color[direction ? picture.width() : picture.height()];
    if (direction) {
      Picture newPicture = new Picture(picture.width(), picture.height() - 1);
      for (int x = 0; x < picture.width(); x++) {
        for (int y = 0; y < picture.height(); y++) {
          if (y < seam[x]) {
            newPicture.set(x, y, picture.get(x, y));
          } else if (y > seam[x]) {
            newPicture.set(x, y - 1, picture.get(x, y));
          } else {
            pixels[x] = picture.get(x, y);
          }
        }
      }
      picture = newPicture;
    } else {
      Picture newPicture = new Picture(picture.width() - 1, picture.height());
      for (int y = 0; y < picture.height(); y++) {
        for (int x = 0; x < picture.width(); x++) {
          if (x < seam[y]) {
            newPicture.set(x, y, picture.get(x, y));
          } else if (x > seam[y]) {
            newPicture.set(x - 1, y, picture.get(x, y));
          } else {
            pixels[y] = picture.get(x, y);
          }
        }
      }
      picture = newPicture;
    }
    return pixels;
  }

  // direction: show the direction of the strech, true for horizontal, false for vertical
  public void strech(boolean direction) {
    int[] seam;
    if (direction) {
      calEnergyMap();
      calVcost();
      seam = findVseam(HstrechLog * 2 + 1);
      HstrechLog++;
      Picture newPicture = new Picture(picture.width() + 1, picture.height());
      for (int y = 0; y < picture.height(); y++) {
        for (int x = 0; x < picture.width(); x++) {
          if (x < seam[y]) {
            newPicture.set(x, y, picture.get(x, y));
          } else if (x == seam[y]) {
            Color leftPixel;
            if (x == 0) {
              leftPixel = picture.get(x, y);
            } else {
              leftPixel = picture.get(x - 1, y);
            }
            Color rightPixel = picture.get(x, y);
            int avgRed = (leftPixel.getRed() + rightPixel.getRed()) / 2;
            int avgGreen = (leftPixel.getGreen() + rightPixel.getGreen()) / 2;
            int avgBlue = (leftPixel.getBlue() + rightPixel.getBlue()) / 2;
            Color avgColor = new Color(avgRed, avgGreen, avgBlue);
            newPicture.set(x, y, avgColor);
            newPicture.set(x + 1, y, picture.get(x, y));
          } else {
            newPicture.set(x + 1, y, picture.get(x, y));
          }
        }
      }
    } else {
      calEnergyMap();
      calHcost();
      seam = findHseam(VstrechLog * 2 + 1);
      VstrechLog++;
      Picture newPicture = new Picture(picture.width(), picture.height() + 1);
      for (int x = 0; x < picture.width(); x++) {
        for (int y = 0; y < picture.height(); y++) {
          if (y < seam[x]) {
            newPicture.set(x, y, picture.get(x, y));
          } else if (y == seam[x]) {
            Color topPixel;
            if (y == 0) {
              topPixel = picture.get(x, y);
            } else {
              topPixel = picture.get(x, y - 1);
            }
            Color bottomPixel = picture.get(x, y);
            int avgRed = (topPixel.getRed() + bottomPixel.getRed()) / 2;
            int avgGreen = (topPixel.getGreen() + bottomPixel.getGreen()) / 2;
            int avgBlue = (topPixel.getBlue() + bottomPixel.getBlue()) / 2;
            Color avgColor = new Color(avgRed, avgGreen, avgBlue);
            newPicture.set(x, y, avgColor);
            newPicture.set(x, y + 1, picture.get(x, y));
          } else {
            newPicture.set(x, y + 1, picture.get(x, y));
          }
        }
      }
    }
    undoSeamsStack.push(seam);
  }

  // direction: show the direction of the compress, true for horizontal, false for vertical
  public void compress(boolean direction) {
    HstrechLog = 0;
    int[] seam;
    Color[] pixels;
    if (direction) {
      calEnergyMap();
      calVcost();
      seam = findVseam(1);
      pixels = removePixels(false, seam);
    } else {
      calEnergyMap();
      calHcost();
      seam = findHseam(1);
      pixels = removePixels(true, seam);
    }
    undoSeamsStack.push(seam);
    undoPixelsStack.push(pixels);
  }

  public void operate(int op) {
    // horizontal or vertical
    boolean direction = (op == XADD || op == XSUB);
    // add or sub
    boolean add = (op == XADD || op == YADD);
    if (add) {
      strech(direction);
    } else {
      compress(direction);
    }
    undoStack.push(op);
    if (undoStack.size() > 50) {
      undoStack.remove(0);
    }
    redoStack.clear();
    redoSeamsStack.clear();
    redoPixelsStack.clear();
  }

  // true for undo, false for redo
  public void undo(boolean undo) {
    Stack<Integer> opStack = undo ? undoStack : redoStack;
    if (opStack.isEmpty()) {
      return;
    }
    Stack<int[]> seamsStacksFrom = undo ? undoSeamsStack : redoSeamsStack;
    Stack<Color[]> pixelsStackFrom = undo ? undoPixelsStack : redoPixelsStack;
    Stack<int[]> seamsStacksTo = undo ? redoSeamsStack : undoSeamsStack;
    Stack<Color[]> pixelsStackTo = undo ? redoPixelsStack : undoPixelsStack;
    int op = opStack.pop();
    boolean direction = (op == XADD || op == XSUB);
    boolean add = (op == XADD || op == YADD);
    if (undo ^ add) {
      // undo compress or redo strech
      // insert the seam
      int[] seam = seamsStacksFrom.pop();
      Color[] pixels = pixelsStackFrom.pop();
      insertPixels(!direction, seam, pixels);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    } else {
      // undo strech or redo compress
      // remove the seam
      int[] seam = seamsStacksFrom.pop();
      Color[] pixels = removePixels(!direction, seam);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    }
  }

  // unit testing (optional)
  public static void main(String[] args) {
    // StdOut.println("Hello World");
    // Picture picture = new Picture("example.jpg");
    // picture.show();
  }
}
