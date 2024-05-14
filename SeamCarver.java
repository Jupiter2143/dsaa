import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.PriorityQueue;
import java.util.Stack;

public class SeamCarver {
  private Picture picture;
  private Picture originPicture; // original picture
  private double[][] energyMap; // energy map, 2d array to store the energy of the pixel
  private double[][] mask;
  // private double[][] split;
  private boolean maskFlag = false;
  private boolean splitFlag = false;
  private double[][] maskedEnergyMap;
  private double[][] Vcost; // 2d cumulative energy matrix to store the cost of the vertical seam
  private double[][] Hcost; // 2d cumulative energy matrix to store the cost of the horizontal seam
  private int[][] traceMatrix; // 2d matrix to store the trace of the seam
  public int width = 0;
  public int height = 0;
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
    this.originPicture = new Picture(picture);
    this.picture = new Picture(picture);
    this.width = picture.width();
    this.height = picture.height();
    calEnergyMap();
    // init energy map
  }

  // current picture
  public Picture picture() {
    if (this.picture == null) {
      throw new IllegalArgumentException("Picture is null");
    }
    return this.picture;
  }

  // energy of pixel at column x and row y
  private double energy(int x, int y) {
    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
      return 1000;
    }

    Color top = picture.get(x, y + 1);
    Color bottom = picture.get(x, y - 1);
    Color left = picture.get(x - 1, y);
    Color right = picture.get(x + 1, y);

    return Math.sqrt(deltaSquare(top, bottom) + deltaSquare(left, right));
  }

  private static double deltaSquare(Color a, Color b) {
    return Math.pow(a.getRed() - b.getRed(), 2)
        + Math.pow(a.getGreen() - b.getGreen(), 2)
        + Math.pow(a.getBlue() - b.getBlue(), 2);
  }

  private void calEnergyMap() {
    energyMap = new double[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        energyMap[y][x] = energy(x, y);
      }
    }
  }

  // calculate Vcost matrix
  private void calVcost() {
    Vcost = new double[height][width];
    traceMatrix = new int[height][width];
    maskedEnergyMap = new double[height][width];
    // maskedEnergyMap = EnergyMap+mask+split
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        maskedEnergyMap[y][x] = energyMap[y][x];
        if (maskFlag) {
          maskedEnergyMap[y][x] += mask[y][x];
        }
        // if (splitFlag) {
        //   maskedEnergyMap[y][x] += split[y][x];
        // }
      }
    }

    for (int x = 0; x < width; x++) {
      Vcost[0][x] = maskedEnergyMap[0][x];
    }
    for (int y = 1; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (x == 0) {
          traceMatrix[y][x] = Vcost[y - 1][x] < Vcost[y - 1][x + 1] ? 0 : 1;
          Vcost[y][x] = maskedEnergyMap[y][x] + Vcost[y - 1][x + traceMatrix[y][x]];
        } else if (x == width - 1) {
          traceMatrix[y][x] = Vcost[y - 1][x - 1] < Vcost[y - 1][x] ? -1 : 0;
          Vcost[y][x] = maskedEnergyMap[y][x] + Vcost[y - 1][x + traceMatrix[y][x]];
        } else {
          traceMatrix[y][x] = minIndex(Vcost[y - 1][x - 1], Vcost[y - 1][x], Vcost[y - 1][x + 1]);
          Vcost[y][x] = maskedEnergyMap[y][x] + Vcost[y - 1][x + traceMatrix[y][x]];
        }
      }
    }
  }

  // calculate Hcost matrix
  private void calHcost() {
    Hcost = new double[height][width];
    traceMatrix = new int[height][width];
    for (int y = 0; y < height; y++) {
      Hcost[y][0] = energyMap[y][0];
    }
    for (int x = 1; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (y == 0) {
          traceMatrix[y][x] = Hcost[y][x - 1] < Hcost[y + 1][x - 1] ? 0 : 1;
          Hcost[y][x] = energyMap[y][x] + Hcost[y + traceMatrix[y][x]][x - 1];
        } else if (y == height - 1) {
          traceMatrix[y][x] = Hcost[y - 1][x - 1] < Hcost[y][x - 1] ? -1 : 0;
          Hcost[y][x] = energyMap[y][x] + Hcost[y + traceMatrix[y][x]][x - 1];
        } else {
          traceMatrix[y][x] = minIndex(Hcost[y - 1][x - 1], Hcost[y][x - 1], Hcost[y + 1][x - 1]);
          // System.out.println("traceMatrix[y][x]: " + traceMatrix[y][x]);
          Hcost[y][x] = energyMap[y][x] + Hcost[y + traceMatrix[y][x]][x - 1];
        }
      }
    }
  }

  // find the index of the minimum value of a, b, c
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
    int[] seam = new int[height];
    PriorityQueue<Double> pq = new PriorityQueue<>();
    for (int x = 0; x < width; x++) {
      pq.add(Vcost[height - 1][x]);
    }
    for (int i = 0; i < k - 1; i++) {
      pq.poll();
    }
    double kMin = pq.poll();
    for (int x = 0; x < width; x++) {
      if (Vcost[height - 1][x] == kMin) {
        seam[height - 1] = x;
        break;
      }
    }
    for (int y = height - 2; y >= 0; y--) {
      seam[y] = seam[y + 1] + traceMatrix[y + 1][seam[y + 1]];
    }
    return seam;
  }

  // trace back the Hcost matrix to find the kth energy-lowest seam
  public int[] findHseam(int k) {
    int[] seam = new int[width];
    PriorityQueue<Double> pq = new PriorityQueue<>();
    for (int y = 0; y < height; y++) {
      pq.add(Hcost[y][width - 1]);
    }
    for (int i = 0; i < k - 1; i++) {
      pq.poll();
    }
    double kMin = pq.poll();
    for (int y = 0; y < height; y++) {
      if (Hcost[y][width - 1] == kMin) {
        seam[width - 1] = y;
        break;
      }
    }
    for (int x = width - 2; x >= 0; x--) {
      seam[x] = seam[x + 1] + traceMatrix[seam[x + 1]][x + 1];
    }
    return seam;
  }

  // XADD or YADD
  private void insertPixels(int op, int[] seam, Color[] pixels) {
    if (op == XADD) {
      Picture newPicture = new Picture(width + 1, height);
      for (int y = 0; y < height; y++)
        for (int x = 0; x < width; x++)
          if (x < seam[y]) newPicture.set(x, y, picture.get(x, y));
          else if (x == seam[y]) {
            newPicture.set(x, y, pixels[y]);
            newPicture.set(x + 1, y, picture.get(x, y));
          } else newPicture.set(x + 1, y, picture.get(x, y));
      picture = newPicture;
      width++;
    } else {
      Picture newPicture = new Picture(width, height + 1);
      for (int x = 0; x < width; x++)
        for (int y = 0; y < height; y++)
          if (y < seam[x]) newPicture.set(x, y, picture.get(x, y));
          else if (y == seam[x]) {
            newPicture.set(x, y, pixels[x]);
            newPicture.set(x, y + 1, picture.get(x, y));
          } else newPicture.set(x, y + 1, picture.get(x, y));
      picture = newPicture;
      height++;
    }
    updateEnergyMap(op, seam);
  }

  // XSUB or YSUB
  private Color[] removePixels(int op, int[] seam) {
    Color[] pixels = new Color[(op == XSUB) ? height : width];
    if (op == XSUB) {
      Picture newPicture = new Picture(width - 1, height);
      for (int y = 0; y < height; y++)
        for (int x = 0; x < width; x++)
          if (x < seam[y]) newPicture.set(x, y, picture.get(x, y));
          else if (x > seam[y]) newPicture.set(x - 1, y, picture.get(x, y));
          else {
            pixels[y] = picture.get(x, y);
          }
      picture = newPicture;
      width--;
    } else {
      Picture newPicture = new Picture(width, height - 1);
      for (int x = 0; x < width; x++)
        for (int y = 0; y < height; y++)
          if (y < seam[x]) newPicture.set(x, y, picture.get(x, y));
          else if (y > seam[x]) newPicture.set(x, y - 1, picture.get(x, y));
          else {
            pixels[x] = picture.get(x, y);
          }
      picture = newPicture;
      height--;
    }
    updateEnergyMap(op, seam);
    return pixels;
  }

  private void updateEnergyMap(int op, int[] seam) {
    double[][] newEnergyMap = new double[height][width];
    double splitEnergy = splitFlag ? 1000 : 0;
    if (op == XADD) {
      for (int y = 0; y < height; y++)
        for (int x = 0; x < width; x++)
          if (x < seam[y] - 1) newEnergyMap[y][x] = energyMap[y][x];
          else if (x > seam[y] + 1) newEnergyMap[y][x] = energyMap[y][x - 1];
          else newEnergyMap[y][x] = splitFlag ? energyMap[y][seam[y]] + splitEnergy : energy(x, y);
    } else if (op == XSUB) {
      for (int y = 0; y < height; y++)
        for (int x = 0; x < width; x++)
          if (x < seam[y] - 1) newEnergyMap[y][x] = energyMap[y][x];
          else if (x > seam[y]) newEnergyMap[y][x] = energyMap[y][x + 1];
          else newEnergyMap[y][x] = energy(x, y);
    } else if (op == YADD) {
      for (int x = 0; x < width; x++)
        for (int y = 0; y < height; y++)
          if (y < seam[x] - 1) newEnergyMap[y][x] = energyMap[y][x];
          else if (y > seam[x] + 1) newEnergyMap[y][x] = energyMap[y - 1][x];
          else {
            // System.out.println("width: " + width + ",seam.length: " + seam.length);
            // System.out.println("height: " + height + ",seam[x]: " + seam[x]);
            newEnergyMap[y][x] = splitFlag ? energyMap[seam[x]][x] + splitEnergy : energy(x, y);
          }
    } else {
      for (int x = 0; x < width; x++)
        for (int y = 0; y < height; y++)
          if (y < seam[x] - 1) newEnergyMap[y][x] = energyMap[y][x];
          else if (y > seam[x]) newEnergyMap[y][x] = energyMap[y + 1][x];
          else newEnergyMap[y][x] = energy(x, y);
    }
    energyMap = newEnergyMap;
  }

  // XADD or YADD
  public void strech(int op) {
    Color left;
    Color top;
    Color here;
    Color avg;
    splitFlag = true;
    if (op == XADD) {
      Color[] pixels = new Color[height];
      calVcost();
      int[] seam = findVseam(1);
      // get the pixel to be inserted
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (x == seam[y]) {
            if (x == 0) left = picture.get(x, y);
            else left = picture.get(x - 1, y);
            here = picture.get(x, y);
            avg =
                new Color(
                    (left.getRed() + here.getRed()) / 2,
                    (left.getGreen() + here.getGreen()) / 2,
                    (left.getBlue() + here.getBlue()) / 2);
            pixels[y] = avg;
          }
        }
      }
      insertPixels(op, seam, pixels);
      undoSeamsStack.push(seam);
    } else {
      Color[] pixels = new Color[width];
      calHcost();
      int[] seam = findHseam(1);
      // get the pixel to be inserted
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          if (y == seam[x]) {
            if (y == 0) top = picture.get(x, y);
            else top = picture.get(x, y - 1);
            here = picture.get(x, y);
            avg =
                new Color(
                    (top.getRed() + here.getRed()) / 2,
                    (top.getGreen() + here.getGreen()) / 2,
                    (top.getBlue() + here.getBlue()) / 2);
            pixels[x] = avg;
          }
        }
      }
      insertPixels(op, seam, pixels);
      undoSeamsStack.push(seam);
    }
  }

  // XSUB or YSUB
  public void compress(int op) {
    int[] seam;
    Color[] pixels;
    if (splitFlag) {
      calEnergyMap();
      splitFlag = false;
    }
    if (op == XSUB) {
      calVcost();
      seam = findVseam(1);
      pixels = removePixels(op, seam);
    } else {
      calHcost();
      seam = findHseam(1);
      pixels = removePixels(op, seam);
    }
    undoSeamsStack.push(seam);
    undoPixelsStack.push(pixels);
  }

  public void operate(int op) {
    if (op == XADD || op == YADD) {
      strech(op);
    } else {
      compress(op);
    }
    undoStack.push(op);
    redoStack.clear();
    redoSeamsStack.clear();
    redoPixelsStack.clear();
  }

  // true for undo, false for redo
  public void undo(boolean undo) {
    Stack<Integer> opStackFrom = undo ? undoStack : redoStack;
    Stack<Integer> opStackTo = undo ? redoStack : undoStack;
    if (opStackFrom.isEmpty()) {
      return;
    }
    Stack<int[]> seamsStacksFrom = undo ? undoSeamsStack : redoSeamsStack;
    Stack<Color[]> pixelsStackFrom = undo ? undoPixelsStack : redoPixelsStack;
    Stack<int[]> seamsStacksTo = undo ? redoSeamsStack : undoSeamsStack;
    Stack<Color[]> pixelsStackTo = undo ? redoPixelsStack : undoPixelsStack;
    int op = opStackFrom.pop();
    int invOp = ~op & 0b11;
    boolean add = (op == XADD || op == YADD);
    boolean direct = (op == XADD || op == XSUB);
    int[] seam = seamsStacksFrom.pop();
    if (undo ^ add) {
      // undo compress or redo strech: undo+XSUP or redo+XADD
      // insert the seam
      Color[] pixels = pixelsStackFrom.pop();
      insertPixels(direct ? XADD : YADD, seam, pixels);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    } else {
      // undo strech or redo compress: undo+XADD or redo+XSUB
      // remove the seam
      System.out.println("undo: " + undo + ", add: " + add);
      Color[] pixels = removePixels(direct ? XSUB : YSUB, seam);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    }
    opStackTo.push(op);
  }

  // unit testing (optional)
  public static void main(String[] args) {}
}
