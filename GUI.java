import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class GUI {
  public GUI() {
    // 创建主窗口
    JFrame frame = new JFrame("Seam Carver");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) (screenSize.width * 0.70);
    int height = (int) (screenSize.height * 0.70);
    frame.setSize(width, height);

    frame.setLayout(new BorderLayout());

    // 创建子窗口（JPanel）
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    // 创建并添加图片对象到子窗口
    ImageIcon imageIcon = new ImageIcon("example.jpg"); // 替换为图片的路径
    JLabel label = new JLabel(imageIcon);

    // 创建一个滚动面板并将标签添加到其中
    JScrollPane scrollPane = new JScrollPane(label);
    panel.add(scrollPane, BorderLayout.CENTER);
    // panel.add(label, BorderLayout.CENTER);

    // 创建按钮面板
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(0, 1)); // 一列多行
    JButton button1 = new JButton("按钮1");
    JButton button2 = new JButton("按钮2");
    // 添加按钮到按钮面板
    buttonPanel.add(button1);
    buttonPanel.add(button2);
    // 将按钮面板添加到子窗口的右侧
    panel.add(buttonPanel, BorderLayout.EAST);

    // 创建菜单栏
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("文件");
    JMenuItem menuItem1 = new JMenuItem("打开");
    JMenuItem menuItem2 = new JMenuItem("保存");
    JMenuItem menuItem3 = new JMenuItem("退出");
    // 添加菜单项到菜单
    menu.add(menuItem1);
    menu.add(menuItem2);
    menu.add(menuItem3);
    // 添加菜单到菜单栏
    menuBar.add(menu);
    // 将菜单栏设置到窗口
    frame.setJMenuBar(menuBar);

    // 创建状态栏
    JLabel statusBar = new JLabel("状态栏");
    statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    frame.add(statusBar, BorderLayout.SOUTH);

    // 将子窗口（JPanel）添加到主窗口（JFrame）
    frame.add(panel, BorderLayout.CENTER);
    // 将主窗口放在屏幕中间
    frame.setLocationRelativeTo(null);
    // 显示主窗口
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    new GUI();
  }
}
