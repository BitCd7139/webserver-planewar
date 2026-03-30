package object;

import ui.Game;

public class EnemyBlue extends Enemy{

    public EnemyBlue(int speed, int health) {
        super(2, health, speed, 5, Game.enemyBlue);
    }

}