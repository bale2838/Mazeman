package com.bale.mazeman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
	private final int NUM_BLOCKS = 15;
	private final int BLOCK_SIZE = 24;
	private final int MAZEMAN_SPEED = 6;

	private final short LEVEL_DATA[] = {
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

	private short[] screenData;
	private Dimension dimension;
	private Image img;

	private int lives, score;
	private int mazemanx, mazemany, mazemandx, mazemandy;
	private int reqdx, reqdy, viewdx, viewdy;
	private int[] dx, dy;
	private int mazemanAnimPos = 0;
	
	private Image mazeman1;
	private Image mazeman2up, mazeman2down, mazeman2left, mazeman2right;
	private Image mazeman3up, mazeman3down, mazeman3left, mazeman3right;
	private Image mazeman4up, mazeman4down, mazeman4left, mazeman4right;

	private boolean inGame = true;// TODO set back to false
	private boolean isDying = false;
	
	private Timer timer;

	public Board() {
		loadImages();
		initVariables();
		setFocusable(true);
		setDoubleBuffered(true);
		setBackground(Color.black);
		addKeyListener(new TAdapter());
	}

	private void loadImages() {
		mazeman1 = new ImageIcon(Board.class.getResource("/mazeman.png")).getImage();

		mazeman2up = new ImageIcon(Board.class.getResource("/up1.png")).getImage();
		mazeman3up = new ImageIcon(Board.class.getResource("/up2.png")).getImage();
		mazeman4up = new ImageIcon(Board.class.getResource("/up3.png")).getImage();

		mazeman2down = new ImageIcon(Board.class.getResource("/down1.png")).getImage();
		mazeman3down = new ImageIcon(Board.class.getResource("/down2.png")).getImage();
		mazeman4down = new ImageIcon(Board.class.getResource("/down3.png")).getImage();

		mazeman2left = new ImageIcon(Board.class.getResource("/left1.png")).getImage();
		mazeman3left = new ImageIcon(Board.class.getResource("/left2.png")).getImage();
		mazeman4left = new ImageIcon(Board.class.getResource("/left3.png")).getImage();

		mazeman2right = new ImageIcon(Board.class.getResource("/right1.png")).getImage();
		mazeman3right = new ImageIcon(Board.class.getResource("/right2.png")).getImage();
		mazeman4right = new ImageIcon(Board.class.getResource("/right3.png")).getImage();
	}

	private void initVariables() {
		screenData = new short[NUM_BLOCKS * NUM_BLOCKS];
		dimension = new Dimension(400, 400);
		dx = new int[4];
		dy = new int[4];
		timer = new Timer(40, this);
		timer.start();
	}

	private void initGame() {
		score = 0;
		lives = 3;
		initLevel();
	}


	private void initLevel() {
		for (int i = 0; i < NUM_BLOCKS * NUM_BLOCKS; i++) {
			screenData[i] = LEVEL_DATA[i];
		}
		mazemanx = 7 * BLOCK_SIZE;
		mazemany = 11 * BLOCK_SIZE;
		mazemandx = 0;
		mazemandy = 0;
		reqdx = 0;
		reqdy = 0;
		viewdx = -1;
		viewdy = 0;
		isDying = false;
	}

	private void playGame(Graphics2D g2d) {
		moveMazeman();
		drawMazeman(g2d);
	}

	private void moveMazeman() {
		int pos;
		short ch;

		if (reqdx == -mazemandx && reqdy == -mazemandy) {
			mazemandx = reqdx;
			mazemandy = reqdy;
			viewdx = mazemandx;
			viewdy = mazemandy;
		}

		if (mazemanx % BLOCK_SIZE == 0 && mazemany % BLOCK_SIZE == 0) {
			pos  = (mazemanx / BLOCK_SIZE) + NUM_BLOCKS * (int)(mazemany / BLOCK_SIZE);
			ch = screenData[pos];

			if ((ch & 16) != 0){
				screenData[pos] = (short)(ch & 15);
				score++;
			}

			if (reqdx != 0 || reqdy != 0) {
				if (!((reqdx == -1 && reqdy == 0 && (ch & 1) != 0)
						|| (reqdx == 1 && reqdy == 0 && (ch & 4) != 0)
						|| (reqdx == 0 && reqdy == -1 && (ch & 2) != 0)
						|| (reqdx == 0 && reqdy == 1 && (ch & 8) != 0))) {
					mazemandx = reqdx;
					mazemandy = reqdy;
					viewdx = mazemandx;
					viewdy = mazemandy;
				}
			}

			// check for standstill
			if ((mazemandx == -1 && mazemandy == 0 && (ch & 1) != 0)
					|| (mazemandx == 1 && mazemandy == 0 && (ch & 4) != 0)
					|| (mazemandx == 0 && mazemandy == -1 && (ch & 2) != 0)
					|| (mazemandx == 0 && mazemandy == 1 && (ch & 8) != 0)) {
				mazemandx = 0;
				mazemandy = 0;
			}
		}

		mazemanx += MAZEMAN_SPEED * mazemandx;
		mazemany += MAZEMAN_SPEED * mazemandy;
	}

	private void drawMazeman(Graphics2D g2d) {
		if (viewdx == -1) {
			drawMazemanLeft(g2d);
		} else if (viewdx == 1) {
			drawMazemanRight(g2d);
		} else if (viewdy == -1) {
			drawMazemanUp(g2d);
		} else if (viewdy == 1) {
			drawMazemanDown(g2d);
		}
	}

	private void drawMazemanUp(Graphics2D g2d) {
		switch (mazemanAnimPos) {
		case 1:
			g2d.drawImage(mazeman2up, mazemanx + 1, mazemany + 1, this);
			break;
		case 2:
			g2d.drawImage(mazeman3up, mazemanx + 1, mazemany + 1, this);
			break;
		case 3:
			g2d.drawImage(mazeman3up, mazemanx + 1, mazemany + 1, this);
			break;
		default:
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
			break;
		}
	}

	private void drawMazemanDown(Graphics2D g2d) {
		switch (mazemanAnimPos) {
		case 1: 
			g2d.drawImage(mazeman2down, mazemanx + 1, mazemany + 1, this);
			break;
		case 2: 
			g2d.drawImage(mazeman3down, mazemanx + 1, mazemany + 1, this);
			break;
		case 3: 
			g2d.drawImage(mazeman4down, mazemanx + 1, mazemany + 1, this);
			break;
		default: 
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
			break;
		}
	}

	private void drawMazemanLeft(Graphics2D g2d) {
		switch (mazemanAnimPos) {
		case 1: 
			g2d.drawImage(mazeman2left, mazemanx + 1, mazemany + 1, this);
			break;
		case 2:
			g2d.drawImage(mazeman3left, mazemanx + 1, mazemany + 1, this);
			break;
		case 3:
			g2d.drawImage(mazeman4left, mazemanx + 1, mazemany + 1, this);
			break;
		default:
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
			break;
		}
	}

	private void drawMazemanRight(Graphics2D g2d) {
		switch (mazemanAnimPos) {
		case 1: 
			g2d.drawImage(mazeman2right, mazemanx + 1, mazemany + 1, this);
			break;
		case 2: 
			g2d.drawImage(mazeman3right, mazemanx + 1, mazemany + 1, this);
			break;
		case 3: 
			g2d.drawImage(mazeman4right, mazemanx + 1, mazemany + 1, this);
			break;
		default: 
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, dimension.width, dimension.height);

		if (inGame)
			playGame(g2d);

		g2d.drawImage(img, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		initGame();
	}

	class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if (inGame) {
				if (key ==  KeyEvent.VK_LEFT) {
					reqdx = -1;
					reqdy = 0;
				} else if (key == KeyEvent.VK_RIGHT) {
					reqdx = 1;
					reqdy = 0; 
				} else if (key == KeyEvent.VK_UP) {
					reqdx = 0;
					reqdy = -1;
				} else if (key == KeyEvent.VK_DOWN) {
					reqdx = 0;
					reqdy = 1;
				} else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
					inGame = false;
				} else if (key == KeyEvent.VK_PAUSE) {
					if (timer.isRunning()) 
						timer.stop();
					else 
						timer.start();
				}
			} else {
				if (key == 's' || key == 'S') {
					inGame = true;
					initGame();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();

			if (key == Event.LEFT || key == Event.RIGHT
					|| key == Event.UP || key == Event.DOWN) {
				reqdx = 0;
				reqdy = 0;
			}
		}
	}
}