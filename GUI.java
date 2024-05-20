import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI {
  private static final int XADD = 0b00;
  private static final int YADD = 0b01;
  private static final int XSUB = 0b11;
  private static final int YSUB = 0b10;
  private JFrame frame = new JFrame("Seam Carver");
  private SeamCarver seamCarver = new SeamCarver("example2.jpg");
  private JPanel panel = new JPanel();
  private JLabel label = new JLabel();
  private JSpinner wSpinner;
  private JSpinner hSpinner;
  private ImageIcon imageIcon;
  private ArrayList<Point> points = new ArrayList<>(); // 存储套索选区的所有点
  private boolean[][] highlight; // 套索内矩阵

  public GUI() {
    initMainWindow();
    initMainPanel();
    frame.setVisible(true);
  }

  private void initMainPanel() {
    panel.setLayout(new BorderLayout());
    initScrollPane();
    initRightPanel();
    frame.add(panel, BorderLayout.CENTER);
  }

  private void initMainWindow() {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) (screenSize.width * 0.70);
    int height = (int) (screenSize.height * 0.70);
    frame.setSize(width, height);
    frame.setLayout(new BorderLayout());
    frame.setLocationRelativeTo(null);
    initMenuBar();
    initStatusBar();
  }

  // 显示图、鼠标监听、套索实施
  private void initScrollPane() {
    imageIcon = new ImageIcon(seamCarver.picture());

    label =
        new JLabel(imageIcon) {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!points.isEmpty()) {
              int[] xPoints = new int[points.size()];
              int[] yPoints = new int[points.size()];

              for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                xPoints[i] = point.x;
                yPoints[i] = point.y;
              }

              Graphics2D g2d = (Graphics2D) g.create();
              // fillPolygon(g2d, xPoints, yPoints, points.size()); // 填充多边形内部
              g2d.setColor(Color.RED);
              g2d.drawPolygon(xPoints, yPoints, points.size()); // 多边形
              g2d.dispose();
            }
          }
        };

    int width = imageIcon.getIconWidth();
    int height = imageIcon.getIconHeight();
    label.setPreferredSize(new Dimension(width, height));

    JScrollPane scrollPane = new JScrollPane(label);
    panel.add(scrollPane, BorderLayout.CENTER);

    highlight = new boolean[width][height];

    label.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            points.clear();
            points.add(e.getPoint());
            label.repaint(); // 重新绘制以显示套索选区
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            points.add(e.getPoint()); // 最后一个点
            label.repaint();
            // updateHighlightMatrix();
          }
        });

    label.addMouseMotionListener(
        new MouseMotionAdapter() {
          @Override
          public void mouseDragged(MouseEvent e) {
            points.add(e.getPoint());
            label.repaint();
          }
        });
  }

  // 多边形不太好写，想不出来也没查出来，放一个外接矩形的，留条后路
  //   private void fillPolygon(Graphics g, int[] xPoints, int[] yPoints, int nPoints) {
  //     Polygon polygon = new Polygon(xPoints, yPoints, nPoints);

  //     // 边界矩形
  //     Rectangle bounds = polygon.getBounds();

  //     // 获取多边形内部的像素点颜色
  //     Image image = imageIcon.getImage();
  //     BufferedImage bufferedImage = toBufferedImage(image);

  //     for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
  //         for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
  //             if (polygon.contains(x, y)) {
  //                 g.setColor(new Color(bufferedImage.getRGB(x, y))); // 获取像素点颜色
  //                 g.fillRect(x, y, 1, 1); // 填充像素点
  //                 highlight[x][y] = true; // 标记高亮像素点
  //             }
  //         }
  //     }
  // }

  // 更新高亮矩阵
  // private void updateHighlightMatrix() {
  //     for (int i = 0; i < highlight.length; i++) {
  //         for (int j = 0; j < highlight[0].length; j++) {
  //             highlight[i][j] = false;
  //         }
  //     }

  //     // 获取多边形内的像素点
  //     for (int x = 0; x < imageIcon.getIconWidth(); x++) {
  //         for (int y = 0; y < imageIcon.getIconHeight(); y++) {
  //             if (label.contains(x, y)) {
  //                 if (label.contains(x + 1, y + 1)) {
  //                     if (highlight[x][y] && highlight[x + 1][y] && highlight[x][y + 1] &&
  // highlight[x + 1][y + 1]) {
  //                         highlight[x][y] = true;
  //                     }
  //                 }
  //             }
  //         }
  //     }
  // }

  // 转换Image为BufferedImage
  private BufferedImage toBufferedImage(Image image) {
    if (image instanceof BufferedImage) {
      return (BufferedImage) image;
    }

    BufferedImage bufferedImage =
        new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.drawImage(image, 0, 0, null);
    g2d.dispose();

    return bufferedImage;
  }

  private void initRightPanel() {
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    inith1(rightPanel);
    initButtons(rightPanel);
    panel.add(rightPanel, BorderLayout.EAST);
  }

  private void inith1(JPanel rightPanel) {
    JPanel h1 = new JPanel();
    h1.setLayout(new BoxLayout(h1, BoxLayout.X_AXIS));
    h1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    int originW = seamCarver.width();
    int originH = seamCarver.height();
    wSpinner = new JSpinner(new SpinnerNumberModel(originW, originW / 4, originW * 4, 1));
    wSpinner.addChangeListener(
        new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            int op = (seamCarver.width() < (int) wSpinner.getValue()) ? XADD : XSUB;
            seamCarver.operate(op);
            imageIcon.setImage(seamCarver.picture());
            label.repaint();
          }
        });
    hSpinner = new JSpinner(new SpinnerNumberModel(originH, originH / 4, originH * 4, 1));
    hSpinner.addChangeListener(
        new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            int op = (seamCarver.height() < (int) hSpinner.getValue()) ? YADD : YSUB;
            seamCarver.operate(op);
            imageIcon.setImage(seamCarver.picture());
            label.repaint();
          }
        });
    h1.add(new JLabel("Width: "));
    h1.add(wSpinner);
    h1.add(new JLabel("Height: "));
    h1.add(hSpinner);
    rightPanel.add(h1);
  }

  private void initButtons(JPanel rightPanel) {
    JButton resizeButton = new JButton("Resize");
    resizeButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            int newWidth = (int) wSpinner.getValue();
            int newHeight = (int) hSpinner.getValue();
            int picWidth = seamCarver.width();
            int picHeight = seamCarver.height();
            for (int i = 0; i < picWidth - newWidth; i++) seamCarver.operate(XSUB); // 0b11 for XSUB
            for (int i = 0; i < newWidth - picWidth; i++) seamCarver.operate(XADD); // 0b00 for XADD
            for (int i = 0; i < picHeight - newHeight; i++)
              seamCarver.operate(YSUB); // 0b10 for YSUB
            for (int i = 0; i < newHeight - picHeight; i++)
              seamCarver.operate(YADD); // 0b01 for YADD
            imageIcon.setImage(seamCarver.picture());
            label.repaint();
          }
        });
    rightPanel.add(resizeButton);

    // 添加 "Undo" 按钮
    JButton undoButton = new JButton("Undo");
    undoButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // 当点击 "Undo" 按钮时调用 SeamCarver 的 undo 方法进行撤销操作
            seamCarver.undo(true);
            // 刷新图像显示
            imageIcon.setImage(seamCarver.picture());
            label.repaint();
          }
        });
    rightPanel.add(undoButton);

    // 添加 "Redo" 按钮
    JButton redoButton = new JButton("Redo");
    redoButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // 重做操作
            seamCarver.undo(false);
            imageIcon.setImage(seamCarver.picture());
            label.repaint();
          }
        });
    rightPanel.add(redoButton);
  }

  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("文件");
    JMenuItem menuItemOpen = new JMenuItem("打开");
    JMenuItem menuItemSave = new JMenuItem("保存");
    JMenuItem menuItemExit = new JMenuItem("退出");
    menu.add(menuItemOpen);
    menu.add(menuItemSave);
    menu.add(menuItemExit);

    // 为 "打开" 菜单项添加动作监听器
    menuItemOpen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // 实现打开文件的逻辑
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
              seamCarver = new SeamCarver(fileChooser.getSelectedFile().getAbsolutePath());
              imageIcon.setImage(seamCarver.picture());
              label.repaint();
            }
          }
        });

    // 为 "保存" 菜单项添加动作监听器
    menuItemSave.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // 实现保存文件的逻辑
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
              seamCarver.save(fileChooser.getSelectedFile().getAbsolutePath());
            }
          }
        });

    // 为 "退出" 菜单项添加动作监听器
    menuItemExit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // 实现退出程序的逻辑
            System.exit(0);
          }
        });

    menuBar.add(menu);
    frame.setJMenuBar(menuBar);
  }

  private void initStatusBar() {
    JLabel statusBar = new JLabel("状态栏");
    statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    frame.add(statusBar, BorderLayout.SOUTH);

    JButton selectButton = new JButton("套索工具");
    statusBar.add(selectButton);
    selectButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            startSelectionTool();
          }
        });
  }

  private void startSelectionTool() {
    // 点击则弹出一个对话框，用于提示用户进行套索选区
    JOptionPane.showMessageDialog(
        frame, "请在图像上用鼠标拖动进行选区。", "套索工具", JOptionPane.INFORMATION_MESSAGE);
  }
}
