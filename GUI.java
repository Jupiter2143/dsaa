import java.awt.*;
import java.awt.event.*;
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
  private JScrollPane scrollPane;
  private JPanel rightPanel = new JPanel();
  private ImageIcon imageIcon;
  private JLabel label;
  private JPanel h1 = new JPanel();
  private JSpinner wSpinner;
  private JSpinner hSpinner;
  private JButton resizeButton;

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

  private void initScrollPane() {
    imageIcon = new ImageIcon(seamCarver.picture());
    label = new JLabel(imageIcon);
    scrollPane = new JScrollPane(label);
    panel.add(scrollPane, BorderLayout.CENTER);
  }

  private void initRightPanel() {
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    inith1();
    initButtons();
    panel.add(rightPanel, BorderLayout.EAST);
  }

  private void inith1() {
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

  private void initButtons() {
    resizeButton = new JButton("Resize");
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
  }
}
