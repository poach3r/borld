An extremely simple platformer to help me learn Java.
![image](https://github.com/poach3r/borld/assets/58641438/c042bb14-960f-4441-b2f5-d404ee0fde8b)

# Documentation

## Controls

A: Move Left
D: Move Right
Space: Jump

## Assets

All non-level assets are stored as follows
```
./assets/
./levels/assets/
```

### ./assets/

This folder contains all player and enemy assets. 
You can add assets or replace assets and they will have proper collision out of the box.

### ./levels/assets/

This folder contains all level assets.

## Levels

Levels contain 3 core components, these are 
```
./levels/data/level(n).txt
./levels/data/level(n)Enemy.txt
./levels/assets/level(n).png
./levels/data/levelVars.txt
```

### ./levels/data/level(n).txt

This file contains the collision for the (n) level and is formatted as follows.

L1: Whether the level has an enemy or not (int enemyPresent)
L2-17: Collision for each tile of the level (int floor[x])

### ./levels/data/level(n)Enemy.txt

This file contains the enemy data for (n) level and is formatted as follows.

L1: Starting X Value of enemy (int enemyX, defaults to 512)
L2: Starting Y Value of enemy (int enemyY, defaults to 0)
L3: xVelocity of enemy (int enemyXVelocity, defaults to 0)
L4: yFallVelocity of enemy (int enemyYFallVelocity, defaults to 16)
L5: 7JumpVelocity of enemy (int enemyYJumpVelocity, defaults to 0, currently unused)
L6: The asset used by the enemy (int enemyImage, enemy(n).png)

### ./levels/assets/level(n).png

This file contains the graphics for (n) level, it is a 512x512 .png file and each tile is 32x32px.

### .levels/data/levelVars.txt

This is a universal file that declares the level variables

L1: The starting level (int activeLevel)
L2: The last level (int lastLevel)