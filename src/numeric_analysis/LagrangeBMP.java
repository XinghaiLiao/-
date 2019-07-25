package numeric_analysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

/**
 * 
 * @author liaoxinghai
 *
 */
public class LagrangeBMP extends JFrame implements ActionListener, MouseListener {
	/**
	 * 界面变量声明
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar aMenuBar = null;
	private JMenu fMenu = null;
	private JMenuItem fOpenFile = null, fSaveFile = null, fSaveAs = null, fExit = null;
	private JFileChooser aChooser = null;
	private JPopupMenu aPopupMenu = null;
	private MyLabel aLabel = null;
	private String filePath = null;
	private JPanel aPanel = null;
	private JLabel lHeight = null, lWidth = null, lInterpolation = null;
	private JButton btnOK = null;
	private JTextField tHeight = null, tWidth = null, tInterpolation = null;
	private JSlider iAlpha = null;
	
	/**
	 * 文件头
	 */
	private byte[] bfType = new byte[2];
	private byte[] bfSize = new byte[4];
	private byte[] bfReserved1 = new byte[2];
	private byte[] bfReserved2 = new byte[2];
	private byte[] bfOffBits = new byte[4];
	
	/**
	 * 信息头
	 */
	private byte[] biSize = new byte[4];
	private byte[] biWidth = new byte[4];
	private byte[] biHeight = new byte[4];
	private byte[] biPlanes = new byte[2];
	private byte[] biBitCount = new byte[2];
	private byte[] biCompression = new byte[4];
	private byte[] biSizeImage = new byte[4];
	private byte[] biXPelsPerMeter = new byte[4];
	private byte[] biYPelsPerMeter = new byte[4];
	private byte[] biClrUsed = new byte[4];
	private byte[] biClrImportant = new byte[4];
	
	/**
	 * 调色板和图像数据
	 */
	private byte[] clrPal = null;
	private byte[] data = null;
	
	private int[] dataColor = null;
	private int newHeight = 0;
	private int newWidth = 0;
	private int interpolation = 0;
	private int bitCount = 0;
	private int iHeight = 0, iWidth = 0;
	
	public LagrangeBMP() {
		/**
		 * 初始化文件选择窗口
		 */
		aChooser = new JFileChooser();
		//设置打开窗口时的默认路径
		aChooser.setCurrentDirectory(new File("."));
		//设置文件赛选
		aChooser.setFileFilter(new FileNameFilter());
		//设置指定文件类型的图片
		aChooser.setFileView(new FileIconView(new FileNameFilter(), new ImageIcon("src/img/palette.gif")));
		//设置文件窗口的文件预览
		aChooser.setAccessory(new ImagePreviewer(aChooser));
		
		/**
		 * 初始化菜单栏
		 */
		aMenuBar = new JMenuBar();
		fMenu = new JMenu("文件");
		fOpenFile = new JMenuItem("打开");
		fSaveFile = new JMenuItem("保存");
		fSaveAs = new JMenuItem("另存为");
		fExit = new JMenuItem("退出");
		//给APP的JMenuItem设置快捷键
		fOpenFile.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		fSaveFile.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		fSaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
		//将APP的JMenuItem添加到JMenu,将JMenu添加到JMenuBar
		aMenuBar.add(fMenu);
		fMenu.add(fOpenFile);
		fMenu.addSeparator();//此方法用于添加JMenuItem组件间的分割线
		fMenu.add(fSaveFile);
		fMenu.addSeparator();
		fMenu.add(fSaveAs);
		fMenu.addSeparator();
		fMenu.add(fExit);
		//给APP的JMenuItem组件添加事件监听
		fOpenFile.addActionListener(this);
		fSaveFile.addActionListener(this);
		fSaveAs.addActionListener(this);
		fExit.addActionListener(this);
		
		/**
		 * 初始化弹出菜单
		 */
		aPopupMenu = new JPopupMenu();
		JMenuItem popOpenFileItem = new JMenuItem("打开");
		JMenuItem popSaveFileItem = new JMenuItem("保存");
		JMenuItem popSaveAsItem = new JMenuItem("另存为");
		//添加事件监听
		popOpenFileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		popSaveFileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		popSaveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		aPopupMenu.add(popOpenFileItem);
		aPopupMenu.add(popSaveFileItem);
		aPopupMenu.add(popSaveAsItem);
		
		/**
		 * 初始化窗口
		 */
		this.setTitle("基于Lagrange插值的图像缩放算法的实现 " + "201705500216");//设置窗口标题
		this.setSize(new Dimension(700, 700));//设置窗口大小
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//点击退出什么都不干
		this.setLocationByPlatform(true);
		this.setJMenuBar(aMenuBar);//设置窗口菜单栏
		this.addMouseListener(this);//添加鼠标监听
		this.addWindowListener(new WindowAdapter() { //添加退出事件监听
			public void windowClosing(WindowEvent e) {
				String title = "确认退出";
				String message = "确认退出？";
				int option = JOptionPane.YES_NO_OPTION;
				int messageType = JOptionPane.QUESTION_MESSAGE;
				int buttonValue = JOptionPane.showConfirmDialog(LagrangeBMP.this, message, title, option, messageType);
				if(buttonValue == JOptionPane.YES_OPTION) {
					LagrangeBMP.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
		this.setIconImage(Toolkit.getDefaultToolkit().createImage("src/img/icon.jpg"));//设置窗口icon
		//设置窗口内容
		Container c = this.getContentPane();//窗口内容面板
		aLabel = new MyLabel();
		aLabel.addMouseListener(this);
		aPanel = new JPanel();
		lHeight = new JLabel("高度:");
		lWidth = new JLabel("宽度:");
		lInterpolation = new JLabel("插值个数:");
		btnOK = new JButton("确定");
		tWidth = new JTextField("0", 5);
		tWidth.setToolTipText("设置图像宽度");
		tHeight = new JTextField("0", 5);
		tHeight.setToolTipText("设置图像高度");
		tInterpolation = new JTextField("3", 2);
		tInterpolation.setToolTipText("设置缩放图像时使用的插值个数");
		iAlpha = new JSlider(0, 255, 0);
		iAlpha.setMajorTickSpacing(50);
		iAlpha.setMinorTickSpacing(5);
		iAlpha.setToolTipText("效果不明");
		iAlpha.setPaintTicks(true);
		iAlpha.setPaintLabels(true);
		aPanel.add(lHeight);
		aPanel.add(tHeight);
		aPanel.add(lWidth);
		aPanel.add(tWidth);
		aPanel.add(lInterpolation);
		aPanel.add(tInterpolation);
		aPanel.add(new JLabel("alpha:"));
		aPanel.add(iAlpha);
		btnOK.addActionListener(new ActionListener() {//确定按钮事件监听及事件处理
			public void actionPerformed(ActionEvent e) {
				if(Integer.parseInt(tHeight.getText()) <= 0 || Integer.parseInt(tWidth.getText()) <= 0) {
					JOptionPane.showMessageDialog(LagrangeBMP.this, "输入错误！", "失败", JOptionPane.WARNING_MESSAGE);
				} else {
					if(clrPal != null) { 
						int alpha = iAlpha.getValue();
						for(int i = 3; i < clrPal.length; i+=4) {
							clrPal[i] = (byte) alpha;
						}
						newHeight = Integer.parseInt(tHeight.getText());
						newWidth = Integer.parseInt(tWidth.getText());
						interpolation = Integer.parseInt(tInterpolation.getText());
						aLabel.setPreferredSize(new Dimension(newWidth, newHeight));
						changePic(newHeight, newWidth, interpolation);
						int skip = 4 - ((newWidth * 8)>>3) & 3;
						dataColor = new int[newHeight*newWidth];
						for(int i = 0; i < newHeight; i++) {
							for(int j = 0; j < newWidth + skip; j++) {
								if(j < newWidth) {
									dataColor[i*newWidth+j] = new Color(data[i*(newWidth+skip)+j]&0xff, data[i*(newWidth+skip)+j]&0xff, data[i*(newWidth+skip)+j]&0xff, alpha).getRGB();
								}
							}
						}
						aLabel.repaint();
					} else {
						int alpha = iAlpha.getValue();
						newHeight = Integer.parseInt(tHeight.getText());
						newWidth = Integer.parseInt(tWidth.getText());
						int interpolation = Integer.parseInt(tInterpolation.getText());
						int skip = 4 - ((newWidth * 24)>>3) & 3;
						aLabel.setPreferredSize(new Dimension(newWidth, newHeight));
						dataColor = new int[newHeight*newWidth];
						changePic(newHeight, newWidth,interpolation);
						for(int i = 0; i < newHeight; i++) {
							for(int j = 0; j < newWidth + skip; j++) {
								if(j < newWidth) {
									dataColor[i*newWidth+j] = new Color(data[i*(newWidth*3+skip)+3*j+2]&0xff, data[i*(newWidth*3+skip)+3*j+1]&0xff, data[i*(newWidth*3+skip)+3*j]&0xff, alpha).getRGB();
								}
							}
						}
						aLabel.repaint();
					}
				}
			}
		});
		aPanel.add(btnOK);
		aLabel.setHorizontalAlignment(JLabel.CENTER);
		c.add(new JScrollPane(aLabel), BorderLayout.CENTER);
		c.add(aPanel, BorderLayout.SOUTH);
		String className = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";//设置窗口观感
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(this);
		} catch(Exception e) {
			
		}
		this.setVisible(true);//设置窗口可见
	}
	
	/**
	 * 事件处理
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == fExit) { //退出事件处理
			System.exit(0);
		} 
		if(e.getSource() == fOpenFile) {//打开文件事件处理
			openFile();
		} else if(e.getSource() == fSaveFile) {//保存文件事件处理
			saveFile();
		} else if(e.getSource() == fSaveAs) {//另存为事件处理
			saveAs();
		} 
	}
	
	/**
	 * 打开文件
	 */
	private void openFile() {
		int result = aChooser.showOpenDialog(LagrangeBMP.this);//显示文件选择对话框
		if(result == JFileChooser.APPROVE_OPTION) {//文件对话框点击确定返回JFileChooser.APPROVE_OPTION
			File file = aChooser.getSelectedFile();
			if(file != null) {
				filePath = file.getAbsolutePath();
			}
			try {
				Image image = ImageIO.read(file);
				ImageIcon icon = new ImageIcon(image);
				iHeight = icon.getIconHeight();
				iWidth = icon.getIconHeight();
				tHeight.setText("" + iHeight);
				tWidth.setText("" + iWidth);
				readFile();
				int skip = 4 - ((iWidth * bitCount)>>3) & 3;
				dataColor = new int[iHeight*iWidth]; 
				aLabel.setPreferredSize(new Dimension(iWidth, iHeight));
				newHeight = iHeight;
				newWidth = iWidth;
				if(bitCount == 8)
					for(int i = 0; i < iHeight; i++) {
						for(int j = 0; j < iWidth + skip; j++) {
							if(j < iWidth) {
								dataColor[i*iWidth+j] = new Color(data[i*(iWidth + skip) + j]&0xff, data[i*(iWidth + skip) + j]&0xff, data[i*(iWidth + skip) + j]&0xff, 0).getRGB();
							}
						}
					}
				else if(bitCount == 24){
					for(int i = 0; i < iHeight; i++) {
						for(int j = 0; j < iWidth + skip; j++) {
							if(j < iWidth) {
								dataColor[i*iWidth + j] = new Color(data[i*(iWidth*3 + skip) + 3*j+2]&0xff, data[i*(iWidth*3 + skip) + 3*j+1]&0xff, data[i*(iWidth*3 + skip)+ 3*j]&0xff).getRGB();
							}
						}
					}
				}
				aLabel.repaint();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "打开图片失败！", "失败", JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取BMP
	 */
	private void readFile() {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bfType);
			bis.read(bfSize);
			bis.read(bfReserved1);
			bis.read(bfReserved2);
			bis.read(bfOffBits);
			bis.read(biSize);
			bis.read(biWidth);
			bis.read(biHeight);
			iWidth = byte2Int(biWidth);
			iHeight = byte2Int(biHeight);
			bis.read(biPlanes);
			bis.read(biBitCount);
			bis.read(biCompression);
			bis.read(biSizeImage);
			bis.read(biXPelsPerMeter);
			bis.read(biYPelsPerMeter);
			bis.read(biClrUsed);
			bis.read(biClrImportant);
			bitCount = byte2Int(biBitCount);
			int skip = 4 - ((iWidth * bitCount)>>3) & 3;
			if(bitCount == 8) {
				clrPal = new byte[256 * 4];
				bis.read(clrPal);
				data = new byte[iHeight*iWidth + skip*iHeight];
				bis.read(data);
			} else if(bitCount == 24) {
				clrPal = null;
				data = new byte[iHeight*iWidth*3 + skip*iHeight];
				bis.read(data);
			}
			fis.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存文件
	 */
	private void saveFile() {
		File file = aChooser.getSelectedFile();
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bfType);
			fos.write(bfSize);
			fos.write(bfReserved1);
			fos.write(bfReserved2);
			fos.write(bfOffBits);
			fos.write(biSize);
			fos.write(biWidth);
			fos.write(biHeight);
			fos.write(biPlanes);
			fos.write(biBitCount);
			fos.write(biCompression);
			fos.write(biSizeImage);
			fos.write(biXPelsPerMeter);
			fos.write(biYPelsPerMeter);
			fos.write(biClrUsed);
			fos.write(biClrImportant);
			if(clrPal != null) {
				fos.write(clrPal);
			}
			fos.write(data);
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 另存为
	 */
	private void saveAs() {
		int result = aChooser.showSaveDialog(LagrangeBMP.this);
		if(result == JFileChooser.APPROVE_OPTION) {
			File file = aChooser.getSelectedFile();
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(bfType);
				fos.write(bfSize);
				fos.write(bfReserved1);
				fos.write(bfReserved2);
				fos.write(bfOffBits);
				fos.write(biSize);
				fos.write(biWidth);
				fos.write(biHeight);
				fos.write(biPlanes);
				fos.write(biBitCount);
				fos.write(biCompression);
				fos.write(biSizeImage);
				fos.write(biXPelsPerMeter);
				fos.write(biYPelsPerMeter);
				fos.write(biClrUsed);
				fos.write(biClrImportant);
				if(clrPal != null) {
					fos.write(clrPal);
				}
				fos.write(data);
				fos.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	//监听鼠标点击事件
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			aPopupMenu.show(this, e.getX(), e.getY());
		}
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
	
	/**
	 * 将byte数组（小端）转int
	 * @param by byte数组
	 * @return 转换后的int值
	 */
	private static int byte2Int(byte[] by) {
		if(by.length == 4) {
			int t1 = by[3] & 0xff;
			int t2 = by[2] & 0xff;
			int t3 = by[1] & 0xff;
			int t4 = by[0] & 0xff;
			return t1 << 24 | t2 << 16 | t3 << 8 | t4;
		} else {
			int t1 = by[0] & 0xff;
			int t2 = by[1] & 0xff;
			return t2 << 8 | t1;
		}
	}
	
	/**
	 * 图片处理函数
	 * @param height 图片高度
	 * @param width 图片宽度
	 * @param interpolation 插值个数
	 */
	private void changePic(int height, int width, int interpolation) {
		int n = interpolation;
		if(bitCount == 8) {
			int skip = 0;
			if(iWidth % 4 != 0) {
				skip = 4 - iWidth%4;
			}
			double [][]x = new double[iHeight][width];
			double []xx  = new double[width];
			byte[] temp = data;
			data = new byte[iHeight*iWidth];
			for(int i = 0; i < iHeight; i++) {
				for(int j = 0; j < iWidth+skip; j++) {
					if(j < iWidth) {
						data[i*iWidth+j] = temp[i*(iWidth+skip)+j];
					}
				}
			}
			for(int i=0; i<iHeight; i++){
				for(int k=0;k<width;k++){
					x[i][k] = 0;
					double g = 1.0*k*(iWidth-1)/(width-1);
					int ii, jj;
					if((n%2) == 1) {
						ii = (int)(g+0.5) - (n-1)/2;
						jj = (int)(g+0.5) + (n-1)/2;
					}
					else {
						ii = (int)(g) - (n-2)/2;
						jj = (int)(g) + 1 + (n-2)/2;
					}
					for(int h=ii;h<=jj;h++) {
						if(h < 0) 
							xx[h-ii] = byte2Double(data[i*iWidth - h]);
						else if(h < iWidth) 
							xx[h-ii] = byte2Double(data[i*iWidth + h]);
						else xx[h-ii] = byte2Double(data[i*iWidth + 2*(iWidth-1) - h]);
					}
					x[i][k] = Lagrange(xx, n, g, ii);
				}
			}
			data = new byte[height*width];
			for(int i=0; i<height; i++){
				for(int k=0;k<width;k++){
					int pos = (int)(1.0*i*(iWidth-1)/(width-1));
					data[i*width+k] = double2Bytes(x[pos][k]);
				}
			}
			byte[] array = null;
			if(width % 4 == 0) {
				array = new byte[width*height];  
				bfSize = int2Byte(14+40+width*height);
				for(int i = 0; i < array.length; i++) {
					array[i] = data[i];
				}
			} else {
				skip = 4-width%4;
				bfSize = int2Byte(14+40+height*(width+skip));
				array = new byte[height*(width+skip)]; 
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width+skip; j++) {
						if(j < width) {
							array[i*(width+skip)+j] = data[i*width+j];
						} else {
							array[i*(width+skip)+j] = 0x00;
						}
					}
				}
			}
			data = array;
			biWidth = int2Byte(width);
			biHeight = int2Byte(height);
		} else {
			int skip = 0;
			if((iWidth*3) % 4 != 0) {
				skip = 4 - (iWidth*3)%4;
			}
			double [][]x = new double[iHeight][width];
			double []xx  = new double[width];
			byte[] temp = data;
			data = new byte[iHeight*iWidth*3];
			for(int i = 0; i < iHeight; i++) {
				for(int j = 0; j < iWidth*3+skip; j++) {
					if(j < iWidth*3) {
						data[i*iWidth*3+j] = temp[i*(iWidth*3+skip)+j];
					}
				}
			}
			byte[] bitmap1 = new byte[iHeight*iWidth];
			byte[] bitmap2 = new byte[iHeight*iWidth];
			byte[] bitmap3 = new byte[iHeight*iWidth];
			int s1 = 0, s2 = 0, s3 = 0;
			for(int i = 0; i < data.length; i++) {
				if(i % 3 == 0) {
					bitmap1[s1++] = data[i];
				} else if(i % 3 == 1) {
					bitmap2[s2++] = data[i];
				} else {
					bitmap3[s3++] = data[i];
				}
			}
			for(int i=0; i<iHeight; i++){
				for(int k=0;k<width;k++){
					x[i][k] = 0;
					double g = 1.0*k*(iWidth-1)/(width-1);
					int ii, jj;
					if((n%2) == 1) {
						ii = (int)(g+0.5) - (n-1)/2;
						jj = (int)(g+0.5) + (n-1)/2;
					}
					else {
						ii = (int)(g) - (n-2)/2;
						jj = (int)(g) + 1 + (n-2)/2;
					}
					for(int h=ii;h<=jj;h++) {
						if(h < 0) 
							xx[h-ii] = byte2Double(bitmap1[i*iWidth - h]);
						else if(h < iWidth) 
							xx[h-ii] = byte2Double(bitmap1[i*iWidth + h]);
						else xx[h-ii] = byte2Double(bitmap1[i*iWidth + 2*(iWidth-1) - h]);
					}
					x[i][k] = Lagrange(xx, n, g, ii);
				}
			}
			bitmap1 = new byte[height*width];
			for(int i=0; i<height; i++){
				for(int k=0;k<width;k++){
					int pos = (int)(1.0*i*(iWidth-1)/(width-1));
					bitmap1[i*width+k] = double2Bytes(x[pos][k]);
				}
			}
			for(int i=0; i<iHeight; i++){
				for(int k=0;k<width;k++){
					x[i][k] = 0;
					double g = 1.0*k*(iWidth-1)/(width-1);
					int ii, jj;
					if((n%2) == 1) {
						ii = (int)(g+0.5) - (n-1)/2;
						jj = (int)(g+0.5) + (n-1)/2;
					}
					else {
						ii = (int)(g) - (n-2)/2;
						jj = (int)(g) + 1 + (n-2)/2;
					}
					for(int h=ii;h<=jj;h++) {
						if(h < 0) 
							xx[h-ii] = byte2Double(bitmap2[i*iWidth - h]);
						else if(h < iWidth) 
							xx[h-ii] = byte2Double(bitmap2[i*iWidth + h]);
						else xx[h-ii] = byte2Double(bitmap2[i*iWidth + 2*(iWidth-1) - h]);
					}
					x[i][k] = Lagrange(xx, n, g, ii);
				}
			}
			bitmap2 = new byte[height*width];
			for(int i=0; i<height; i++){
				for(int k=0;k<width;k++){
					int pos = (int)(1.0*i*(iWidth-1)/(width-1));
					bitmap2[i*width+k] = double2Bytes(x[pos][k]);
				}
			}
			for(int i=0; i<iHeight; i++){
				for(int k=0;k<width;k++){
					x[i][k] = 0;
					double g = 1.0*k*(iWidth-1)/(width-1);
					int ii, jj;
					if((n%2) == 1) {
						ii = (int)(g+0.5) - (n-1)/2;
						jj = (int)(g+0.5) + (n-1)/2;
					}
					else {
						ii = (int)(g) - (n-2)/2;
						jj = (int)(g) + 1 + (n-2)/2;
					}
					for(int h=ii;h<=jj;h++) {
						if(h < 0) 
							xx[h-ii] = byte2Double(bitmap3[i*iWidth - h]);
						else if(h < iWidth) 
							xx[h-ii] = byte2Double(bitmap3[i*iWidth + h]);
						else xx[h-ii] = byte2Double(bitmap3[i*iWidth + 2*(iWidth-1) - h]);
					}
					x[i][k] = Lagrange(xx, n, g, ii);
				}
			}
			bitmap3 = new byte[height*width];
			for(int i=0; i<height; i++){
				for(int k=0;k<width;k++){
					int pos = (int)(1.0*i*(iWidth-1)/(width-1));
					bitmap3[i*width+k] = double2Bytes(x[pos][k]);
				}
			}
			s1 = 0; s2 = 0; s3 = 0;
			data = new byte[width*height*3];
			for(int i = 0; i < data.length; i++) {
				if(i % 3 == 0) {
					data[i] = bitmap1[s1++];
				} else if(i % 3 == 1) {
					data[i] = bitmap2[s2++];
				} else {
					data[i] = bitmap3[s3++];
				}
			}
			if(width % 4 == 0) {  
				bfSize = int2Byte(14+40+width*height*3);
			} else {
				byte[] array = null;
				skip = 4-(width*3)%4;
				bfSize = int2Byte(14+40+height*(width*3+skip));
				array = new byte[height*(width*3+skip)]; 
				for(int i = 0; i < height; i++) {
					for(int j = 0; j < width*3+skip; j++) {
						if(j < width*3) {
							array[i*(width*3+skip)+j] = data[i*width*3+j];
						} else {
							array[i*(width*3+skip)+j] = 0x00;
						}
					}
				}
				data = array;
			}
			biWidth = int2Byte(width);
			biHeight = int2Byte(height);
		}
	}
	
	/**
	 * int转byte数组（小端）
	 * @param num 要转换的int值
	 * @return byte[4]数组
	 */
	private byte[] int2Byte(int num){
		byte[] bytes = new byte[4];
		bytes[3] = (byte)((num>>24)&0xff);
		bytes[2] = (byte)((num>>16)&0xff);
		bytes[1] = (byte)((num>>8)&0xff);
		bytes[0] = (byte)(num&0xff);
		return bytes;
	}
	
	/**
	 * byte转double
	 * @param arr byte值
	 * @return 转换后的double值
	 */
	private double byte2Double(byte b) {
		long value = 0;
		value |= ((long) (b & 0xff));
		return Double.longBitsToDouble(value);
	}
	
	/**
	 * double转吧byte
	 * @param d 要转换的double值
	 * @return 转换后的byte值
	 */
	private byte double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte byteRet;
		byteRet = (byte) (value & 0xff);
		return byteRet;
	}
	
	private double Lagrange(double x[], int num, double xx, int pos) {
		double yy = 0;
		for(int i = 0; i < num; ++i) {
			double temp = 1;
			for(int j = pos; j < pos+num; ++j) {
				if(i == j-pos) 
					continue;
				temp += (xx-j)*1.0/(i+pos-j);
			}
			yy += x[i]*temp;
		}
		return yy;
	}
	
	/**
	 * 重写paint()方法用来动态显示图片
	 * @author liaoxinghai
	 *
	 */
	class MyLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		
		public void paint(Graphics g) {
			super.paint(g);
			for(int i = 0; i < newHeight; i++) {
				for(int j = 0; j < newWidth; j++) {
					g.setColor(new Color(dataColor[i*newWidth+j]));
					g.drawLine(j + 2, newHeight-i + 2, j + 2, newHeight-i + 2);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new LagrangeBMP();
	}
}

/**
 * 文件过滤器 用于文件窗口默认显示指定的文件类型
 * @author liaoxinghai
 *
 */
class FileNameFilter extends FileFilter {
	public boolean accept(File f) {
		return f.getName().endsWith(".bmp");
	}

	public String getDescription() {
		return "BMP";
	}
}

/**
 * 文件视图 当文件匹配过滤器时显示指定图片
 * @author liaoxinghai
 *
 */
class FileIconView extends FileView {
	private FileFilter filter;
	private Icon icon;
	
	public FileIconView(FileFilter filter, Icon icon) {
		this.filter = filter;
		this.icon = icon;
	}
	
	public Icon getIcon(File f) {
		if(!f.isDirectory() && filter.accept(f)) {
			return icon;
		}
		return null;
	}
}

/**
 * 当选中文件时提供预览
 * @author liaoxinghai
 *
 */
class ImagePreviewer extends JLabel {
	private static final long serialVersionUID = 1L;

	public ImagePreviewer(JFileChooser chooser) {
		this.setPreferredSize(new Dimension(100, 100));
		this.setBorder(BorderFactory.createEtchedBorder());
		chooser.addPropertyChangeListener(event->{
			if(event.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
				File f = (File)event.getNewValue();
				if(f == null) {
					setIcon(null);
					return;
				}
				try {
					Image image = ImageIO.read(f);
					ImageIcon icon = new ImageIcon(image);
					//调整图片适应大小
					if(icon.getIconWidth() > this.getWidth()) {
						icon = new ImageIcon(icon.getImage().getScaledInstance(this.getWidth(), -1, Image.SCALE_DEFAULT));
					}
					this.setIcon(icon);
					this.repaint();
				} catch (IOException e) {
				}
			}
		});
	}
}