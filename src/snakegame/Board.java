/**
 * 
 */
package snakegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author eibhl
 *
 */
public class Board extends JPanel implements ActionListener {
	
	private final int B_WIDTH = 300;
	private final int B_HEIGHT = 300;
	private final int DOT_SIZE = 10;
	private final int ALL_DOTS = 900;	// maximum number of points available on the board
	private final int RAND_POS = 29;	// random position constant to calculate random position of apple on board
	private final int DELAY = 140;		// defines speed of game
	
	private final int x[] = new int[ALL_DOTS];	// x coordinate of board
	private final int y[] = new int[ALL_DOTS];	// y coordinate of board
	
	private int dots;		// number of dots making up snake
	private int apple_x;	// x coordinate of apple
	private int apple_y;	// y coordinate of apple
	
	// booleans to indicate which directional key is being pressed
	private boolean leftDirection = false;
	private boolean rightDirection = true;
	private boolean upDirection = false;
	private boolean downDirection = false;
	private boolean inGame = true;	// boolean indicates that 'game over' condition hasn't been met
	
	private Timer timer;	// timer object
	private Image ball;		// ball object
	private Image apple;	// apple object
	private Image head;		// head of snake object
	
	/**
	 * 	Method creates Board and calls for initial game conditions and the
	 * 	images of the apple and snake components to be loaded
	 */
	public Board() {
		addKeyListener(new TAdapter());
		setBackground(Color.black);
		setFocusable(true);
		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
		loadImages();
		initGame();
	}
	
	/**
	 * 	Method gets images for apple and snake component objects from the 'res' folder
	 */
	private void loadImages() {
		ImageIcon iid = new ImageIcon(getClass().getResource("/res/dot.png"));
		ball = iid.getImage();
		
		ImageIcon iia = new ImageIcon(getClass().getResource("/res/apple.png"));
		apple = iia.getImage();
		
		ImageIcon iih = new ImageIcon(getClass().getResource("/res/head.png"));
		head = iih.getImage();
	}
	
	/**
	 * 	Method sets initial number of dots that make up snake and their position,
	 * 	calls for a random position for the apple and starts the timer
	 */
	public void initGame() {
		dots = 3;
		
		for (int z = 0; z < dots; z++) {
			x[z] = 50 - z * 10;
			y[z] = 50;
		}
		
		locateApple();
		
		// we use a timer on a timer to call action performed method at fixed delay
		timer = new Timer(DELAY, this);
		timer.start();
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}
	
	/**
	 * Method takes graphics object and draws apple and snake components at 
	 * given coordinates unless 'game over' condition has been met
	 * @param g
	 */
	private void doDrawing(Graphics g) {
		if (inGame) {
			g.drawImage(apple, apple_x, apple_y, this);
			
			for (int z = 0; z < dots; z++) {
				if (z == 0) {
					g.drawImage(head, x[z], y[z], this);	// if dot is the first, method will draw head
				} else {
					g.drawImage(ball, x[z], y[z], this);	// if dot is not the first, will draw ball
				}
			}
			
			Toolkit.getDefaultToolkit().sync();	// synchronises graphics to avoid potential buffering
			
		} else {
			gameOver(g);
		}
	}
	
	/**
	 * Method displays 'Game Over' message on window
	 * @param g
	 */
	private void gameOver(Graphics g) {
		String msg = "Game Over";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
	}
	
	/**
	 * Method will add a dot to the number of dots making up the snake and will call for
	 * a new apple position if the head of the snake passes over the current coordinates of the apple
	 */
	private void checkApple() {
		if(x[0] == apple_x && y[0] == apple_y) {
			dots++;
			locateApple();
		}
	}
	
	/**
	 * Method indicates coordinates of dots that make up the snake given the current direction of movement
	 */
	private void move() {
		// gives each dot of the snake the last position of the dot directly ahead of it
		for (int z = dots; z > 0; z--) {
			x[z] = x[z-1];
			y[z] = y[z-1];
		}
		
		// if statements move head of snake left, right, up or down given direction of movement
		// if head of snake reaches any border of the window, the x/y coordinate will be moved 
		// to the opposite border
		if (leftDirection) {
			x[0] -= DOT_SIZE;
			if (x[0] < 0) {
				x[0] = B_WIDTH;
			}
		}
		
		if (rightDirection) {
			x[0] += DOT_SIZE;
			if (x[0] >= B_WIDTH) {
				x[0] = 0;
			}
		}
		
		if (upDirection) {
			y[0] -= DOT_SIZE;
			if (y[0] < 0) {
				y[0] = B_HEIGHT;
			}
		}
		
		if (downDirection) {
			y[0] += DOT_SIZE;
			if (y[0] >= B_HEIGHT) {
				y[0] = 0;
			}
		}		
	}
	
	/**
	 * Method checks if the head of the snake hits any point on the rest of the snake
	 * in which case 'game over' condition is met and the timer is stopped
	 */
	private void checkCollision() {
		for (int z = dots; z > 0; z--) {
			if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
				inGame = false;
			}
		}
		
		if (!inGame) {
			timer.stop();
		}
	}
	
	/**
	 * Method creates random x and y coordinate for the apple
	 */
	private void locateApple() {
		int r = (int)(Math.random() * RAND_POS);
		apple_x = r * DOT_SIZE;
		
		r = (int)(Math.random() * RAND_POS);
		apple_y = r * DOT_SIZE;
	}
			
	@Override
	public void actionPerformed(ActionEvent e) {
		if (inGame) {
			checkApple();
			checkCollision();
			move();
		}
		
		repaint();
		
	}
	
	/**
	 * Class describes which key on the keyboard is being pressed for directional movement of snake
	 * and sets other directions to false. If conditions for direction ensure snake cannot double back
	 * on itself
	 * @author eibhl
	 *
	 */
	private class TAdapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_LEFT && !rightDirection) {
				leftDirection = true;
				upDirection = false;
				downDirection = false;
			}
			
			if (key == KeyEvent.VK_RIGHT && !leftDirection) {
				rightDirection = true;
				upDirection = false;
				downDirection = false;
			}
			
			if (key == KeyEvent.VK_UP && !downDirection) {
				leftDirection = false;
				upDirection = true;
				rightDirection = false;
			}
			
			if (key == KeyEvent.VK_DOWN && !upDirection) {
				leftDirection = false;
				rightDirection = false;
				downDirection = true;
			}
			
		}
		
	}

}
