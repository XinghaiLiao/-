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
	 * �����������
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
	 * �ļ�ͷ
	 */
	private byte[] bfType = new byte[2];
	private byte[] bfSize = new byte[4];
	private byte[] bfReserved1 = new byte[2];
	private byte[] bfReserved2 = new byte[2];
	private byte[] bfOffBits = new byte[4];
	
	/**
	 * ��Ϣͷ
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
	 * ��ɫ���ͼ������
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
		 * ��ʼ���ļ�ѡ�񴰿�
		 */
		aChooser = new JFileChooser();
		//���ô򿪴���ʱ��Ĭ��·��
		aChooser.setCurrentDirectory(new File("."));
		//�����ļ���ѡ
		aChooser.setFileFilter(new FileNameFilter());
		//����ָ���ļ����͵�ͼƬ
		aChooser.setFileView(new FileIconView(new FileNameFilter(), new ImageIcon("src/img/palette.gif")));
		//�����ļ����ڵ��ļ�Ԥ��
		aChooser.setAccessory(new ImagePreviewer(aChooser));
		
		/**
		 * ��ʼ���˵���
		 */
		aMenuBar = new JMenuBar();
		fMenu = new JMenu("�ļ�");
		fOpenFile = new JMenuItem("��");
		fSaveFile = new JMenuItem("����");
		fSaveAs = new JMenuItem("���Ϊ");
		fExit = new JMenuItem("�˳�");
		//��APP��JMenuItem���ÿ�ݼ�
		fOpenFile.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		fSaveFile.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		fSaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
		//��APP��JMenuItem��ӵ�JMenu,��JMenu��ӵ�JMenuBar
		aMenuBar.add(fMenu);
		fMenu.add(fOpenFile);
		fMenu.addSeparator();//�˷����������JMenuItem�����ķָ���
		fMenu.add(fSaveFile);
		fMenu.addSeparator();
		fMenu.add(fSaveAs);
		fMenu.addSeparator();
		fMenu.add(fExit);
		//��APP��JMenuItem�������¼�����
		fOpenFile.addActionListener(this);
		fSaveFile.addActionListener(this);
		fSaveAs.addActionListener(this);
		fExit.addActionListener(this);
		
		/**
		 * ��ʼ�������˵�
		 */
		aPopupMenu = new JPopupMenu();
		JMenuItem popOpenFileItem = new JMenuItem("��");
		JMenuItem popSaveFileItem = new JMenuItem("����");
		JMenuItem popSaveAsItem = new JMenuItem("���Ϊ");
		//����¼�����
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
		 * ��ʼ������
		 */
		this.setTitle("����Lagrange��ֵ��ͼ�������㷨��ʵ�� " + "201705500216");//���ô��ڱ���
		this.setSize(new Dimension(700, 700));//���ô��ڴ�С
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//����˳�ʲô������
		this.setLocationByPlatform(true);
		this.setJMenuBar(aMenuBar);//���ô��ڲ˵���
		this.addMouseListener(this);//���������
		this.addWindowListener(new WindowAdapter() { //����˳��¼�����
			public void windowClosing(WindowEvent e) {
				String title = "ȷ���˳�";
				String message = "ȷ���˳���";
				int option = JOptionPane.YES_NO_OPTION;
				int messageType = JOptionPane.QUESTION_MESSAGE;
				int buttonValue = JOptionPane.showConfirmDialog(LagrangeBMP.this, message, title, option, messageType);
				if(buttonValue == JOptionPane.YES_OPTION) {
					LagrangeBMP.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
		this.setIconImage(Toolkit.getDefaultToolkit().createImage("src/img/icon.jpg"));//���ô���icon
		//���ô�������
		Container c = this.getContentPane();//�����������
		aLabel = new MyLabel();
		aLabel.addMouseListener(this);
		aPanel = new JPanel();
		lHeight = new JLabel("�߶�:");
		lWidth = new JLabel("���:");
		lInterpolation = new JLabel("��ֵ����:");
		btnOK = new JButton("ȷ��");
		tWidth = new JTextField("0", 5);
		tWidth.setToolTipText("����ͼ����");
		tHeight = new JTextField("0", 5);
		tHeight.setToolTipText("����ͼ��߶�");
		tInterpolation = new JTextField("3", 2);
		tInterpolation.setToolTipText("��������ͼ��ʱʹ�õĲ�ֵ����");
		iAlpha = new JSlider(0, 255, 0);
		iAlpha.setMajorTickSpacing(50);
		iAlpha.setMinorTickSpacing(5);
		iAlpha.setToolTipText("Ч������");
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
		btnOK.addActionListener(new ActionListener() {//ȷ����ť�¼��������¼�����
			public void actionPerformed(ActionEvent e) {
				if(Integer.parseInt(tHeight.getText()) <= 0 || Integer.parseInt(tWidth.getText()) <= 0) {
					JOptionPane.showMessageDialog(LagrangeBMP.this, "�������", "ʧ��", JOptionPane.WARNING_MESSAGE);
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
		String className = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";//���ô��ڹ۸�
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(this);
		} catch(Exception e) {
			
		}
		this.setVisible(true);//���ô��ڿɼ�
	}
	
	/**
	 * �¼�����
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == fExit) { //�˳��¼�����
			System.exit(0);
		} 
		if(e.getSource() == fOpenFile) {//���ļ��¼�����
			openFile();
		} else if(e.getSource() == fSaveFile) {//�����ļ��¼�����
			saveFile();
		} else if(e.getSource() == fSaveAs) {//���Ϊ�¼�����
			saveAs();
		} 
	}
	
	/**
	 * ���ļ�
	 */
	private void openFile() {
		int result = aChooser.showOpenDialog(LagrangeBMP.this);//��ʾ�ļ�ѡ��Ի���
		if(result == JFileChooser.APPROVE_OPTION) {//�ļ��Ի�����ȷ������JFileChooser.APPROVE_OPTION
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
				JOptionPane.showMessageDialog(this, "��ͼƬʧ�ܣ�", "ʧ��", JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ȡBMP
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
	 * �����ļ�
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
	 * ���Ϊ
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

	//����������¼�
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
	 * ��byte���飨С�ˣ�תint
	 * @param by byte����
	 * @return ת�����intֵ
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
	 * ͼƬ������
	 * @param height ͼƬ�߶�
	 * @param width ͼƬ���
	 * @param interpolation ��ֵ����
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
	 * intתbyte���飨С�ˣ�
	 * @param num Ҫת����intֵ
	 * @return byte[4]����
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
	 * byteתdouble
	 * @param arr byteֵ
	 * @return ת�����doubleֵ
	 */
	private double byte2Double(byte b) {
		long value = 0;
		value |= ((long) (b & 0xff));
		return Double.longBitsToDouble(value);
	}
	
	/**
	 * doubleת��byte
	 * @param d Ҫת����doubleֵ
	 * @return ת�����byteֵ
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
	 * ��дpaint()����������̬��ʾͼƬ
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
 * �ļ������� �����ļ�����Ĭ����ʾָ�����ļ�����
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
 * �ļ���ͼ ���ļ�ƥ�������ʱ��ʾָ��ͼƬ
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
 * ��ѡ���ļ�ʱ�ṩԤ��
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
					//����ͼƬ��Ӧ��С
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