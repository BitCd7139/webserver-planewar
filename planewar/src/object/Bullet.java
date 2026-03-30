package object;

import lombok.Data;
import ui.Game;

@Data
public class Bullet extends FlyingObject {
    private int speed = 15;
    private int offset;

    public Bullet(int x, int y, int offset) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.type = -1;
        this.image = Game.bulletImg;
    }

    public void move () {
        this.y -= this.speed;
        this.x += this.offset;
    }

    public boolean outOfBounds() {
        return this.y < -this.height;
    }
}