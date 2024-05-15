// Utils contain a collection of useful methods that are used throughout the program

public class Utils {
  public static int deltaSquare(int a, int b) {
    int deltaRed = ((a >> 16) & 0xFF) - ((b >> 16) & 0xFF);
    int deltaGreen = ((a >> 8) & 0xFF) - ((b >> 8) & 0xFF);
    int deltaBlue = (a & 0xFF) - (b & 0xFF);
    return deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue;
  }

  public static int minIndex(double a, double b, double c) {
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

  public static int avgColor(int a, int b) {
    int a_r = (a >> 16) & 0xFF;
    int a_g = (a >> 8) & 0xFF;
    int a_b = a & 0xFF;
    int b_r = (b >> 16) & 0xFF;
    int b_g = (b >> 8) & 0xFF;
    int b_b = b & 0xFF;
    int r = (a_r + b_r) / 2;
    int g = (a_g + b_g) / 2;
    int c = (a_b + b_b) / 2;
    return (r << 16) | (g << 8) | c;
  }

  interface ParallelFunc {
    void process(int cpu, int cpus);
  }

  public static void parallel(ParallelFunc func) {
    int cpus = Runtime.getRuntime().availableProcessors();

    Thread[] threads = new Thread[cpus];
    for (int i = 0; i < cpus; i++) {
      int cpu = i;
      threads[cpu] = new Thread(() -> func.process(cpu, cpus));
    }

    for (Thread thread : threads) {
      thread.start();
    }

    try {
      for (Thread thread : threads) {
        thread.join();
      }
    } catch (InterruptedException ignored) {
    }
  }
}
