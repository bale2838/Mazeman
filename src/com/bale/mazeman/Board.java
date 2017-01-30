package com.bale.mazeman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
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

import com.bale.mazeman.sound.Sound;

public class Board extends JPanel implements ActionListener {
	private final int NUM_BLOCKS = 15;
	private final int BLOCK_SIZE = 24;
	private final int SCREEN_SIZE = NUM_BLOCKS * BLOCK_SIZE;
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

	private int[] screenData;
	private Dimension dimension;
	private Image img;

	private int lives, score;
	private int mazemanx, mazemany, mazemandx, mazemandy;
	private int reqdx, reqdy, viewdx, viewdy;
	private int[] dx, dy;

	private Image mazeman1;
	

	private boolean inGame = false;
	private boolean isDying = false;
	private Color mazeColor;
	private Color dotColor;
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
	}

	private void initVariables() {
		screenData = new int[NUM_BLOCKS * NUM_BLOCKS];
		dimension = new Dimension(400, 400);
		dx = new int[4];
		dy = new int[4];
		mazeColor = new Color(5, 100, 5);
		dotColor = new Color(192, 192, 0);
		timer = new Timer(40, this);
		timer.start();
	}

	private void initGame() {
		if (inGame) {
			score = 0;
			lives = 3;
			Sound.theme.loop();
			initLevel();
		}
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
	
	private void showIntroScreen(Graphics2D g2d) {
		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
		
		String s = "Press s to start.";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = this.getFontMetrics(small);
		
		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
	}

	private void playGame(Graphics2D g2d) {
		moveMazeman();
		drawMazeman(g2d);
	}

	private void moveMazeman() {
		int pos;
		int ch;

		if (reqdx == -mazemandx && reqdy == -mazemandy) {
			mazemandx = reqdx;
			mazemandy = reqdy;
			viewdx = mazemandx;
			viewdy = mazemandy;
		}

		if (mazemanx % BLOCK_SIZE == 0 && mazemany % BLOCK_SIZE == 0) {
			pos  = (int)(mazemanx / BLOCK_SIZE) + NUM_BLOCKS * (int)(mazemany / BLOCK_SIZE);
			ch = screenData[pos];

			if ((ch & 16) != 0){
				screenData[pos] = (int)(ch & 15);
				score++;
			}

			/*
			 * 1 == left corner
			 * 4 == right corner
			 * 2 == top corner
			 * 8 == bottom corner	
			 */
			boolean hitLeftCorner = ((ch & 1) != 0);
			boolean hitRightCorner = ((ch & 4) != 0);
			boolean hitTopCorner = ((ch & 2) != 0);
			boolean hitBotCorner = ((ch & 8) != 0);
			
			boolean requestLeft = (reqdx == -1 && reqdy == 0);
			boolean requestRight = (reqdx == 1 && reqdy == 0);
			boolean requestUp = (reqdx == 0 && reqdy == -1);
			boolean requestDown = (reqdx == 0 && reqdy == 1);
			boolean noHit = !((requestLeft && hitLeftCorner)
					|| (requestRight && hitRightCorner)
					|| (requestUp && hitTopCorner)
					|| (requestDown && hitBotCorner));
			
			if (reqdx != 0 || reqdy != 0) {
				if (noHit) {
					mazemandx = reqdx;
					mazemandy = reqdy;
					viewdx = mazemandx;
					viewdy = mazemandy;
				}
			}

			// check for standstill
			boolean moveLeft = (mazemandx == -1 && mazemandy == 0);
			boolean moveRight = (mazemandx == 1 && mazemandy == 0);
			boolean moveUp = (mazemandx == 0 && mazemandy == -1);
			boolean moveDown = (mazemandx == 0 && mazemandy == 1);
			boolean hitBarrier = (moveLeft && hitLeftCorner)
					|| (moveRight && hitRightCorner)
					|| (moveUp && hitTopCorner)
					|| (moveDown && hitBotCorner);
			
			if (hitBarrier) {
				mazemandx = 0;
				mazemandy = 0;
			}
		}

		mazemanx += MAZEMAN_SPEED * mazemandx;
		mazemany += MAZEMAN_SPEED * mazemandy;
	}

	private void drawMazeman(Graphics2D g2d) {
		if (viewdx == -1) {
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
		} else if (viewdx == 1) {
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
		} else if (viewdy == -1) {
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
		} else if (viewdy == 1) {
			g2d.drawImage(mazeman1, mazemanx + 1, mazemany + 1, this);
		}
		else {
			g2d.drawImage(mazeman1, mazemanx, mazemany, this);
		}
	}

	private void drawMaze(Graphics2D g2d) {
		int i = 0;

		for (int y = 0; y < SCREEN_SIZE; y+= BLOCK_SIZE) {
			for (int x = 0; x < SCREEN_SIZE; x+= BLOCK_SIZE) {
				g2d.setColor(mazeColor);
				g2d.setStroke(new BasicStroke(2));

				// 1 == left border
				if ((screenData[i] & 1) != 0) {
					g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
				}

				// 2 == top border
				if ((screenData[i] & 2) != 0) {
					g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
				}

				// 4 == right border
				if ((screenData[i] & 4) != 0) {
					g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, 
							y + BLOCK_SIZE - 1);
				}

				// 8 == bottom border
				if ((screenData[i] & 8) != 0) {
					g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
							y + BLOCK_SIZE - 1);
				}

				// 16 == dot
				if ((screenData[i] & 16) != 0) {
					g2d.setColor(dotColor);
					g2d.fillRect(x + 11, y + 11, 2, 2);
				}

				i++;
			}
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

		drawMaze(g2d);

		if (inGame)
			playGame(g2d);
		else
			showIntroScreen(g2d);

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