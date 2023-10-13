import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;

public class MyPanel extends JPanel implements ActionListener {

	//declare shit
	final int PANEL_WIDTH = 512;
	final int PANEL_HEIGHT = 512;
	Image player;
	Image enemy;
	Image levelImage;
	Timer timer;
	KeyHandler keyH = new KeyHandler();
	int floor[] = new int[600];
	int xVelocity = 8, yFallVelocity = 16, yJumpVelocity = 4;
	int x = 0, y = 0;
	int enemyXVelocity = 0, enemyYFallVelocity = 16, enemyYJumpVelocity = 0;
	int enemyX = 0, enemyY = 0;
	int enemyImage = 0;
	int jump = 0;
	boolean jumping = false;
	boolean gameOver = false;
	int i = 0;
	int n = 0;
	File levelFile;
	int activeLevel = 0;
	int lastLevel = 0;
	int enemyPresent = 0;
	
	//constructor
	public MyPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		player = new ImageIcon("./assets/player.png").getImage();
		enemy = new ImageIcon("./assets/enemy0.png").getImage();
		timer = new Timer(16, this);
		timer.start();
		this.addKeyListener(keyH);
		this.setFocusable(true);
		File levelVars = new File("./levels/data/levelVars.txt");
		try {
			Scanner varReader = new Scanner(levelVars);
			activeLevel = varReader.nextInt();
			lastLevel = varReader.nextInt();
			varReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		readLevelData();
		loadLevel();
	}

	//create player and level
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(levelImage, 0, 0, null);
        g2D.drawImage(player, x, y, null);
		g2D.drawImage(enemy, enemyX, enemyY, null);
    }

	//read in level data from level(n).txt
	public void readLevelData() {
		enemyPresent = 0;
		enemyXVelocity = 0;
		enemyX = 512;
		enemyY = 0;
		enemyYFallVelocity = 16;
		enemyYJumpVelocity = 0;
		try {
			i = 0;
			if(gameOver == true) 
				levelFile = new File("./levels/data/gameover.txt");
			else
				levelFile = new File("./levels/data/level" + activeLevel + ".txt");
			Scanner levelReader = new Scanner(levelFile);
			enemyPresent = levelReader.nextInt();
			System.out.println(enemyPresent);
			while (levelReader.hasNextLine()) {
			  	floor[i] = (levelReader.nextInt());
				for(n = 0; n < 32; n++) //give data to pixels in between tiles
					floor[i+n] = floor[i];
				System.out.println(floor[i]);
				i += 32;
				n = 0;
			}
			levelReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		//read enemy data
		if(enemyPresent == 1) {
			try {
				File levelFileEnemy = new File("./levels/data/level" + activeLevel + "Enemy.txt");
				Scanner levelReaderEnemy = new Scanner(levelFileEnemy);
				while (levelReaderEnemy.hasNextLine()) {
					enemyX = levelReaderEnemy.nextInt() - enemy.getWidth(null);
					enemyY = (PANEL_HEIGHT - levelReaderEnemy.nextInt() - enemy.getHeight(null));
					enemyXVelocity = levelReaderEnemy.nextInt();
					enemyYFallVelocity = levelReaderEnemy.nextInt();
					enemyYJumpVelocity = levelReaderEnemy.nextInt();
					enemyImage = levelReaderEnemy.nextInt();
				}
				enemy = new ImageIcon("./assets/enemy" + enemyImage + ".png").getImage();
				levelReaderEnemy.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
	}

	//load level image from level(n).png
	//may eventually replace with a system that draws the level from the level data
	public void loadLevel() {
		if(gameOver == true)
			levelImage = new ImageIcon("./levels/assets/gameover.png").getImage();
		else
			levelImage = new ImageIcon("./levels/assets/level" + activeLevel + ".png").getImage();
	}

	//movement logic
    @Override
    public void actionPerformed(ActionEvent e) {
		//jumping logic
		if(jump > 0) { //if players jump has not ended
			y -= jump * yJumpVelocity;
			jump -= 1;
			jumping = true;
		}
		else if(y < PANEL_HEIGHT - (floor[x] > floor[x + player.getWidth(null)]? floor[x]: floor[x + player.getWidth(null)]) - player.getHeight(null)) { //if player is in air and jump has ended
			y += yFallVelocity;
			jumping = true;
		}
		if(enemyY < PANEL_HEIGHT - (floor[x] > floor[x + enemy.getWidth(null)]? floor[x]: floor[x + enemy.getWidth(null)]) - enemy.getHeight(null)) { //if player is in air and jump has ended
			enemyY += enemyYFallVelocity;
		}
		if(keyH.spacePressed == true && jumping == false) //if jumps
			jump = 8;
		else
			jumping = false;
		
		//left logic
		if(keyH.leftPressed == true && PANEL_HEIGHT - y - player.getHeight(null) >= floor[x - 4] && x - 8 != 0 ) {
			x -= xVelocity;
		}
		else if(activeLevel > 0) {
			if(keyH.leftPressed == true && x - 8 == 0) {
				x = PANEL_WIDTH - player.getWidth(null);
				activeLevel -= 1;
				readLevelData();
				loadLevel();
			}
		}
		
		//right logic
		if(keyH.rightPressed == true && PANEL_HEIGHT - y - player.getHeight(null) >= floor[x + 4 + player.getWidth(null)] && x + 8 != PANEL_WIDTH - player.getWidth(null)) {
			x += xVelocity;	
		}
		else if(activeLevel < lastLevel) {
			if(keyH.rightPressed == true && x + 8 == PANEL_WIDTH - player.getWidth(null)) {
				x = 0;
				activeLevel += 1;
				readLevelData();
				loadLevel();
			}
		}

		//enemy detection
		if(enemyPresent == 1) {
			enemyX += enemyXVelocity;
			if(PANEL_HEIGHT - y - player.getHeight(null) == 0 || (enemyX >= x && enemyX <= x + player.getWidth(null) && enemyY >= y && enemyY <= y + player.getHeight(null)) || (x >= enemyX && x <= enemyX + enemy.getWidth(null) && y >= enemyY && y <= enemyY + enemy.getHeight(null))) {
				gameOver = true;
				x = 256;
				y = 32;
				readLevelData();
				loadLevel();
			}
		}
		repaint();
    }
}