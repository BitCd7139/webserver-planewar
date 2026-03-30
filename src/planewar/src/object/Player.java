package object;

import lombok.Data;
import ui.Game;

import java.awt.image.BufferedImage;

@Data
public class Player extends FlyingObject {
    private BufferedImage image;
    private int power = 0;
    private int spell = 3;
    private int speed = 8;
    private boolean isShoot = false;

    private int speedX;
    private int speedY;
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;
    private boolean lastMoveRight;
    private boolean lastMoveDown;

    public Player(){
        this.type = 0;
        this.image = Game.playerImg;
        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
        this.health = 3;
        this.x = 200;
        this.y = 500;
    }

    public void move(){
        if (moveLeft && !lastMoveRight) {
            speedX = -speed;
        }
        if(moveRight && lastMoveRight){
            speedX = speed;
        }
        if(moveUp && !lastMoveDown){
            speedY = -speed;
        }
        if(moveDown && lastMoveDown){
            speedY = speed;
        }
        if(!moveLeft && !moveRight){
            speedX = 0;
        }
        if(!moveUp && !moveDown){
            speedY = 0;
        }
        x += speedX;
        y += speedY;
    }

    public void pressMoveUp(boolean isPressed){
        moveUp = isPressed;
        lastMoveDown = !isPressed;
    }

    public void pressMoveDown(boolean isPressed){
        moveDown = isPressed;
        lastMoveDown = isPressed;
    }

    public void pressMoveLeft(boolean isPressed){
        moveLeft = isPressed;
        lastMoveRight = !isPressed;
    }

    public void pressMoveRight(boolean isPressed){
        moveRight = isPressed;
        lastMoveRight = isPressed;
    }

    public void pressShift(boolean isPressed){
        if(isPressed){
            speed = 1;
        }
        else{
            speed = 3;
        }
    }

    //发射子弹，子弹数量由power决定
    public Bullet[] shoot(){
        if(isShoot) {
            //System.out.println("shooting");

            int stepX = this.width / 4;
            int stepY = 20;
            Bullet[] bullets;
            if(this.power > 100){
                this.power = 100;
            }
            if (this.power < 10) {
                bullets = new Bullet[]{new Bullet(this.x + 2 * stepX - 7, this.y - stepY, 0)};
            } else if (this.power < 30) {
                bullets = new Bullet[]{
                        new Bullet(this.x + stepX - 7, this.y - stepY, 0),
                        new Bullet(this.x + 3 * stepX - 7, this.y - stepY, 0)};
            } else if (this.power < 60){
                bullets = new Bullet[]{
                        new Bullet(this.x + stepX - 7, this.y - stepY, -1),
                        new Bullet(this.x + 2 * stepX - 7, this.y - stepY, 0),
                        new Bullet(this.x + 3 * stepX - 7, this.y - stepY, 1)};
            }else{
                bullets = new Bullet[]{
                        new Bullet(this.x + stepX - 7, this.y - stepY, -2),
                        new Bullet(this.x + 2 * stepX - 7, this.y - stepY, -1),
                        new Bullet(this.x + 3 * stepX - 7, this.y - stepY, 1),
                        new Bullet(this.x + 4 * stepX - 7, this.y - stepY, 2)};

            }
            return bullets;
        }
        return null;
    }

    public Boom[] boom(){
        int stepX = this.width / 4;
        int stepY = 20;
        return new Boom[]{
                new Boom(this.x + 2*stepX - 30, this.y - stepY, 0),};
    }

    //玩家死亡
    public void death(){
        x = 200;
        y = 500;
        health--;
        spell = 3;
        power /= 2;
    }

    //越界检查
    public boolean outOfBounds() {
        if(x < 3){
            x = 3;
        }
        else if(x >= 395){
            x = 395;
        }
        if(y < 0){
            y = 0;
        }
        else if(y >= 540){
            y = 540;
        }
        return false;
    }

    //检测玩家是否被击中
    public boolean hit(FlyingObject other) {
        int x1 = other.x - this.width * 2 / 3 + 3;
        int x2 = other.x + this.width / 3 + other.width - 3;
        int y1 = other.y - this.height * 2 / 3 + 5;
        int y2 = other.y + this.height / 3 + other.height - 5;
        int playerX = this.x + this.width / 2;
        int playerY = this.y + this.height / 2;
        return playerX > x1 && playerX < x2 && playerY > y1 && playerY < y2;
    }

    public void setIsShoot(boolean isShoot){
        this.isShoot = isShoot;
    }

}
