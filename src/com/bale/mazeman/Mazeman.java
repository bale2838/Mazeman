package com.bale.mazeman;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Mazeman extends JFrame {
	public Mazeman() {
		// init
		add(new Board());
		setTitle("Mazeman");		
		setSize(380, 420);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Mazeman ex = new Mazeman();
				//ex.setVisible(true);
			}
		});
	}
}