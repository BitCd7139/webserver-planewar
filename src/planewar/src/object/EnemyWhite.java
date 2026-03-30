package object;

import ui.Game;

public class EnemyWhite extends Enemy{

    public EnemyWhite(int health) {
        super(4, health, 1, 10, Game.enemyWhite);
    }

}