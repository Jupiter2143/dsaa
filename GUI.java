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
  private SeamCarver seamCarver;
  private JLabel statusBar;
  private JPanel panel = new JPanel();
  private JLabel label = new JLabel();
  private JSpinner wSpinner;
  private JSpinner hSpinner;
  private ImageIcon imageIcon;
  private ArrayList<Point> points = new ArrayList<>(); // 存储套索选区的所有点
  private volatile boolean runningUndo = false;
  private volatile boolean runningRedo = false;
  private ChangeListener chgUpdate;
  private ActionListener actSave;
  private ArrayList<Point> highPriorityPoints = new ArrayList<>();
  private ArrayList<Point> lowPriorityPoints = new ArrayList<>();
  private boolean selectionToolActive = false;
  private JButton undoButton = new JButton("撤销");
  private JButton redoButton = new JButton("重做");
  private JButton selectButton = new JButton("套索工具");
  private JCheckBox selectionCheckBox = new JCheckBox();
  private JButton setMaskButton = new JButton("确定");
  private JButton removeMaskButton = new JButton("取消区域");
  private JButton saveButton = new JButton("保存");
  private JButton restoreButton = new JButton("还原");

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

  private void initScrollPane() {
    initLabel();
    JScrollPane scrollPane = new JScrollPane(label);
    panel.add(scrollPane, BorderLayout.CENTER);
  }

  private float[][] calculateMask() {
    int width = imageIcon.getIconWidth();
    int height = imageIcon.getIconHeight();
    float[][] mask = new float[height][width];

    applyPriorityToMask(highPriorityPoints, mask, 1e5f);
    applyPriorityToMask(lowPriorityPoints, mask, -1e4f);

    return mask;
  }

  private void applyPriorityToMask(ArrayList<Point> points, float[][] mask, float value) {
    if (points.size() < 3) return;
    Polygon polygon = new Polygon();
    for (Point point : points) polygon.addPoint(point.x, point.y);

    for (int y = 0; y < mask.length; y++)
      for (int x = 0; x < mask[0].length; x++) if (polygon.contains(x, y)) mask[y][x] = value;
  }

  private void clearSelection() {
    highPriorityPoints.clear();
    lowPriorityPoints.clear();
  }

  private void initRightPanel() {
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    initButtons(rightPanel);
    inith1(rightPanel);
    inith2(rightPanel);
    inith3(rightPanel);

    panel.add(rightPanel, BorderLayout.EAST);
  }

  private void inith1(JPanel rightPanel) {
    JPanel h1 = new JPanel();
    h1.setLayout(new GridLayout(2, 2));
    h1.setBorder(BorderFactory.createTitledBorder("设置图片大小"));
    h1.setMaximumSize(new Dimension(300, 80));
    int originW = seamCarver.width();
    int originH = seamCarver.height();
    wSpinner = new JSpinner(new SpinnerNumberModel(originW, originW / 4, originW * 4, 1));
    hSpinner = new JSpinner(new SpinnerNumberModel(originH, originH / 4, originH * 4, 1));
    wSpinner.addChangeListener(chgUpdate);
    hSpinner.addChangeListener(chgUpdate);
    JPanel controlWidth = new JPanel();
    controlWidth.add(new JLabel("宽度: "));
    controlWidth.add(wSpinner);
    JPanel controlHeight = new JPanel();
    controlHeight.add(new JLabel("高度: "));
    controlHeight.add(hSpinner);
    h1.add(controlWidth);
    h1.add(controlHeight);
    h1.add(undoButton);
    h1.add(redoButton);
    rightPanel.add(h1);
  }

  private void inith2(JPanel rightPanel) {
    JPanel h2 = new JPanel();
    h2.setLayout(new GridLayout(2, 2));
    h2.setBorder(BorderFactory.createTitledBorder("选中保护与删除"));
    h2.setMaximumSize(new Dimension(300, 80));
    h2.add(selectButton);
    h2.add(selectionCheckBox);
    selectionCheckBox.setText("开启");
    h2.add(setMaskButton);
    h2.add(removeMaskButton);
    rightPanel.add(h2);
  }

  private void inith3(JPanel rightPanel) {
    // 保存和还原
    JPanel h3 = new JPanel();
    h3.setLayout(new GridLayout(1, 2));
    h3.setBorder(BorderFactory.createTitledBorder("操作"));
    h3.setMaximumSize(new Dimension(300, 60));
    h3.add(saveButton);
    h3.add(restoreButton);
    rightPanel.add(h3);
  }

  private void initButtons(JPanel rightPanel) {

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

    selectionCheckBox.addItemListener(
        new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            selectionToolActive = e.getStateChange() == ItemEvent.SELECTED;
          }
        });
    selectButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(
                frame,
                "<html>请在图像上用鼠标拖动进行选区<br>画出保护区域请用鼠标左键（显示为红色）<br>画出消除区域请用鼠标右键（显示为绿色）</html>",
                "套索工具",
                JOptionPane.INFORMATION_MESSAGE);
            selectionToolActive = true;
            selectionCheckBox.setSelected(true);
          }
        });

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
          }
        });

    setMaskButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (highPriorityPoints.isEmpty() && lowPriorityPoints.isEmpty()) {
              JOptionPane.showMessageDialog(
                  frame, "请先用鼠标在图像上画出选区", "错误", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JOptionPane.showMessageDialog(
                frame, "选中区域成功", "选中区域成功", JOptionPane.INFORMATION_MESSAGE);
            clearSelection();
            highPriorityPoints.clear();
            lowPriorityPoints.clear();
            label.repaint();
          }
        });

    saveButton.addActionListener(actSave);
    removeMaskButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(
                frame, "成功移除选中区域的保护与易除性", "成功", JOptionPane.INFORMATION_MESSAGE);
            seamCarver.removeMask();
            clearSelection();
            highPriorityPoints.clear();
            lowPriorityPoints.clear();
            label.repaint();
          }
        });
  }

  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("文件");
    JMenuItem menuItemOpen = new JMenuItem("打开");
    JMenuItem menuItemSave = new JMenuItem("保存");
    JMenuItem menuItemExit = new JMenuItem("退出");
    fileMenu.add(menuItemOpen);
    fileMenu.add(menuItemSave);
    fileMenu.add(menuItemExit);

    menuItemOpen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("请选择一张图片");
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

    menuItemSave.addActionListener(actSave);

    menuItemExit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });

    JMenu helpMenu = new JMenu("帮助");
    JMenuItem menuItemAbout = new JMenuItem("关于");
    JMenuItem menuItemTips = new JMenuItem("贴士和技巧");
    helpMenu.add(menuItemAbout);
    helpMenu.add(menuItemTips);

    menuItemAbout.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(
                frame,
                "Seam Carver\n\n"
                    + "一个基于能量函数的图片缩放工具\n"
                    + "作者：杨璐祯, 赵蕾悦, 梁瑞健\n"
                    + "版本：1.0\n"
                    + "github地址：https://github.com/Jupiter2143/dsaa",
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
          }
        });

    menuItemTips.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(
                frame,
                "<html>1. 长按spinBox右侧的上下按钮可连续增大或减小数值<br>"
                    + "2. 在spinBox中输入数值后按回车键可直接修改图片大小<br>"
                    + "3. 状态栏显示鼠标当前位置的坐标<br>"
                    + "4. 长按撤销和重做按钮可连续撤销或重做<br>"
                    + "5. 点击套索工具按钮后，用鼠标在图片上拖动即可选中区域<br>"
                    + "6. 点击保存按钮可将当前图片保存到本地<br>"
                    + "7. 点击还原按钮可还原到原始图片<br>",
                "贴士和技巧",
                JOptionPane.INFORMATION_MESSAGE);
          }
        });
    menuBar.add(fileMenu);
    menuBar.add(helpMenu);
    frame.setJMenuBar(menuBar);
  }

  private void initStatusBar() {
    statusBar = new JLabel("状态栏");
    statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    frame.add(statusBar, BorderLayout.SOUTH);
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
    actSave =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
              seamCarver.save(fileChooser.getSelectedFile().getAbsolutePath());
            }
          }
        };
  }

  private void openFileFunc() {
    JFileChooser fileChooser = new JFileChooser(".");
    fileChooser.setDialogTitle("请选择一张图片");
    int result = fileChooser.showOpenDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
      seamCarver = new SeamCarver(fileChooser.getSelectedFile().getAbsolutePath());
    } else {
      System.exit(0);
    }
  }

  private void initLabel() {
    openFileFunc();
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

    label.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
    label.setHorizontalAlignment(JLabel.LEFT);
    label.setVerticalAlignment(JLabel.TOP);

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
              seamCarver.setMask(calculateMask());
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

          @Override
          public void mouseMoved(MouseEvent e) {
            statusBar.setText("x: " + e.getX() + ", y: " + e.getY());
          }
        });
  }
}
