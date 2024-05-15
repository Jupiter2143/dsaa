// import edu.princeton.cs.algs4.Picture;
import java.awt.image.BufferedImage; // no longer use edu.princeton.cs.algs4.Picture
import java.io.File;
import java.util.Stack;
import javax.imageio.ImageIO;

public class SeamCarver implements ISeamCarver {
  private BufferedImage picture;
  private BufferedImage originPicture; // original picture
  private float[][] energyMap; // energy map, 2d array to store the energy of the pixel
  private float[][] mask;
  private boolean maskFlag = false;
  private boolean splitFlag = false;
  private float[][] maskedEnergyMap;
  private float[][] Vcost; // 2d cumulative energy matrix to store the cost of the vertical seam
  private float[][] Hcost; // 2d cumulative energy matrix to store the cost of the horizontal seam
  private int[][] traceMatrix; // 2d matrix to store the trace of the seam
  private int width;
  private int height;
  // operand stack for undo and redo
  private final Stack<Integer> undoStack = new Stack<>();
  private final Stack<Integer> redoStack = new Stack<>();
  private final Stack<int[]> undoSeamsStack = new Stack<>();
  private final Stack<int[]> redoSeamsStack = new Stack<>();
  private final Stack<int[]> undoPixelsStack = new Stack<>();
  private final Stack<int[]> redoPixelsStack = new Stack<>();
  // The oprands for the operation
  private static final int XADD = 0b00;
  private static final int YADD = 0b01;
  private static final int XSUB = 0b11;
  private static final int YSUB = 0b10;

  public SeamCarver(String filename) {
    try {
      BufferedImage picture = ImageIO.read(new File(filename));
      // SeamCarver(picture);
      this.originPicture = picture;
      this.picture = picture;
      this.width = picture.getWidth();
      this.height = picture.getHeight();
      calEnergyMap();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // current picture
  @Override
  public BufferedImage picture() {
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
  private float energy(int x, int y) {
    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return 1000;
    int top = picture.getRGB(x, y + 1);
    int bottom = picture.getRGB(x, y - 1);
    int left = picture.getRGB(x - 1, y);
    int right = picture.getRGB(x + 1, y);
    return (float) Math.sqrt(Utils.deltaSquare(top, bottom) + Utils.deltaSquare(left, right));
  }

  private void calEnergyMap() {
    energyMap = new float[height][width];
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
    Vcost = new float[height][width];
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
    Hcost = new float[height][width];
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
    float min = Vcost[height - 1][0];
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
    float min = Hcost[0][width - 1];
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
  private void insertPixels(int op, int[] seam, int[] pixels) {
    if (op == XADD) {
      // Picture newPicture = new Picture(width + 1, height);
      BufferedImage newPicture = new BufferedImage(width + 1, height, BufferedImage.TYPE_INT_RGB);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++)
                // if (x < seam[y]) newPicture.set(x, y, picture.get(x, y));
                // else if (x == seam[y]) {
                //   newPicture.set(x, y, pixels[y]);
                //   newPicture.set(x + 1, y, picture.get(x, y));
                // } else newPicture.set(x + 1, y, picture.get(x, y));
                if (x < seam[y]) newPicture.setRGB(x, y, picture.getRGB(x, y));
                else if (x == seam[y]) {
                  newPicture.setRGB(x, y, pixels[y]);
                  newPicture.setRGB(x + 1, y, picture.getRGB(x, y));
                } else newPicture.setRGB(x + 1, y, picture.getRGB(x, y));
          });
      picture = newPicture;
      width++;
    } else {
      // Picture newPicture = new Picture(width, height + 1);
      BufferedImage newPicture = new BufferedImage(width, height + 1, BufferedImage.TYPE_INT_RGB);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                // if (y < seam[x]) newPicture.set(x, y, picture.get(x, y));
                // else if (y == seam[x]) {
                //   newPicture.set(x, y, pixels[x]);
                //   newPicture.set(x, y + 1, picture.get(x, y));
                // } else newPicture.set(x, y + 1, picture.get(x, y));
                if (y < seam[x]) newPicture.setRGB(x, y, picture.getRGB(x, y));
                else if (y == seam[x]) {
                  newPicture.setRGB(x, y, pixels[x]);
                  newPicture.setRGB(x, y + 1, picture.getRGB(x, y));
                } else newPicture.setRGB(x, y + 1, picture.getRGB(x, y));
          });
      picture = newPicture;
      height++;
    }
    updateEnergyMap(op, seam);
  }

  // XSUB or YSUB
  private int[] removePixels(int op, int[] seam) {
    // int[] pixels = new Color[(op == XSUB) ? height : width];
    int[] pixels = new int[(op == XSUB) ? height : width];
    if (op == XSUB) {
      // Picture newPicture = new Picture(width - 1, height);
      BufferedImage newPicture = new BufferedImage(width - 1, height, BufferedImage.TYPE_INT_RGB);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int y = cpu; y < height; y += cpus)
              for (int x = 0; x < width; x++)
                // if (x < seam[y]) newPicture.set(x, y, picture.get(x, y));
                // else if (x > seam[y]) newPicture.set(x - 1, y, picture.get(x, y));
                // else pixels[y] = picture.get(x, y);
                if (x < seam[y]) newPicture.setRGB(x, y, picture.getRGB(x, y));
                else if (x > seam[y]) newPicture.setRGB(x - 1, y, picture.getRGB(x, y));
                else pixels[y] = picture.getRGB(x, y);
          });
      picture = newPicture;
      width--;
    } else {
      // Picture newPicture = new Picture(width, height - 1);
      BufferedImage newPicture = new BufferedImage(width, height - 1, BufferedImage.TYPE_INT_RGB);
      Utils.parallel(
          (cpu, cpus) -> {
            for (int x = cpu; x < width; x += cpus)
              for (int y = 0; y < height; y++)
                // if (y < seam[x]) newPicture.set(x, y, picture.get(x, y));
                // else if (y > seam[x]) newPicture.set(x, y - 1, picture.get(x, y));
                // else pixels[x] = picture.get(x, y);
                if (y < seam[x]) newPicture.setRGB(x, y, picture.getRGB(x, y));
                else if (y > seam[x]) newPicture.setRGB(x, y - 1, picture.getRGB(x, y));
                else pixels[x] = picture.getRGB(x, y);
          });
      picture = newPicture;
      height--;
    }
    updateEnergyMap(op, seam);
    return pixels;
  }

  private void updateEnergyMap(int op, int[] seam) {
    float[][] newEnergyMap = new float[height][width];
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
  private int getAvgColor(boolean direct, int x, int y) {
    // Color here = picture.get(x, y);
    // if (direct) {
    //   Color left = (x == 0) ? picture.get(x, y) : picture.get(x - 1, y);
    //   return new Color(
    //       (left.getRed() + here.getRed()) / 2,
    //       (left.getGreen() + here.getGreen()) / 2,
    //       (left.getBlue() + here.getBlue()) / 2);
    // } else {
    //   Color top = (y == 0) ? picture.get(x, y) : picture.get(x, y - 1);
    //   return new Color(
    //       (top.getRed() + here.getRed()) / 2,
    //       (top.getGreen() + here.getGreen()) / 2,
    //       (top.getBlue() + here.getBlue()) / 2);
    // }
    int here = picture.getRGB(x, y);
    if (direct) {
      int left = (x == 0) ? picture.getRGB(x, y) : picture.getRGB(x - 1, y);
      return Utils.avgColor(left, here);
    } else {
      int top = (y == 0) ? picture.getRGB(x, y) : picture.getRGB(x, y - 1);
      return Utils.avgColor(top, here);
    }
  }

  private int[] findPixels(int op, int[] seam) {
    // Color[] pixels = new Color[(op == XADD) ? height : width];
    int[] pixels = new int[(op == XADD) ? height : width];
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
    // Color[] pixels;
    int[] pixels;
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
    // Color[] pixels;
    int[] pixels;
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
    // Stack<Color[]> pixelsStackFrom = undo ? undoPixelsStack : redoPixelsStack;
    Stack<int[]> pixelsStackFrom = undo ? undoPixelsStack : redoPixelsStack;
    Stack<int[]> seamsStacksTo = undo ? redoSeamsStack : undoSeamsStack;
    // Stack<Color[]> pixelsStackTo = undo ? redoPixelsStack : undoPixelsStack;
    Stack<int[]> pixelsStackTo = undo ? redoPixelsStack : undoPixelsStack;
    int op = opStackFrom.pop();
    int invOp = ~op & 0b11;
    boolean add = (op == XADD || op == YADD);
    boolean direct = (op == XADD || op == XSUB);
    splitFlag = false;
    int[] seam = seamsStacksFrom.pop();
    if (undo ^ add) {
      // undo compress or redo strech: undo+XSUP or redo+XADD
      // insert the seam
      int[] pixels = pixelsStackFrom.pop();
      insertPixels(direct ? XADD : YADD, seam, pixels);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    } else {
      // undo strech or redo compress: undo+XADD or redo+XSUB
      // remove the seam
      int[] pixels = removePixels(direct ? XSUB : YSUB, seam);
      seamsStacksTo.push(seam);
      pixelsStackTo.push(pixels);
    }
    opStackTo.push(op);
  }

  @Override
  public void setMask(float[][] mask) {
    this.mask = new float[height][width];
    for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) this.mask[y][x] = mask[y][x];
    maskFlag = true;
  }

  @Override
  public void removeMask() {
    maskFlag = false;
  }

  @Override
  public void save(String filename) {
    try {
      ImageIO.write(picture, "jpg", new File(filename));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
