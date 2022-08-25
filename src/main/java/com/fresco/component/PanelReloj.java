package com.fresco.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class PanelReloj extends JComponent {

	private static final long serialVersionUID = 1L;

	private final static int FPS = 60;
	private final static int TARGET_TIME = 1_000_000_000 / FPS;

	private Graphics2D g2;
	private BufferedImage image;
	private int widthPanel;
	private int heightPanel;
	private int width;
	private int height;
	private int width2;
	private int height2;
	private Icon background;
	private boolean start = true;
	private int offsetX;
	private int offsetY;
	private ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	private int capture;
	private int fps = FPS;

	record Point(int x, int y) {
	};

	public void start() {
		capture = 0;
		background = new ImageIcon(classLoader.getResource("pngegg.png"));
		width = background.getIconWidth();
		height = background.getIconHeight();
		width2 = width / 2 - 10;
		height2 = height / 2 - 10;
		widthPanel = getWidth();
		heightPanel = getHeight();
		offsetX = (widthPanel - width) / 2;
		offsetY = (heightPanel - height) / 2;

		image = new BufferedImage(widthPanel, heightPanel, BufferedImage.TYPE_INT_ARGB);
		g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setFont(new Font("Monospaced", Font.PLAIN, 18));
		var thread = new Thread(() -> {
			while (start) {
				long startTime = System.nanoTime();
				drawBackGround();
				drawReloj();
				g2.drawString("fps= " + fps, 50, 50);
				render();
				long endTime = System.nanoTime();
				long time = endTime - startTime;
				if (time < TARGET_TIME) {
					fps = FPS;
					long sleep = (TARGET_TIME - time) / 1_000_000;
					sleep(sleep);
				}
				else {
					fps = (int) (1_000_000_000 / time);
				}
			}
		});
		thread.start();
	}

	private void drawBackGround() {
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, widthPanel, heightPanel);
		g2.drawImage(toImage(background), offsetX, offsetY, null);
	}

	private void drawReloj() {
		var now = LocalTime.now();
		var hour = now.getHour();
		hour = hour > 12 ? hour - 12 : hour;
		var angleh = 90 - (hour * 30);

		drawManecilla(angleh, Color.BLUE, 8);
		var min = now.getMinute();
		var anglem = 90 - (min * 6);

		drawManecilla(anglem, Color.GREEN, 5);
		var nano = now.getNano() / 1_000_000_000f;
		var sec = now.getSecond() + nano;
		var angles = 90 - (sec * 6);
		drawManecilla(angles, Color.RED, 2);

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(1));
		var p1 = getPoint(-10, 10);
		g2.fillOval(p1.x, p1.y, 20, 20);
		g2.setStroke(new BasicStroke(4));
		var p2 = getPoint(-15, 15);
		g2.drawOval(p2.x, p2.y, 30, 30);
	}

	private void drawManecilla(float alfa, Color c, float lineWidth) {
		var beta = 180 + alfa;
		var alfaInRadians = Math.toRadians(alfa);
		var betaInRadians = Math.toRadians(beta);
		var p = getPoint(0, 0);
		var x1 = width2 / 5 * Math.cos(betaInRadians);
		var y1 = height2 / 5 * Math.sin(betaInRadians);
		var p1 = getPoint(x1, y1);
		var x2 = width2 * Math.cos(alfaInRadians);
		var y2 = height2 * Math.sin(alfaInRadians);
		var p2 = getPoint(x2, y2);
		g2.setColor(c);
		g2.setStroke(new BasicStroke(lineWidth));
		g2.drawLine(p1.x, p1.y, p.x, p.y);
		g2.drawLine(p.x, p.y, p2.x, p2.y);
	}

	private Point getPoint(double x, double y) {
		int w = width / 2 + offsetX;
		int h = height / 2 + offsetY;
		return new Point((int) Math.round(x + w), (int) Math.round(h - y));
	}

	private void render() {
		var g = getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ex) {
			System.out.println(ex);
		}
	}

	private Image toImage(Icon icon) {
		return ((ImageIcon) icon).getImage();
	}

	public void capture() {
		if (capture > 3600) {
			return;
		}
		try {
			var f = Integer.toString(capture);
			var fileName = "/tmp/capture" + "0".repeat(4 - f.length()) + f + ".png";
			var fileCapture = new File(fileName);

			if (!fileCapture.exists()) {
				fileCapture.createNewFile();
			}
			try (var fos = new FileOutputStream(fileCapture))
			{
				ImageIO.write(image, "png", fos);
			}
			capture++;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
