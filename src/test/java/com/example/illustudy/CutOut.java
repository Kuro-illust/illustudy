//画像トリミングのテスト
package com.example.illustudy;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class CutOut extends JFrame implements ActionListener {

	BufferedImage Img;
	BufferedImage cutImg;
	MyPanel1 mypane1;
	int h;
	int w;
	int x0;
	int y0;
	int xEnd;
	int yEnd;
	int deltaX;
	int deltaY;
	int counter = 0;


	//画像を開く
	public void OpenImage () {
		try {
		File openFile = new File ("開きたいファイル");
		Img = ImageIO.read(openFile);
		h = Img.getHeight();  //画像の高さを取得
		w = Img.getWidth();    //画像の幅を取得
		} catch (IOException ex) {
			System.out.println("Miss");
		}

		//画像をフレームに貼る
		JFrame imgFrame = new JFrame("Open Image");
		imgFrame.setBounds (300, 100, 520, 410);
		imgFrame.setVisible(true);

		mypane1 = new MyPanel1 (520, 410);
		imgFrame.getContentPane().add(mypane1, BorderLayout.CENTER);
	}

	//画像を貼る用のツール
	public class MyPanel1 extends JPanel {
		public MyPanel1 (int width, int height) {
			setSize (width, height);
		}

		public void paintComponent (Graphics g) {
			g.drawImage(Img, 0, 0, this);
		}
	}

	//ROIの始点と終点を取得
	class MouseCheck extends MouseInputAdapter {

		//マウスが押された点を調べる
		public void mousePressed(MouseEvent e) {
			x0 = e.getX();  //始点のx座標
			y0 = e.getY();   // 始点のy座標
		}

		//マウスが放された点を調べる
		public void mouseReleased (MouseEvent e) {
			if (counter == 0) {
			Graphics g = mypane1.getGraphics();
			xEnd = e.getX();  //終点のx座標
			yEnd = e.getY();  //終点のy座標
			g.drawLine(x0, y0, x0, yEnd);
			g.drawLine(x0, y0, xEnd, y0);
			g.drawLine(x0, yEnd, xEnd, yEnd);
			g.drawLine(xEnd, y0, xEnd, yEnd);
			counter++;
			}
		}
	}

	//ROIを書く
	public void roiDraw () {
		mypane1.addMouseListener(new MouseCheck());
		mypane1.addMouseMotionListener(new MouseCheck());
	}


	//ROIで囲んだ領域の画像の切り出し
	public void CutImage () {
		//x0, y0, xEnd, yEndの並び替え
		if (x0 > xEnd) {
			int k = x0;
			x0 = xEnd;
			xEnd = k;
		}
		if (y0 > yEnd) {
			int k = y0;
			y0 = yEnd;
			yEnd = k;
		}

		//切り取る幅の取得
		deltaX = xEnd - x0;
		deltaY = yEnd - y0;

		//切り取った画像を入れ物の作成
		cutImg = new BufferedImage (deltaX, deltaY, Img.getType());
		cutImg= Img.getSubimage(x0, y0, deltaX, deltaY);

		//切り取った画像の表示用のフレール
		JFrame cutFrame = new JFrame ("Cut");
		cutFrame.setSize(deltaX, deltaY);
		cutFrame.setLocationRelativeTo(null);
		cutFrame.setVisible(true);
		MyPanel3 mypane3 = new MyPanel3(deltaX, deltaY);
		cutFrame.getContentPane().add(mypane3, BorderLayout.CENTER);
	}

	//切り取った画像の表示用ツール
	public class MyPanel3 extends JPanel {
		public MyPanel3 (int width, int height) {
			setSize (width, height);
		}

		public void paintComponent (Graphics gp) {
			gp.drawImage(cutImg, 0, 0, this);
		}
	}

	public void actionPerformed (ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("open")) {
			OpenImage();
		} else if (cmd.equals("ROI")) {
			roiDraw();
		} else if (cmd.equals("cut")) {
			CutImage();
		}
	}

	CutOut (String title) {
		setTitle(title);
		setBounds (10, 10 , 200, 170);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JButton button = new JButton ("Open");
		JButton roiBut = new JButton ("ROI");
		JButton button1 = new JButton ("Cut");
		button.setBounds(50, 10, 80, 30);
		roiBut.setBounds (50, 50, 80, 30);
		button1.setBounds(50, 90, 80, 30);
		button.addActionListener (this);
		button1.addActionListener(this);
		roiBut.addActionListener(this);
		button.setActionCommand("open");
		roiBut.setActionCommand("ROI");
		button1.setActionCommand("cut");
		JPanel pane = new JPanel ();
		pane.setLayout(null);
		pane.add(button);
		pane.add(roiBut);
		pane.add(button1);
		getContentPane().add(pane, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		CutOut frame = new CutOut ("Cut Out");
		frame.setVisible(true);
	}
}