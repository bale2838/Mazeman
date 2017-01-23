package com.bale.mazeman;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Pacman extends JFrame {
	public Pacman() {
		// init window stuff
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
				Pacman ex = new Pacman();
				ex.setVisible(true);
			}
		});
	}
}