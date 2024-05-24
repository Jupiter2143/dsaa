import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
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
  private volatile boolean runningUndo = false;
  private volatile boolean runningRedo = false;
  private ChangeListener chgUpdate;
  private ArrayList<Point> highPriorityPoints = new ArrayList<>();
  private ArrayList<Point> lowPriorityPoints = new ArrayList<>();
  private boolean selectionToolActive = false;

  public GUI() {
    initAllListener();
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
            Graphics2D g2d = (Graphics2D) g.create();

            if (!highPriorityPoints.isEmpty()) {
              drawPolygon(g2d, highPriorityPoints, Color.RED);
            }

            if (!lowPriorityPoints.isEmpty()) {
              drawPolygon(g2d, lowPriorityPoints, Color.GREEN);
            }

            g2d.dispose();
          }

          private void drawPolygon(Graphics2D g2d, ArrayList<Point> points, Color color) {
            int[] xPoints = new int[points.size()];
            int[] yPoints = new int[points.size()];

            for (int i = 0; i < points.size(); i++) {
              Point point = points.get(i);
              xPoints[i] = point.x;
              yPoints[i] = point.y;
            }

            g2d.setColor(color);
            g2d.drawPolygon(xPoints, yPoints, points.size());
          }
        };

           int width = imageIcon.getIconWidth();
           int height = imageIcon.getIconHeight();
           label.setPreferredSize(new Dimension(width, height));
    
           JScrollPane scrollPane = new JScrollPane(label);
           panel.add(scrollPane, BorderLayout.CENTER);
    
           highlight = new boolean[height][width];
    // 创建一个JScrollPane并将label添加进去
    label.setHorizontalAlignment(JLabel.LEFT); // 设置水平对齐方式为左对齐
    label.setVerticalAlignment(JLabel.TOP); // 设置垂直对齐方式为顶部对齐

    // 将JScrollPane添加到panel中
    panel.add(scrollPane, BorderLayout.CENTER);

    label.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            if (selectionToolActive) {
              if (SwingUtilities.isLeftMouseButton(e)) {
                highPriorityPoints.clear();
                highPriorityPoints.add(e.getPoint());
              } else if (SwingUtilities.isRightMouseButton(e)) {
                lowPriorityPoints.clear();
                lowPriorityPoints.add(e.getPoint());
              }
              label.repaint();
            }
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            if (selectionToolActive) {
              if (SwingUtilities.isLeftMouseButton(e)) {
                highPriorityPoints.add(e.getPoint());
              } else if (SwingUtilities.isRightMouseButton(e)) {
                lowPriorityPoints.add(e.getPoint());
              }
              label.repaint();
              seamCarver.setMask(calculateEnergyWithHighlight());
            }
          }
        });

    label.addMouseMotionListener(
        new MouseMotionAdapter() {
          @Override
          public void mouseDragged(MouseEvent e) {
            if (selectionToolActive) {
              if (SwingUtilities.isLeftMouseButton(e)) {
                highPriorityPoints.add(e.getPoint());
              } else if (SwingUtilities.isRightMouseButton(e)) {
                lowPriorityPoints.add(e.getPoint());
              }
              label.repaint();
            }
          }
        });
  }

  private void updateHighlightForPoints(ArrayList<Point> points) {
    if (points.size() < 3) return;

    Polygon polygon = new Polygon();
    for (Point point : points) {
      polygon.addPoint(point.x, point.y);
    }

    for (int y = 0; y < imageIcon.getIconHeight(); y++) {
      for (int x = 0; x < imageIcon.getIconWidth(); x++) {
        if (polygon.contains(x, y)) {
          highlight[y][x] = true;
        }
      }
    }
  }

  private float[][] calculateEnergyWithHighlight() {
    int width = imageIcon.getIconWidth();
    int height = imageIcon.getIconHeight();
    float[][] mask = new float[height][width];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        mask[y][x] = 0;
      }
    }

    applyPriorityToMask(highPriorityPoints, mask, 1e5f);
    applyPriorityToMask(lowPriorityPoints, mask, -1e4f);

    return mask;
  }

  private void applyPriorityToMask(ArrayList<Point> points, float[][] mask, float value) {
    if (points.size() < 3) return;

    Polygon polygon = new Polygon();
    for (Point point : points) {
      polygon.addPoint(point.x, point.y);
    }

    for (int y = 0; y < mask.length; y++) {
      for (int x = 0; x < mask[0].length; x++) {
        if (polygon.contains(x, y)) {
          mask[y][x] = value;
        }
      }
    }
  }

  private void startselectiontool() {
    selectionToolActive = true;
  }

  private void clearSelection() {
    highPriorityPoints.clear();
    lowPriorityPoints.clear();
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
    wSpinner.addChangeListener(chgUpdate);
    hSpinner = new JSpinner(new SpinnerNumberModel(originH, originH / 4, originH * 4, 1));
    hSpinner.addChangeListener(chgUpdate);
    h1.add(new JLabel("Width: "));
    h1.add(wSpinner);
    h1.add(new JLabel("Height: "));
    h1.add(hSpinner);
    rightPanel.add(h1);
  }

  private void initButtons(JPanel rightPanel) {
    // 添加 "Undo" 按钮
    JButton undoButton = new JButton("Undo");
    undoButton.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            runningUndo = true;
            new Thread(
                    new Runnable() {
                      @Override
                      public void run() {
                        while (runningUndo) {
                          seamCarver.undo(true);
                          imageIcon.setImage(seamCarver.picture());
                          label.repaint();
                          wSpinner.setValue(seamCarver.width());
                          hSpinner.setValue(seamCarver.height());
                          try {
                            Thread.sleep(10); // 10ms
                          } catch (InterruptedException e) {
                            e.printStackTrace();
                          }
                        }
                      }
                    })
                .start();
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            runningUndo = false;
          }
        });
    rightPanel.add(undoButton);

    // 添加 "Redo" 按钮
    JButton redoButton = new JButton("Redo");
    redoButton.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            runningRedo = true;
            new Thread(
                    new Runnable() {
                      @Override
                      public void run() {
                        while (runningRedo) {
                          seamCarver.undo(false);
                          imageIcon.setImage(seamCarver.picture());
                          label.repaint();
                          wSpinner.setValue(seamCarver.width());
                          hSpinner.setValue(seamCarver.height());
                          try {
                            Thread.sleep(10); // 10ms
                          } catch (InterruptedException e) {
                            e.printStackTrace();
                          }
                        }
                      }
                    })
                .start();
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            runningRedo = false;
          }
        });
    rightPanel.add(redoButton);

    JButton selectButton = new JButton("套索工具");
    selectButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            startSelectionTool();
            startselectiontool();
          }
        });
    rightPanel.add(selectButton);

    JButton restoreButton = new JButton("还原");
    restoreButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            BufferedImage originalPicture = seamCarver.originalPicture();
            imageIcon.setImage(originalPicture);
            label.repaint();
            seamCarver.restore();
            ChangeListener[] w = wSpinner.getChangeListeners();
            ChangeListener[] h = hSpinner.getChangeListeners();
            for (ChangeListener i : w) wSpinner.removeChangeListener(i);
            for (ChangeListener i : h) hSpinner.removeChangeListener(i);
            wSpinner.setValue(originalPicture.getWidth());
            hSpinner.setValue(originalPicture.getHeight());
            JFormattedTextField wTextField =
                ((JSpinner.DefaultEditor) wSpinner.getEditor()).getTextField();
            JFormattedTextField hTextField =
                ((JSpinner.DefaultEditor) hSpinner.getEditor()).getTextField();
            wTextField.setValue(originalPicture.getWidth());
            hTextField.setValue(originalPicture.getHeight());
            for (ChangeListener i : w) wSpinner.addChangeListener(i);
            for (ChangeListener i : h) hSpinner.addChangeListener(i);
            // isSelectionToolActive = false;
          }
        });
    rightPanel.add(restoreButton);

    JButton setMaskButton = new JButton("设置Mask");
    setMaskButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            clearSelection();
          }
        });
    rightPanel.add(setMaskButton);

    JButton removeMaskButton = new JButton("移除Mask");
    removeMaskButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            seamCarver.removeMask();
          }
        });
    rightPanel.add(removeMaskButton);
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
            // JFileChooser fileChooser = new JFileChooser();
            // 打开到当前目录
            JFileChooser fileChooser = new JFileChooser(".");
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
              seamCarver = new SeamCarver(fileChooser.getSelectedFile().getAbsolutePath());
              imageIcon.setImage(seamCarver.picture());
              label.repaint();
              wSpinner.removeChangeListener(chgUpdate);
              hSpinner.removeChangeListener(chgUpdate);
              wSpinner.setModel(
                  new SpinnerNumberModel(
                      seamCarver.width(), seamCarver.width() / 4, seamCarver.width() * 4, 1));
              hSpinner.setModel(
                  new SpinnerNumberModel(
                      seamCarver.height(), seamCarver.height() / 4, seamCarver.height() * 4, 1));
              wSpinner.setValue(seamCarver.width());
              hSpinner.setValue(seamCarver.height());
              wSpinner.addChangeListener(chgUpdate);
              hSpinner.addChangeListener(chgUpdate);
              label.setPreferredSize(new Dimension(seamCarver.width(), seamCarver.height()));
              label.revalidate();
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
  }

  private void startSelectionTool() {
    // 点击则弹出一个对话框，用于提示用户进行套索选区
    JOptionPane.showMessageDialog(
        frame,
        "请在图像上用鼠标拖动进行选区。画出保护区域请用鼠标左键（显示为红色），画出消除区域请用鼠标右键（显示为绿色）。",
        "套索工具",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void initAllListener() {
    chgUpdate =
        new ChangeListener() {
          @Override
          public void stateChanged(javax.swing.event.ChangeEvent e) {
            int newWidth = (int) wSpinner.getValue();
            int newHeight = (int) hSpinner.getValue();
            if (newWidth < seamCarver.width()) {
              for (int i = 0; i < seamCarver.width() - newWidth; i++) seamCarver.operate(XSUB);
            } else if (newWidth > seamCarver.width()) {
              for (int i = 0; i < newWidth - seamCarver.width(); i++) seamCarver.operate(XADD);
            }
            if (newHeight < seamCarver.height()) {
              for (int i = 0; i < seamCarver.height() - newHeight; i++) seamCarver.operate(YSUB);
            } else if (newHeight > seamCarver.height()) {
              for (int i = 0; i < newHeight - seamCarver.height(); i++) seamCarver.operate(YADD);
            }
            imageIcon.setImage(seamCarver.picture());
            label.repaint();
            label.setPreferredSize(new Dimension(newWidth, newHeight));
            label.revalidate();
          }
        };
  }
}
