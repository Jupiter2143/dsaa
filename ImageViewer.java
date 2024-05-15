import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageViewer extends JFrame {
  private BufferedImage image;
  private JLabel imageLabel;
  private double zoomFactor = 1.0;

  public ImageViewer() {
    setTitle("Image Viewer");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // 菜单栏
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("操作");
    JMenuItem zoomInItem = new JMenuItem("放大");
    JMenuItem zoomOutItem = new JMenuItem("缩小");
    menu.add(zoomInItem);
    menu.add(zoomOutItem);
    menuBar.add(menu);
    setJMenuBar(menuBar);

    // 读取图片
    try {
      image = ImageIO.read(new File("example.jpg")); // 替换为图片的路径
    } catch (IOException e) {
      e.printStackTrace();
    }

    // 显示图片
    imageLabel = new JLabel(new ImageIcon(image));
    JScrollPane scrollPane = new JScrollPane(imageLabel);
    add(scrollPane, BorderLayout.CENTER);

    // 放大操作
    zoomInItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            zoomFactor *= 1.25;
            updateImage();
          }
        });

    // 缩小操作
    zoomOutItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            zoomFactor /= 1.25;
            updateImage();
          }
        });

    setVisible(true);
  }

  // 更新图片大小
  private void updateImage() {
    Image scaledImage =
        image.getScaledInstance(
            (int) (image.getWidth() * zoomFactor),
            (int) (image.getHeight() * zoomFactor),
            Image.SCALE_SMOOTH);
    imageLabel.setIcon(new ImageIcon(scaledImage));
  }

  public static void main(String[] args) {
    new ImageViewer();
  }
}
