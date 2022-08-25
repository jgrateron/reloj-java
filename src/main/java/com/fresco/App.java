package com.fresco;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.fresco.component.PanelReloj;

public class App extends JFrame {

	private static final long serialVersionUID = 1L;

	public App() {
		init();
	}

	public void init() {
		setTitle("Java Reloj");
		setSize(1280, 768);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		var panelReloj = new PanelReloj();
		add(panelReloj);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				panelReloj.start();
			}
		});
	}

	public static void main(String[] args) {
		var app = new App();
		app.setVisible(true);
	}
}
