import java.awt.*;
import java.awt.event.*;
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

  //显示图、鼠标监听、套索实施
  private void initScrollPane() {
    imageIcon = new ImageIcon(seamCarver.picture());
    
    label = new JLabel(imageIcon) {
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

              g.setColor(Color.RED);
              g.drawPolygon(xPoints, yPoints, points.size()); // 绘制套索选区
          }
      }
  };

  int width = imageIcon.getIconWidth();
  int height = imageIcon.getIconHeight();
  label.setPreferredSize(new Dimension(width, height));

  JScrollPane scrollPane = new JScrollPane(label);
  panel.add(scrollPane, BorderLayout.CENTER);

  label.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
          points.clear(); 
          points.add(e.getPoint()); 
          label.repaint(); // 重新绘制以显示套索选区
      }

      @Override
      public void mouseReleased(MouseEvent e) {
          points.add(e.getPoint()); // 添加最后一个点
          label.repaint(); 
      }
  });

  label.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
          points.add(e.getPoint()); // 拖动时添加点
          label.repaint(); 
      }
  });
  
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
  }

  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("文件");
    JMenuItem menuItem1 = new JMenuItem("打开");
    JMenuItem menuItem2 = new JMenuItem("保存");
    JMenuItem menuItem3 = new JMenuItem("退出");
    menu.add(menuItem1);
    menu.add(menuItem2);
    menu.add(menuItem3);
    menuBar.add(menu);
    frame.setJMenuBar(menuBar);
  }

  private void initStatusBar() {
    JLabel statusBar = new JLabel("状态栏");
    statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    frame.add(statusBar, BorderLayout.SOUTH);
    
    JButton selectButton = new JButton("套索工具");
    statusBar.add(selectButton);
    selectButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            startSelectionTool();
        }
    });
    
  }

  private void startSelectionTool() {
        // 点击则弹出一个对话框，用于提示用户进行套索选区
        JOptionPane.showMessageDialog(frame, "请在图像上用鼠标拖动进行选区。", "套索工具", JOptionPane.INFORMATION_MESSAGE);
    }

}
