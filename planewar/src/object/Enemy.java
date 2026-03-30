package object;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends FlyingObject{
    private final int speed;

    public Enemy(int type, int health, int speed, int score, BufferedImage image) {
        this.type = type;
        this.speed = speed;
        this.health = health;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.y = -this.height;
        Random rand = new Random();
        this.x = rand.nextInt(400 - this.width);
        this.score = score;
    }

    public boolean outOfBounds() {
        return this.y > 654;
    }

    public void move(){
        this.y += speed;
    }
}
