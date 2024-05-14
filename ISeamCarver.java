import edu.princeton.cs.algs4.Picture;

interface ISeamCarver {
  // Returns the width of the current image.
  int width();

  // Returns the height of the current image.
  int height();

  Picture picture();

  void addMask(int[] mask);

  void removeMask();
}
