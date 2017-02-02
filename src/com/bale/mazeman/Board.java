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
	private int numDots = 0;
	private int numGhosts = 0;
	private int currentSpeed;
	private int mazemanx, mazemany, mazemandx, mazemandy;
	private int reqdx, reqdy, viewdx, viewdy;
	private int[] dx, dy;
	private int[] ghostx, ghosty, ghostdx, ghostdy, ghostSpeed;

	private Image mazeman1;
	private Image ghost;

	private boolean inGame = false;
	private boolean dead = false;
	private boolean gameOver = false;
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
		ghost = new ImageIcon(Board.class.getResource("/ghost.png")).getImage();
		mazeman1 = new ImageIcon(Board.class.getResource("/mazeman.png")).getImage();
	}

	private void initVariables() {
		screenData = new int[NUM_BLOCKS * NUM_BLOCKS];
		dimension = new Dimension(400, 400);
		dx = new int[4];
		dy = new int[4];
		ghostx = new int[numGhosts];
		ghosty = new int[numGhosts];
		ghostdx = new int[numGhosts];
		ghostdy = new int[numGhosts];
		ghostSpeed = new int[numGhosts];
		mazeColor = new Color(5, 100, 5);
		dotColor = new Color(192, 192, 0);
		timer = new Timer(40, this);
		timer.start();
	}

	private void initGame() {
		if (inGame) {
			score = -1;// starts at 1
			lives = 3;
			//Sound.theme.loop();
			currentSpeed = 3;
			initLevel();
		}
	}

	private void initLevel() {
		for (int i = 0; i < NUM_BLOCKS * NUM_BLOCKS; i++) {
			screenData[i] = LEVEL_DATA[i];
		}

		if (numDots == 0) {
			int i = 0;
			for (int y = 0; y < SCREEN_SIZE; y+= BLOCK_SIZE) {
				for (int x = 0; x < SCREEN_SIZE; x+= BLOCK_SIZE) {
					// 16 == dot
					if ((screenData[i] & 16) != 0) {
						numDots++;
					}
					i++;
				}
			}
			numDots--;// since score starts at 1
		}

		continueLevel();
	}

	private void continueLevel() {
		int dy = 1;
		int dx = 1;

		for (int i = 0; i < numGhosts; i++) {
			ghosty[i] = 4 * BLOCK_SIZE;
			ghostx[i] = 4 * BLOCK_SIZE;
			ghostdy[i] = 0;
			ghostdx[i] = dx;
			dx = -dx;
			ghostSpeed[i] = currentSpeed;
		}

		mazemanx = 7 * BLOCK_SIZE;
		mazemany = 11 * BLOCK_SIZE;
		mazemandx = 0;
		mazemandy = 0;
		reqdx = 0;
		reqdy = 0;
		viewdx = -1;
		viewdy = 0;
		dead = false;
	}

	private void showIntroScreen(Graphics2D g2d) {
		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

		String title = "Mazeman";
		String s = "Press s to start.";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = this.getFontMetrics(small);

		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(title, (SCREEN_SIZE - metr.stringWidth(title)) / 2, SCREEN_SIZE / 2 - 10);
		g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, (SCREEN_SIZE / 2) + 10);
	}

	private void showGameOverScreen(Graphics2D g2d) {
		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

		String title = "Game Over!";
		String s = "Press s to start again.";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = this.getFontMetrics(small);

		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(title, (SCREEN_SIZE - metr.stringWidth(title)) / 2, SCREEN_SIZE / 2 - 10);
		g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, (SCREEN_SIZE / 2) + 10);
	}

	private void showScore(Graphics2D g2d) {
		String str;
		Font smallFont = new Font("Helvetica", Font.BOLD, 14);
		g2d.setFont(smallFont);
		g2d.setColor(new Color(96, 128, 255));
		str = "Score: " + score;
		g2d.drawString(str, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

		for (int i = 0; i < lives; i++) {
			g2d.drawImage(mazeman1, i * 28 + 8, SCREEN_SIZE + 1, this);
		}
	}

	private void playGame(Graphics2D g2d) {
		if (dead) {
			death(g2d);
		} else {
			moveMazeman();
			moveGhosts(g2d);
			drawMazeman(g2d);
		}
	}

	private void death(Graphics2D g2d) {
		lives--;
		Sound.playerhurt.play();
		if (lives == 0) {
			Sound.theme.stop();
			Sound.death.play();
			inGame = false;
			gameOver = true;
		}
		continueLevel();
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
			// BLOCK_SIZE = 24; NUM_BLOCKS = 15;
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

	private void moveGhosts(Graphics2D g2d) {
		int pos;
		int count;

		for (int i = 0; i < numGhosts; i++) {
			if (ghostx[i] % BLOCK_SIZE == 0 && ghosty[i] % BLOCK_SIZE == 0) {
				pos = (int)(ghostx[i] / BLOCK_SIZE) + (NUM_BLOCKS * (int)(ghosty[i] / BLOCK_SIZE));

				count = 0;

				/*
				 * 1 == left corner
				 * 4 == right corner
				 * 2 == top corner
				 * 8 == bottom corner	
				 */
				/*
				 * If there is no obstacle on the specified corner and the ghost is not already moving to the specified dir, 
				 * the ghost will move to the left. Otherwise, ghost will continue moving down its path until he is at the 
				 * end.
				 */
				if ((screenData[pos] & 1) == 0 && ghostdx[i] != 1) {
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}

				if ((screenData[pos] & 2) == 0 && ghostdy[i] != 1) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}

				if ((screenData[pos] & 4) == 0 && ghostdx[i] != -1) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}

				if ((screenData[pos] & 8) == 0 && ghostdy[i] != -1) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}

				if (count == 0) {
					if ((screenData[pos] & 15) == 15) {
						ghostdx[i] = 0;
						ghostdy[i] = 0;
					} else {
						ghostdx[i] = -ghostdx[i];
						ghostdy[i] = -ghostdy[i];
					}
				} else {
					count = (int)(Math.random() * count);
					if (count > 3) {
						count = 3;
					}

					ghostdx[i] = dx[count];
					ghostdy[i] = dy[count];
				}
			}

			ghostx[i] = ghostx[i] + (ghostdx[i] * ghostSpeed[i]);
			ghosty[i] = ghosty[i] + (ghostdy[i] * ghostSpeed[i]);
			drawGhost(g2d, ghostx[i] + 1, ghosty[i] + 1);

			if (mazemanx > (ghostx[i] - 12) && mazemanx < (ghostx[i] + 12) 
					&& mazemany > (ghosty[i] - 12) && mazemany < (ghosty[i] + 12) && inGame) {
				dead = true;
			}
		}
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

	private void drawGhost(Graphics2D g2d, int x, int y) {
		g2d.drawImage(ghost, x, y, this);
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

		System.out.println("score: " + score);
		System.out.println("numDots: " +  numDots);
		if (score == numDots) {
			System.out.println("score: " + score);
			System.out.println("numDots: " +  numDots);
			gameOver = true;
		}

		if (gameOver) {
			showGameOverScreen(g2d);
			return;
		} else if (!inGame) {
			showIntroScreen(g2d);
			return;
		}

		drawMaze(g2d);
		showScore(g2d);

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
					if (timer.isRunning()) {
						try {
							Sound.theme.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						timer.stop();
					}
					else 
						timer.start();
				} else {
					if (key == 's' || key == 'S') {
						inGame = true;
						gameOver = false;
						score = -1;
						initGame();
					}
				}
			} else {
				if (key == 's' || key == 'S') {
					inGame = true;
					gameOver = false;
					score = -1;
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