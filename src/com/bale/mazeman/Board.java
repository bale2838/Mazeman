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
	private Dimension dimension;
	private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

	private Image img;
	private final Color dotColor = new Color(192, 192, 0);
	private Color mazeColor;

	private boolean inGame = true;
	private boolean isDying = false;

	private final int blockSize = 24;
	private final int numBlocks = 15;
	private final int screenSize = blockSize * numBlocks;
	private final int pacSpeed = 6;
	
	private final int pacAnimDelay = 2;
	private final int pacmanAnimCount = 4;
	private int pacAnimCount = pacAnimDelay;
	private int pacAnimDir = 1;
	private int pacmanAnimPos = 0;
	
	private int pacsLeft, score;
	private int[] dx, dy;
	
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
		loadImages();
		initVariables();
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(Color.black);
		setDoubleBuffered(true);
	}

	private void loadImages() {
		pacman1 = new ImageIcon(Board.class.getResource("/pacman.png")).getImage();

		pacman2up = new ImageIcon(Board.class.getResource("/up1.png")).getImage();
		pacman3up = new ImageIcon(Board.class.getResource("/up2.png")).getImage();
		pacman4up = new ImageIcon(Board.class.getResource("/up3.png")).getImage();

		pacman2down = new ImageIcon(Board.class.getResource("/down1.png")).getImage();
		pacman3down = new ImageIcon(Board.class.getResource("/down2.png")).getImage();
		pacman4down = new ImageIcon(Board.class.getResource("/down3.png")).getImage();

		pacman2left = new ImageIcon(Board.class.getResource("/left1.png")).getImage();
		pacman3left = new ImageIcon(Board.class.getResource("/left2.png")).getImage();
		pacman4left = new ImageIcon(Board.class.getResource("/left3.png")).getImage();

		pacman2right = new ImageIcon(Board.class.getResource("/right1.png")).getImage();
		pacman3right = new ImageIcon(Board.class.getResource("/right2.png")).getImage();
		pacman4right = new ImageIcon(Board.class.getResource("/right3.png")).getImage();
	}

	private void initVariables() {
		screenData = new short[numBlocks * numBlocks];
		mazeColor = new Color(5, 100, 5);
		dimension = new Dimension(400, 400);
		dx = new int[4];
		dy = new int[4];
		timer = new Timer(40, this);
		timer.start();
	}

	private void initGame() {
		pacsLeft = 3;
		score = 0;
		initLevel();
		currentSpeed = 3;
	}


	private void initLevel() {
		for (int i = 0; i < numBlocks * numBlocks; i++) {
			screenData[i] = levelData[i];
		}

		pacmanx = 7 * blockSize;
		pacmany = 11 * blockSize;
		pacmandx = 0;
		pacmandy = 0;
		reqdx = 0;
		reqdy = 0;
		viewdx = -1;
		viewdy = 0;
		isDying = false;
	}

	private void playGame(Graphics2D g2d) {
			movePacman();
			drawPacman(g2d);
	}

	private void movePacman() {
		int pos;
		short ch;

		if (reqdx == -pacmandx && reqdy == -pacmandy) {
			pacmandx = reqdx;
			pacmandy = reqdy;
			viewdx = pacmandx;
			viewdy = pacmandy;
		}

		if (pacmanx % blockSize == 0 && pacmany % blockSize == 0) {
			pos  = pacmanx / blockSize + numBlocks * (int)(pacmany / blockSize);
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
					pacmandx = reqdx;
					pacmandy = reqdy;
					viewdx = pacmandx;
					viewdy = pacmandy;
				}
			}

			// check for standstill
			if ((pacmandx == -1 && pacmandy == 0 && (ch & 1) != 0)
					|| (pacmandx == 1 && pacmandy == 0 && (ch & 4) != 0)
					|| (pacmandx == 0 && pacmandy == -1 && (ch & 2) != 0)
					|| (pacmandx == 0 && pacmandy == 1 && (ch & 8) != 0)) {
				pacmandx = 0;
				pacmandy = 0;
			}
		}
		pacmanx += pacSpeed * pacmandx;
		pacmany += pacSpeed * pacmandy;
	}

	private void drawPacman(Graphics2D g2d) {
		if (viewdx == -1) {
			drawPacmanLeft(g2d);
		} else if (viewdx == 1) {
			drawPacmanRight(g2d);
		} else if (viewdy == -1) {
			 drawPacmanUp(g2d);
		} else if (viewdy == 1) {
			drawPacmanDown(g2d);
		}
	}

	private void drawPacmanUp(Graphics2D g2d) {
		switch (pacmanAnimPos) {
		case 1:
			g2d.drawImage(pacman2up, pacmanx + 1, pacmany + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3up, pacmanx + 1, pacmany + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman3up, pacmanx + 1, pacmany + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
			break;
		}
	}

	private void drawPacmanDown(Graphics2D g2d) {
		switch (pacmanAnimPos) {
		case 1: 
			g2d.drawImage(pacman2down, pacmanx + 1, pacmany + 1, this);
			break;
		case 2: 
			g2d.drawImage(pacman3down, pacmanx + 1, pacmany + 1, this);
			break;
		case 3: 
			g2d.drawImage(pacman4down, pacmanx + 1, pacmany + 1, this);
			break;
		default: 
			g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
			break;
		}
	}

	private void drawPacmanLeft(Graphics2D g2d) {
		switch (pacmanAnimPos) {
		case 1: 
			g2d.drawImage(pacman2left, pacmanx + 1, pacmany + 1, this);
			break;
		case 2:
			g2d.drawImage(pacman3left, pacmanx + 1, pacmany + 1, this);
			break;
		case 3:
			g2d.drawImage(pacman4left, pacmanx + 1, pacmany + 1, this);
			break;
		default:
			g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
			break;
		}
	}

	private void drawPacmanRight(Graphics2D g2d) {
		switch (pacmanAnimPos) {
		case 1: 
			g2d.drawImage(pacman2right, pacmanx + 1, pacmany + 1, this);
			break;
		case 2: 
			g2d.drawImage(pacman3right, pacmanx + 1, pacmany + 1, this);
			break;
		case 3: 
			g2d.drawImage(pacman4right, pacmanx + 1, pacmany + 1, this);
			break;
		default: 
			g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		initGame();
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

		if (inGame) {
			playGame(g2d);
		} else {
			//show intro screen
		}

		g2d.drawImage(img, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
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
					if (timer.isRunning()) {
						timer.stop();
					} else {
						timer.start();
					}
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

			if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP || key == Event.DOWN) {
				reqdx = 0;
				reqdy = 0;
			}
		}
	}
}