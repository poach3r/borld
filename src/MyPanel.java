import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;

public class MyPanel extends JPanel implements ActionListener {

	final int PANEL_WIDTH = 512;
	final int PANEL_HEIGHT = 512;
	Image player;
	Image levelImage;
	Timer timer;
	KeyHandler keyH = new KeyHandler();
	int floor[] = new int[520];
	int xVelocity = 8, yFallVelocity = 8, yJumpVelocity = 4;
	int x = 0, y = 0;
	int jump = 0;
	int activeLevel = 0;
	boolean jumping = false;
	int i = 0;
	int n = 0;
	int largest = 0;
	
	//constructor
	public MyPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		//this.setBackground(Color.WHITE);
		player = new ImageIcon("./assets/player.png").getImage();
		//Thread movement = new Thread(this);
    	//movement.start();
		timer = new Timer(16, this);
		timer.start();
		this.addKeyListener(keyH);
		this.setFocusable(true);
		readLevelData();
		loadLevel();
	}

	//create player and level
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(levelImage, 0, 0, null);
        g2D.drawImage(player, x, y, null);
    }

	//read in level data from level(n).txt
	public void readLevelData() {
		try {
			i = 0;
			File levelFile = new File("./levels/data/level" + activeLevel + ".txt");
			Scanner levelReader = new Scanner(levelFile);
			while (levelReader.hasNextLine()) {
			  	floor[i] = (levelReader.nextInt());
				for(n = 0; n < 32; n++) //give data to pixels in between tiles
					floor[i+n] = floor[i];
				i += 32;
				n = 0;
			}
			levelReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	//load level image from level(n).png
	//may eventually replace with a system that draws the level from the level data
	public void loadLevel() {
		levelImage = new ImageIcon("./levels/assets/level" + activeLevel + ".png").getImage();
	}

	//movement logic
    @Override
    public void actionPerformed(ActionEvent e) {
		//jumping logic
		if(jump > 0) { //if players jump has not ended
			y -= yJumpVelocity;
			jump -= 1;
			jumping = true;
		}
		else if(y < PANEL_HEIGHT - (floor[x] > floor[x + player.getHeight(null)]? floor[x]: floor[x + player.getHeight(null)]) - player.getHeight(null)) { //if player is in air and jump has ended
			y += yFallVelocity;
			jumping = true;
		}
		if(keyH.spacePressed == true && jumping == false) //if jumps
			jump = 16;
		else
			jumping = false;
		
		//left logic
		if(keyH.leftPressed == true && PANEL_HEIGHT - y - player.getHeight(null) >= floor[x - 4] && x - 8 != 0 ) {
			x -= xVelocity;
		}
		else if(keyH.leftPressed == true && x - 8 == 0) {
			x = PANEL_WIDTH - player.getWidth(null);
			activeLevel -= 1;
			readLevelData();
			loadLevel();
		}
		
		//right logic
		if(keyH.rightPressed == true && PANEL_HEIGHT - y - player.getHeight(null) >= floor[x + 4 + player.getWidth(null)] && x + 8 != PANEL_WIDTH - player.getWidth(null)) {
			x += xVelocity;	
		}
		else if(keyH.rightPressed == true && x + 8 == PANEL_WIDTH - player.getWidth(null)) {
			x = 0;
			activeLevel += 1;
			readLevelData();
			loadLevel();
		}
		repaint();
    }
}
