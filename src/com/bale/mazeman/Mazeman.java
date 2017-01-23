package com.bale.mazeman;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Mazeman extends JFrame {
	public Mazeman() {
		// init window stuff
		add(new Board());
		setTitle("Pacman");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(380, 420);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Mazeman ex = new Mazeman();
				ex.setVisible(true);
			}
		});
	}
}