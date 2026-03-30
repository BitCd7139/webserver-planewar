package object;

import lombok.Data;
import ui.Game;

@Data
public class Boom extends FlyingObject {
    private int speed = 6;
    private int offset;
    public Boom(int x, int y, int offset) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.type = -2;
        this.image = Game.boomImg;
    }

    public void move () {
        this.y -= speed;
        this.x += offset;
    }

    public boolean outOfBounds() {
        return this.y < -this.height && this.x >= 366;
    }
}