package object;

import ui.Game;

public class EnemyBlack extends Enemy{

    public EnemyBlack(int health) {
        super(3, health, 1, 10, Game.enemyBlack);
    }

}