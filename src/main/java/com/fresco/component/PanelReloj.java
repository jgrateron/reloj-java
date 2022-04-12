package com.fresco.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.time.LocalTime;

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

	record Point(int x, int y) {
	};

	public void start() {
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
		var thread = new Thread(() -> {
			while (start) {
				long startTime = System.nanoTime();
				drawBackGround();
				drawReloj();
				render();
				long endTime = System.nanoTime();
				long time = endTime - startTime;
				if (time < TARGET_TIME) {
					long sleep = (TARGET_TIME - time) / 1_000_000;
					// System.out.println(sleep);
					sleep(sleep);
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
	}

	private void drawManecilla(float angle, Color c, float lineWidth) {
		double inRadians = Math.toRadians(angle);
		var x = width2 * Math.cos(inRadians);
		var y = height2 * Math.sin(inRadians);
		var p1 = getPoint(0, 0);
		var p2 = getPoint((int) Math.round(x), (int) Math.round(y));
		g2.setColor(c);
		g2.setStroke(new BasicStroke(lineWidth));
		g2.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	private Point getPoint(int x, int y) {
		int w = width / 2 + offsetX;
		int h = height / 2 + offsetY;
		return new Point(x + w, h - y);
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

}
