import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
// import edu.princeton.cs.algs4.StdOut;
import java.util.PriorityQueue;

public class SeamCarver {
  private Picture picture; // current pictured
  private Picture originPicture; // original picture
  private double[] energyMap; // energy map, 2d array to store the energy of the pixel
  private double[] Vcost; // 2d cumulative energy matrix to store the cost of the vertical seam
  private double[] Hcost; // 2d cumulative energy matrix to store the cost of the horizontal seam
  private int[] traceMatrix; // 2d matrix to store the trace of the seam
  private int[] Vseams;
  private int[] Hseams;
  private final Stack<int[]> VseamsStack = new Stack<>();
  private final Stack<int[]> HseamsStack = new Stack<>();
  // private final Stack<int[]> seamHistory = new Stack<>();
  // private final Stack<Boolean> seamDirection =
  //     new Stack<>();
  // The operation code:
  private static final int XADD = 0b00;
  private static final int YADD = 0b01;
  private static final int XSUB = 0b11;
  private static final int YSUB = 0b10;

  private final Stack<Integer> undoStack = new Stack<>();
  private final Stack<Integer> redoStack = new Stack<>();

  // private final Stack<Boolean> operateHistory =
  //     new Stack<>(); // true for enlarege, false for reduce

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    this.picture = picture;
    // init energy map
    this.energyMap = new double[picture.width() * picture.height()];
    // TODO: calculate the energy map, calculate the Vseams and Hseams
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

  // true for horizontal, false for vertical
  public void strech(boolean direction) {}

  // true for horizontal, false for vertical
  public void undoCompress(boolean direction) {
    if (!direction){
            if (!VseamsStack.isEmpty()) {
                int[] seam = VseamsStack.pop();

                // Insert pixels corresponding to seam from originImage into corresponding positions of currentImage
                Picture newPicture = new Picture(picture.width() - 1, picture.height());
                for (int y = 0; y < picture.height(); y++) {
                    int newX = 0;
                    for (int x = 0; x < picture.width(); x++) {
                        if (seam[x] == 0) {
                            newPicture.set(newX++, y, picture.get(x, y));
                        } else {
                            newPicture.set(newX, y, originPicture.get(x, y)); // Insert pixels from originImage
                        }
                    }
                }
                picture = newPicture;
            }
        }else{
            if (!HseamsStack.isEmpty()) {
                int[] seam = HseamsStack.pop();

               // Insert pixels corresponding to seam from originImage into corresponding positions of currentImage
                Picture newPicture = new Picture(picture.width(), picture.height()-1);
                for (int y = 0; y < picture.height(); y++) {
                    int newY = 0;
                    for (int x = 0; x < picture.width(); x++) {
                        if (seam[y] == 0) {
                            newPicture.set(x, newY++, picture.get(x, y));
                        } else {
                            newPicture.set(x, newY, originPicture.get(x, y)); /// Insert pixels from originImage
                        }
                    }
                }
                picture = newPicture;
            }
        }
  }

  // true for horizontal, false for vertical
  public void compress(boolean direction) {
    
  }

  // true for horizontal, false for vertical
  public void undoStrech(boolean direction) {}

  public void operate(int op) {
    switch (op) {
      case XADD -> {
        if (picture.width() >= originPicture.width()) {
          strech(true);
        } else {
          undoCompress(true);
        }
      }
      case YADD -> {
        if (picture.height() >= originPicture.height()) {
          strech(false);
        } else {
          undoCompress(false);
        }
      }
      case XSUB -> {
        if (picture.width() <= originPicture.width()) {
          compress(true);
        } else {
          undoStrech(true);
        }
      }
      case YSUB -> {
        if (picture.height() >= originPicture.height()) {
          compress(false);
        } else {
          undoStrech(false);
        }
      }
    }
    undoStack.push(op);
    if (undoStack.size() > 50) {
      undoStack.remove(0);
    }
    redoStack.clear();
  }

  public void undo() {}

  public void redo() {}

  // public void operate(boolean direction, int WidthChange, int HeightChange) {
  //   if (WidthChange == 0 && HeightChange == 0) return;

  //   int originWidth = originPicture.width();
  //   int originHight = originPicture.height();
  //   int currentWidth = direction ? originWidth : originWidth + WidthChange;
  //   int currentHeight = direction ? originHigh : originHigh + HeightChange;
  //   Picture resizedPicture = new Picture(currentWidth, currentHeight);

  //   // If reducing
  //   if (WidthChange < 0) {
  //     for (int i = 0; i < Math.abs(WidthChange); i++) {
  //       if (direction) {
  //         removeHorizontalSeam(findHorizontalSeam(i));
  //         for (int y = 0; y < originHeight; y++) {
  //           int seam = horizontalSeams[y];
  //           for (int x = 0; x < seam; x++) {
  //             resizedPicture.set(x, y, picture.get(x, y));
  //           }
  //           for (int x = seam + 1; x < originWidth; x++) {
  //             resizedPicture.set(x - 1, y, picture.get(x, y));
  //           }
  //         }
  //         picture = resizedPicture;
  //         calEnergyMap();
  //       }
  //     }
  //   } else if (HeightChange < 0) {
  //     for (int i = 0; i < Math.abs(HeightChange); i++) {
  //       if (!direction) {
  //         removeVerticalSeam(findVerticalSeam(i));
  //         for (int x = 0; x < originWidth; x++) {
  //           int seam = verticalSeams[x];
  //           for (int y = 0; y < seam; y++) {
  //             resizedPicture.set(x, y, picture.get(x, y));
  //           }
  //           for (int y = seam + 1; y < originHeight; y++) {
  //             resizedPicture.set(x, y - 1, picture.get(x, y));
  //           }
  //         }
  //         picture = resizedPicture;
  //         calEnergyMap();
  //       }
  //     }
  //   } else if (WidthChange > 0) {
  //     for (int i = 0; i < Math.abs(WidthChange); i++) {
  //       int[] seamToAdd = findHorizontalSeam(i);
  //     }

  //   } else if (HeightChange > 0) {
  //     for (int i = 0; i < Math.abs(HeightChange); i++) {
  //       int[] seamToAdd = findVerticalSeam(i);
  //     }
  //   }
  // }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    Picture newPicture = new Picture(this.width(), this.height() - 1);

    int prevSeam = seam[0];
    for (int x = 0; x < this.width(); x++) {
      prevSeam = seam[x];
      for (int y = 0; y < this.height(); y++) {
        if (seam[x] == y) continue;

        Color color = this.picture.get(x, y);
        newPicture.set(x, seam[x] > y ? y : y - 1, color);
      }
    }

    this.picture = newPicture;
    calEnergyMap();
  }

  // // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    Picture newPicture = new Picture(this.width(), this.height() - 1);

    int prevSeam = seam[0];
    for (int y = 0; y < this.width(); y++) {
      prevSeam = seam[y];
      for (int x = 0; x < this.width(); x++) {
        if (seam[y] == x) continue;

        Color color = this.picture.get(x, y);
        newPicture.set(seam[y] > x ? x : x - 1, y, color);
      }
    }

    this.picture = newPicture;
    calEnergyMap();
  }

  // unit testing (optional)
  public static void main(String[] args) {
    // StdOut.println("Hello World");
    // Picture picture = new Picture("example.jpg");
    // picture.show();
  }
}
