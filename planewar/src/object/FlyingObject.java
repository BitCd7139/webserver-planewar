package object;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public abstract class FlyingObject {

    protected int x;
    protected int y;
    protected int score;
    protected int health;
    protected int type;
    protected int width;
    protected int height;
    protected BufferedImage image;

    public FlyingObject(){
    }

    public int getHeight() {
        return height;
    }

    public abstract boolean outOfBounds();

    public abstract void move();

    public boolean shootBy(FlyingObject bullet) {
        int x = bullet.x;
        int y = bullet.y;

        return this.x < x + width && this.x > x - width && this.y < y + height && this.y > y - height;
    }

}