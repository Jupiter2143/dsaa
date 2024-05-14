import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.Stack;

public class SeamCarver implements ISeamCarver {
  private Picture picture;
  private Picture originPicture; // original picture
  private double[][] energyMap; // energy map, 2d array to store the energy of the pixel
  private double[][] mask;
  private boolean maskFlag = false;
  private boolean splitFlag = false;
  private double[][] maskedEnergyMap;
  private double[][] Vcost; // 2d cumulative energy matrix to store the cost of the vertical seam
  private double[][] Hcost; // 2d cumulative energy matrix to store the cost of the horizontal seam
  private int[][] traceMatrix; // 2d matrix to store the trace of the seam
  private int width;
  private int height;
  // operand stack for undo and redo
  private final Stack<Integer> undoStack = new Stack<>();
  private final Stack<Integer> redoStack = new Stack<>();
  private final Stack<int[]> undoSeamsStack = new Stack<>();
  private final Stack<int[]> redoSeamsStack = new Stack<>();
  private final Stack<Color[]> undoPixelsStack = new Stack<>();
  private final Stack<Color[]> redoPixelsStack = new Stack<>();
  // The oprands for the operation
  private static final int XADD = 0b00;
  private static final int YADD = 0b01;
  private static final int XSUB = 0b11;
  private static final int YSUB = 0b10;

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    this.originPicture = new Picture(picture);
    this.picture = new Picture(picture);
    this.width = picture.width();
    this.height = picture.height();
    calEnergyMap();
  }

  // current picture
  @Override
  public Picture picture() {
    if (this.picture == null) {
      throw new IllegalArgumentException("Picture is null");
    }
    return this.picture;
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  // energy of pixel at column x and row y
  private double energy(int x, int y) {
    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return 1000;
    Color top = picture.get(x, y + 1);
    Color bottom = picture.get(x, y - 1);
    Color left = picture.get(x - 1, y);
    Color right = picture.get(x + 1, y);
    return Math.sqrt(Utils.deltaSquare(top, bottom) + Utils.deltaSquare(left, right));
  }

  private void calEnergyMap() {
    energyMap = new double[height][width];
    Utils.parallel(
        (cpu, cpus) -> {
          for (int y = cpu; y < height; y += cpus)
            for (int x = 0; x < width; x++) energyMap[y][x] = energy(x, y);
        });
  }

  private void maskMap() {
    Utils.parallel(
        (cpu, cpus) -> {
          for (int y = cpu; y < height; y += cpus)
            for (int x = 0; x < width; x++) maskedEnergyMap[y][x] = energyMap[y][x] + mask[y][x];
        });
  }

  // calculate Vcost matrix
  private void calVcost() {
    Vcost = new double[height][width];
    traceMatrix = new int[height][width];
    if (maskFlag) maskMap();
    else maskedEnergyMap = energyMap;
    for (int x = 0; x < width; x++) Vcost[0][x] = maskedEnergyMap[0][x];
    for (int y = 1; y < height; y++)
      for (int x = 0; x < width; x++)
        if (x == 0) {
          traceMatrix[y][x] = Vcost[y - 1][x] < Vcost[y - 1][x + 1] ? 0 : 1;
          Vcost[y][x] = maskedEnergyMap[y][x] + Vcost[y - 1][x + traceMatrix[y][x]];
        } else if (x == width - 1) {
          traceMatrix[y][x] = Vcost[y - 1][x - 1] < Vcost[y - 1][x] ? -1 : 0;
          Vcost[y][x] = maskedEnergyMap[y][x] + Vcost[y - 1][x + traceMatrix[y][x]];
        } else {
          traceMatrix[y][x] =
              Utils.minIndex(Vcost[y - 1][x - 1], Vcost[y - 1][x], Vcost[y - 1][x + 1]);
          Vcost[y][x] = maskedEnergyMap[y][x] + Vcost[y - 1][x + traceMatrix[y][x]];
        }
  }

  // calculate Hcost matrix
  private void calHcost() {
    Hcost = new double[height][width];
    traceMatrix = new int[height][width];
    if (maskFlag) maskMap();
    else maskedEnergyMap = energyMap;
    for (int y = 0; y < height; y++) Hcost[y][0] = maskedEnergyMap[y][0];
    for (int x = 1; x < width; x++)
      for (int y = 0; y < height; y++)
        if (y == 0) {
          traceMatrix[y][x] = Hcost[y][x - 1] < Hcost[y + 1][x - 1] ? 0 : 1;
          Hcost[y][x] = maskedEnergyMap[y][x] + Hcost[y + traceMatrix[y][x]][x - 1];
        } else if (y == height - 1) {
          traceMatrix[y][x] = Hcost[y - 1][x - 1] < Hcost[y][x - 1] ? -1 : 0;
          Hcost[y][x] = maskedEnergyMap[y][x] + Hcost[y + traceMatrix[y][x]][x - 1];
        } else {
          traceMatrix[y][x] =
              Utils.minIndex(Hcost[y - 1][x - 1], Hcost[y][x - 1], Hcost[y + 1][x - 1]);
          Hcost[y][x] = maskedEnergyMap[y][x] + Hcost[y + traceMatrix[y][x]][x - 1];
        }
  }

  // trace back the Vcost matrix to find the lowest energy seam
  private int[] findVseam() {
    int[] seam = new int[height];
    int index = 0;
    double min = Vcost[height - 1][0];
    for (int x = 1; x < width; x++)
      if (Vcost[height - 1][x] < min) {
        min = Vcost[height - 1][x];
        index = x;
      }
    seam[height - 1] = index;
    for (int y = height - 2; y >= 0; y--) seam[y] = seam[y + 1] + traceMatrix[y + 1][seam[y + 1]];
    return seam;
  }

  // trace back the Hcost matrix to find the lowest energy seam
  private int[] findHseam() {
    int[] seam = new int[width];
    int index = 0;
    double min = Hcost[0][width - 1];
    for (int y = 1; y < height; y++)
      if (Hcost[y][width - 1] < min) {
        min = Hcost[y][width - 1];
        index = y;
      }
    seam[width - 1] = index;
    for (int x = width - 2; x >= 0; x--) seam[x] = seam[x + 1] + traceMatrix[seam[x + 1]][x + 1];
    return seam;
  }

  // XADD or YADD
  private void insertPixels(int op, int[] seam, Color[] pixels) {
    if (op == XADD) {
      Picture newPicture = new Picture(width + 1, height);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++)
                if (x < seam[y]) newPicture.set(x, y, picture.get(x, y));
                else if (x == seam[y]) {
                  newPicture.set(x, y, pixels[y]);
                  newPicture.set(x + 1, y, picture.get(x, y));
                } else newPicture.set(x + 1, y, picture.get(x, y));
          });
      picture = newPicture;
      width++;
    } else {
      Picture newPicture = new Picture(width, height + 1);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                if (y < seam[x]) newPicture.set(x, y, picture.get(x, y));
                else if (y == seam[x]) {
                  newPicture.set(x, y, pixels[x]);
                  newPicture.set(x, y + 1, picture.get(x, y));
                } else newPicture.set(x, y + 1, picture.get(x, y));
          });
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
      Utils.parallel(
          (cpu, cpus) -> {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++)
                if (x < seam[y]) newPicture.set(x, y, picture.get(x, y));
                else if (x > seam[y]) newPicture.set(x - 1, y, picture.get(x, y));
                else pixels[y] = picture.get(x, y);
          });
      picture = newPicture;
      width--;
    } else {
      Picture newPicture = new Picture(width, height - 1);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                if (y < seam[x]) newPicture.set(x, y, picture.get(x, y));
                else if (y > seam[x]) newPicture.set(x, y - 1, picture.get(x, y));
                else pixels[x] = picture.get(x, y);
          });
      picture = newPicture;
      height--;
    }
    updateEnergyMap(op, seam);
    return pixels;
  }

  private void updateEnergyMap(int op, int[] seam) {
    double[][] newEnergyMap = new double[height][width];
    Utils.parallel(
        (cpu, cpus) -> {
          if (op == XADD) {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++)
                if (x < seam[y] - 1) newEnergyMap[y][x] = energyMap[y][x];
                else if (x > seam[y] + 1) newEnergyMap[y][x] = energyMap[y][x - 1];
                else newEnergyMap[y][x] = splitFlag ? energyMap[y][seam[y]] + 1000 : energy(x, y);
          } else if (op == XSUB) {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++)
                if (x < seam[y] - 1) newEnergyMap[y][x] = energyMap[y][x];
                else if (x > seam[y]) newEnergyMap[y][x] = energyMap[y][x + 1];
                else newEnergyMap[y][x] = energy(x, y);

          } else if (op == YADD) {
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                if (y < seam[x] - 1) newEnergyMap[y][x] = energyMap[y][x];
                else if (y > seam[x] + 1) newEnergyMap[y][x] = energyMap[y - 1][x];
                else newEnergyMap[y][x] = splitFlag ? energyMap[seam[x]][x] + 1000 : energy(x, y);

          } else {
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                if (y < seam[x] - 1) newEnergyMap[y][x] = energyMap[y][x];
                else if (y > seam[x]) newEnergyMap[y][x] = energyMap[y + 1][x];
                else newEnergyMap[y][x] = energy(x, y);
          }
        });
    energyMap = newEnergyMap;
  }

  /*
  direct = true: get the average color of the left and the current pixel
  direct = false: get the average color of the top and the current pixel
  */
  private Color getAvgColor(boolean direct, int x, int y) {
    Color here = picture.get(x, y);
    if (direct) {
      Color left = (x == 0) ? picture.get(x, y) : picture.get(x - 1, y);
      return new Color(
          (left.getRed() + here.getRed()) / 2,
          (left.getGreen() + here.getGreen()) / 2,
          (left.getBlue() + here.getBlue()) / 2);
    } else {
      Color top = (y == 0) ? picture.get(x, y) : picture.get(x, y - 1);
      return new Color(
          (top.getRed() + here.getRed()) / 2,
          (top.getGreen() + here.getGreen()) / 2,
          (top.getBlue() + here.getBlue()) / 2);
    }
  }

  private Color[] findPixels(int op, int[] seam) {
    Color[] pixels = new Color[(op == XADD) ? height : width];
    Utils.parallel(
        (cpu, cpus) -> {
          if (op == XADD) {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++) if (x == seam[y]) pixels[y] = getAvgColor(true, x, y);
          } else
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                if (y == seam[x]) pixels[x] = getAvgColor(false, x, y);
        });
    return pixels;
  }

  // XADD or YADD
  private void strech(int op) {
    int[] seam;
    Color[] pixels;
    splitFlag = true;
    if (op == XADD) {
      calVcost();
      seam = findVseam();
      pixels = findPixels(op, seam);
      insertPixels(op, seam, pixels);
    } else {
      calHcost();
      seam = findHseam();
      pixels = findPixels(op, seam);
      insertPixels(op, seam, pixels);
    }
    undoSeamsStack.push(seam);
  }

  // XSUB or YSUB
  private void compress(int op) {
    int[] seam;
    Color[] pixels;
    if (splitFlag) {
      calEnergyMap();
      splitFlag = false;
    }
    if (op == XSUB) {
      calVcost();
      seam = findVseam();
      pixels = removePixels(op, seam);
    } else {
      calHcost();
      seam = findHseam();
      pixels = removePixels(op, seam);
    }
    undoSeamsStack.push(seam);
    undoPixelsStack.push(pixels);
  }

  @Override
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
  @Override
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
    splitFlag = false;
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
      Color[] pixels = removePixels(direct ? XSUB : YSUB, seam);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    }
    opStackTo.push(op);
  }

  @Override
  public void setMask(double[][] mask) {
    this.mask = new double[height][width];
    for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) this.mask[y][x] = mask[y][x];
    maskFlag = true;
  }

  @Override
  public void removeMask() {
    maskFlag = false;
  }
}
