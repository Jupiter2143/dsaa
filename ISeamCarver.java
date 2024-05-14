import edu.princeton.cs.algs4.Picture;

interface ISeamCarver {
  // Returns the width of the current image.
  int width();

  // Returns the height of the current image.
  int height();

  // get picture
  Picture picture();

  // add mask to protect some pixels or try to remove some pixels
  void setMask(double[][] mask);

  // remove mask
  void removeMask();

  // op: 0b00 for XADD, 0b01 for YADD, 0b11 for XSUB, 0b10 for YSUB
  public void operate(int op);

  // undo: true for undo, false for redo
  public void undo(boolean undo);
}
