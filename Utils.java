// Utils contain a collection of useful methods that are used throughout the program
import java.awt.Color;

public class Utils {
  public static double deltaSquare(Color a, Color b) {
    return Math.pow(a.getRed() - b.getRed(), 2)
        + Math.pow(a.getGreen() - b.getGreen(), 2)
        + Math.pow(a.getBlue() - b.getBlue(), 2);
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
