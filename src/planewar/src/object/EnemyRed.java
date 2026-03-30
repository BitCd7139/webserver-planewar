package object;

import ui.Game;

public class EnemyRed extends Enemy{

    public EnemyRed(int speed, int health) {
        super(1, health, speed, 5, Game.enemyRed);
    }

}