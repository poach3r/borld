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
	int roof[] = new int[600];
	int xVelocity = 8, yFallVelocity = 16, yJumpVelocity = 4;
	int x = 0, y = 16;
	int enemyXVelocity = 0, enemyYFallVelocity = 16, enemyYJumpVelocity = 0;
	int enemyX = 0, enemyY = 0;
	int enemyImage = 0;
	int jump = 0;
	boolean jumping = false;
	boolean gameOver = false;
	int i = 0;
	int n = 0;
	File levelFloorFile;
	File levelRoofFile;
	int activeLevel = 0;
	int lastLevel = 0;
	int enemyPresent = 0;
	int roofPresent = 0;
	
	//constructor
	public MyPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		player = new ImageIcon("./assets/player.png").getImage();
		enemy = new ImageIcon("./assets/enemy0.png").getImage();
		timer = new Timer(16, this);
		timer.start();
		this.addKeyListener(keyH);
		this.setFocusable(true);
		File levelVars = new File("./levels/levelVars.txt");
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
		//read floor data
		try {
			i = 0;
			n = 0;
			if(gameOver == true) 
				levelFloorFile = new File("./levels/gameover/gameover.txt");
			else
				levelFloorFile = new File("./levels/level" + activeLevel + "/level" + activeLevel + ".txt");
			Scanner levelFloorReader = new Scanner(levelFloorFile);
			roofPresent = levelFloorReader.nextInt();
			enemyPresent = levelFloorReader.nextInt();
			System.out.println(enemyPresent);
			while (levelFloorReader.hasNextLine()) {
			  	floor[i] = (levelFloorReader.nextInt());
				for(n = 0; n < 32; n++) //give data to pixels in between tiles
					floor[i+n] = floor[i];
				System.out.println(floor[i]);
				i += 32;
				n = 0;
			}
			levelFloorReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find floor data.");
			e.printStackTrace();
		}
		//read roof data
		if(roofPresent == 1) {
		try {
				i = 0;
				n = 0;
				if(gameOver == true) 
					levelRoofFile = new File("./levels/gameover/gameoverRoof.txt");
				else
					levelRoofFile = new File("./levels/level" + activeLevel + "/level" + activeLevel + "Roof.txt");
				Scanner levelRoofReader = new Scanner(levelRoofFile);
				enemyPresent = levelRoofReader.nextInt();
				while (levelRoofReader.hasNextLine()) {
				  	roof[i] = (levelRoofReader.nextInt());
					for(n = 0; n < 32; n++) //give data to pixels in between tiles
						roof[i+n] = roof[i];
					System.out.println(roof[i]);
					i += 32;
					n = 0;
				}
				levelRoofReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("Could not find roof data.");
				e.printStackTrace();
			}
		}
		//read enemy data
		if(enemyPresent == 1) {
			try {
				File levelFileEnemy = new File("./levels/level" + activeLevel + "/level" + activeLevel + "Enemy.txt");
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
				System.out.println("Could not find enemy data.");
				e.printStackTrace();
			}
		}
	}

	//load level image from level(n).png
	//may eventually replace with a system that draws the level from the level data
	public void loadLevel() {
		if(gameOver == true)
			levelImage = new ImageIcon("./levels/gameover/gameover.png").getImage();
		else
			levelImage = new ImageIcon("./levels/level" + activeLevel + "/level" + activeLevel + ".png").getImage();
	}

	//movement logic
    @Override
    public void actionPerformed(ActionEvent e) {
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("floor[x]: " + (floor[x] - player.getHeight(null)));
		System.out.println("floor[x + player.getWidth(null)]: " + (floor[x + player.getWidth(null)] - player.getHeight(null)));

		if(roofPresent == 1) {
			//jumping logic
			if(keyH.spacePressed == true && jumping == false && y > roof[x + (player.getWidth(null) / 2)]) //if jumps
				jump = 8;
			else
				jumping = false;
			if(jump > 0) { //if players jump has not ended
				y -= jump * yJumpVelocity;
				jump -= 1;
				jumping = true;
			} //end jumping logic

			//falling logic
			//fix clipping bug and apply solution to jumping with roof
			else if(y < floor[x] - player.getHeight(null) && y < floor[x + player.getWidth(null)] - player.getHeight(null)) {
				y += yFallVelocity;
				jumping = true;
			} //end falling logic

			//left logic
			if(keyH.leftPressed == true) {
				if(y + player.getHeight(null) <= floor[x - 1] && x - 8 != 0 && y >= roof[x - 1 + player.getWidth(null)]) {
					x -= xVelocity;
				}
				else if(activeLevel > 0) {
					if(x - 8 == 0) {
						x = PANEL_WIDTH - player.getWidth(null);
						activeLevel -= 1;
					readLevelData();
						loadLevel();
					}
				}
			} //end left logic

			//right logic
			else if(keyH.rightPressed == true ) {
				if(y + player.getHeight(null) <= floor[x + 1 + player.getWidth(null)] && x + 8 != PANEL_WIDTH - player.getWidth(null) && y >= roof[x + 1 + player.getWidth(null)]) {
					x += xVelocity;	
				}
				else if(activeLevel < lastLevel) {
					if(x + 8 == PANEL_WIDTH - player.getWidth(null)) {
					x = 0;
					activeLevel += 1;
					readLevelData();
					loadLevel();
					}
				}
			} //end right logic
		}

		else { //if roofPresent == 0
			//jumping logic
			if(keyH.spacePressed == true && jumping == false) //if jumps
				jump = 8;
			else
				jumping = false;
			if(jump > 0) { //if players jump has not ended
				y -= jump * yJumpVelocity;
				jump -= 1;
				jumping = true;
			} //end jumping logic

			//falling logic
			else if(y < floor[x + (player.getWidth(null) / 2)] - player.getHeight(null)) {
				y += yFallVelocity;
				jumping = true;
			} //end falling logic

			//left logic
			if(keyH.leftPressed == true) {
				if(y + player.getHeight(null) <= floor[x - 1] && x - 8 != 0) {
					x -= xVelocity;
				}
				else if(activeLevel > 0) {
					if(x - 8 == 0) {
						x = PANEL_WIDTH - player.getWidth(null);
						activeLevel -= 1;
						readLevelData();
						loadLevel();
					}
				}
			} //end left logic

			//right logic
			else if(keyH.rightPressed == true ) {
				if(y + player.getHeight(null) <= floor[x + 1 + player.getWidth(null)] && x + 8 != PANEL_WIDTH - player.getWidth(null) && y >= roof[x + 1 + player.getWidth(null)]) {
					x += xVelocity;	
				}
				else if(activeLevel < lastLevel) {
					if(x + 8 == PANEL_WIDTH - player.getWidth(null)) {
					x = 0;
					activeLevel += 1;
					readLevelData();
					loadLevel();
					}
				}
			} //end right logic
		}
		
		//enemy logic
		if(enemyPresent == 1) {
			enemyX += enemyXVelocity;
			if(enemyY < floor[x] - enemy.getHeight(null)) //if player is in air and jump has ended
				enemyY += enemyYFallVelocity;
			if((enemyX >= x && enemyX <= x + player.getWidth(null) && enemyY >= y && enemyY <= y + player.getHeight(null)) || (x >= enemyX && x <= enemyX + enemy.getWidth(null) && y >= enemyY && y <= enemyY + enemy.getHeight(null))) {
				gameOver = true;
				x = 256;
				y = 32;
				readLevelData();
				loadLevel();
			}
		} //end enemy logic

		//dead in a ditch logic
		if(y + player.getHeight(null) == 512) {
			gameOver = true;
			x = 256;
			y = 32;
			readLevelData();
			loadLevel();
		} //end dead in a ditch logic
		repaint();
    }
}