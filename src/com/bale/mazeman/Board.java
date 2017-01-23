package com.bale.mazeman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.Timer;

public class Board {
	private Dimension dimension;
	private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

	private Image img;
	private final Color dotColor = new Color(192, 192, 0);
	private Color mazeColor;

	private boolean inGame = false;
	private boolean isDying = false;

	private final int blockSize = 24;
	private final int numBlocks = 15;
	private final int screenSize = blockSize * numBlocks;
	private final int pacAnimDelay = 2;
	private final int pacmanAnimCount = 4;
	private final int maxGhosts = 12;
	private final int pacSpeed = 6;

	private int pacAnimCount = pacAnimDelay;
	private int pacAnimDir = 1;
	private int pacmanAnimPos = 0;
	private int numGhosts = 6;
	private int pacsLeft, score;
	private int[] dx, dy;
	private int[] ghostx, ghosty, ghostdx, ghostdy, ghostSpeed;

	private Image ghost;
	private Image pacman1, pacman2up, pacman2down, pacman2left, pacman2right;
	private Image pacman3up, pacman3down, pacman3left, pacman3right;
	private Image pacman4up, pacman4down, pacman4left, pacman4right;

	private int pacmanx, pacmany, pacmandx, pacmandy;
	private int reqdx, reqdy, viewdx, viewdy;

	private final short levelData[] = {
			19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
			21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
			21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
			21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
			17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
			17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
			25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
			1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
			1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
			1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
			1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
			1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
			1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
			1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
			9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
	};

	private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
	private final int maxSpeed = 6;
	
	private int currentSpeed = 3;
	private short[] screenData;
	private Timer timer;

	public Board() {
		
	}
}