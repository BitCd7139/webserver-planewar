package ui;

import object.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Game extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 630;
    public static BufferedImage icon;
    public static BufferedImage playerImg;
    public static BufferedImage bulletImg;
    public static BufferedImage boomImg;
    public static BufferedImage enemyRed;
    public static BufferedImage enemyBlue;
    public static BufferedImage playBg;
    public static BufferedImage stateBg;
    public static BufferedImage startBg;
    public static BufferedImage pauseBg;
    public static BufferedImage gameoverBg;
    public static BufferedImage healthBg;
    public static BufferedImage spellBg;
    public static BufferedImage enemyBlack;
    public static BufferedImage enemyWhite;
    public int interval = 16;

    private Enemy[] objects = new Enemy[0];
    private FlyingObject[] bullets = new FlyingObject[0];
    private Player player = new Player();

    private int state;
    private int score = 0;
    private static int spawnIndex = 0;
    private static int shootIndex = 0;

    static {
        try {
            // 建议：使用 class.getResource()，路径前加 "/" 代表从 JAR 包根目录开始找
            icon = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/ui/icon.png")));
            stateBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/ui/state.png")));
            playBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/ui/playBg.png")));
            playerImg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/player.png")));
            startBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/ui/start.png")));
            pauseBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/ui/pause.png")));
            gameoverBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/ui/gameover.png")));
            bulletImg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/bullet.png")));
            boomImg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/boom.png")));
            enemyRed = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/enemyRed.png")));
            enemyBlue = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/enemyBlue.png")));
            enemyBlack = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/enemyBlack.png")));
            enemyWhite = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/enemyWhite.png")));
            healthBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/health.png")));
            spellBg = ImageIO.read(Objects.requireNonNull(Game.class.getResource("/object/spell.png")));
        } catch (IOException e) {
            e.printStackTrace();
            // 打印具体的错误路径方便调试
            System.err.println("资源加载失败，请检查路径是否正确！");
        } catch (IllegalArgumentException e) {
            System.err.println("找不到指定的资源文件！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        Game game = new Game();
        frame.add(game);
        frame.setSize(WIDTH, HEIGHT);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(3);
        frame.setIconImage(icon);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setVisible(true);

        game.action();
    }

    public void action(){

        KeyAdapter kl = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                //state == 1 游戏中
                if(state == 1) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_Z) {
                        player.setIsShoot(true);
                    }
                    if (code == KeyEvent.VK_X) {
                        setBoom();
                    }
                    if (code == KeyEvent.VK_UP) {
                        player.pressMoveUp(true);
                    }
                    if (code == KeyEvent.VK_DOWN) {
                        player.pressMoveDown(true);
                    }
                    if (code == KeyEvent.VK_LEFT) {
                        player.pressMoveLeft(true);
                    }
                    if (code == KeyEvent.VK_RIGHT) {
                        player.pressMoveRight(true);
                    }
                    if (code == KeyEvent.VK_SHIFT) {
                        player.pressShift(true);
                    }
                    if (code == KeyEvent.VK_ESCAPE) {
                        state = 2;
                    }
                }

                //state == 3 结束游戏
                if(state == 3){
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_ESCAPE) {
                        objects = new Enemy[0];
                        bullets = new FlyingObject[0];
                        player = new Player();
                        score = 0;
                        state = 0;
                    }
                }

                //state == 0 准备界面
                if(state == 0){
                    int code = e.getKeyCode();
                    if(code == KeyEvent.VK_Z){
                        state = 1;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e){
                int code = e.getKeyCode();
                if(code == KeyEvent.VK_Z){
                    player.setIsShoot(false);
                    shootIndex = 0;
                }
                if(code == KeyEvent.VK_UP){
                    player.pressMoveUp(false);
                }
                if(code == KeyEvent.VK_DOWN){
                    player.pressMoveDown(false);
                }
                if(code == KeyEvent.VK_LEFT){
                    player.pressMoveLeft(false);
                }
                if(code == KeyEvent.VK_RIGHT){
                    player.pressMoveRight(false);
                }
                if(code == KeyEvent.VK_SHIFT){
                    player.pressShift(false);
                }
            }
        };

        addKeyListener(kl);
        requestFocus();

        //游戏循环
        javax.swing.Timer swingTimer = new javax.swing.Timer(interval, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (state == 1) {
                    spawnAction();
                    moveAction();
                    shootAction();
                    hitAction();
                    outOfBoundsAction();
                    checkGameOverAction();
                }
                repaint();
            }
        });
        swingTimer.start();
    }

    //敌人生成
    public void spawnAction(){
        spawnIndex++;
        if(isSpawnEnemy(spawnIndex)){
            Enemy obj = nextOne();
            objects = Arrays.copyOf(objects, objects.length + 1);
            objects[objects.length - 1] = obj;
        }
    }

    //移动
    public void moveAction(){
        for (FlyingObject object : objects) {
            object.move();
        }
        for (FlyingObject bullet : bullets) {
            bullet.move();
        }

        player.move();
    }

    //子弹是否击中
    public void hitAction(){
        int index = 0;
        FlyingObject[] b = new FlyingObject[bullets.length];
        for (FlyingObject bullet : bullets) {
            if (checkHit(bullet)) {
                b[index++] = bullet;
            }
        }
        bullets = Arrays.copyOf(b, index);
    }

    //射击
    public void shootAction(){
        shootIndex++;
        if(shootIndex % 10 == 0) {
            FlyingObject[] b = player.shoot();
            if(b != null) {
                bullets = Arrays.copyOf(bullets, bullets.length + b.length);
                System.arraycopy(b, 0, bullets, bullets.length - b.length, b.length);
            }

            //if(player.getIsShoot()){System.out.println("bullets:" + playerBullets.length);}
        }
    }

    //使用技能
    public void setBoom(){
        if(player.getSpell() > 0){
            FlyingObject[] b = player.boom();
            bullets = Arrays.copyOf(bullets, bullets.length + b.length);
            System.arraycopy(b, 0, bullets, bullets.length - b.length, b.length);
            player.setSpell(player.getSpell() - 1);
        }
    }

    //是否移除物体
    public void outOfBoundsAction(){
        int index = 0;
        Enemy[] flyingLives = new Enemy[objects.length];

        for (Enemy f : objects) {
            if (!f.outOfBounds()){
                if(f.getHealth() >= 0) {
                    flyingLives[index++] = f;
                }
                else{
                    score += f.getScore();
                    switch (f.getType()) {
                        case 1:
                            player.setPower(player.getPower() + 1);
                            break;
                        case 2:
                            score += 10;
                            break;
                        case 3:
                            player.setSpell(player.getSpell() + 1);
                            break;
                        case 4:
                            player.setHealth(player.getHealth() + 1);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        objects = Arrays.copyOf(flyingLives, index);

        index = 0;
        FlyingObject[] bulletLives = new FlyingObject[bullets.length];

        for (FlyingObject b : bullets) {
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index);

        player.outOfBounds();
    }

    //是否生成敌人
    public boolean isSpawnEnemy(int x){
        return spawnIndex % (30 - (int)Math.log(score + 1)) == 0;
    }

    //检测是否被击中
    public boolean checkHit(FlyingObject bullet){
        if(bullet.getType() == -1) {
            int index = -1;
            FlyingObject temp;
            for (int i = 0; i < objects.length; i++) {
                temp = objects[i];
                if (temp.shootBy(bullet)) {
                    index = i;
                    temp.setHealth(temp.getHealth() - 1);
                    break;
                }
            }
            return index == -1;
        }
        else{
            FlyingObject temp;
            for (int i = 0; i < objects.length; i++) {
                temp = objects[i];
                if (temp.shootBy(bullet) && temp.getType()!=4) {
                    temp.setHealth(-1);
                }
            }
            return true;
        }
    }

    //检测游戏结束
    public void checkGameOverAction() {
        if(this.isGameOver()) {
            state = 3;
        }

    }

    //游戏是否结束
    public boolean isGameOver() {
        for(int i = 0; i < objects.length; i++) {
            Enemy obj = objects[i];
            if(player.hit(obj)) {
                player.death();
                objects = new Enemy[0];
            }

        }

        return player.getHealth() <= 0;
    }

    //生成下一个敌人
    public Enemy nextOne() {
        Random random = new Random();
        int type = random.nextInt(200);
        int speed = random.nextInt(3);
        if(type == 1){
            return new EnemyWhite((int) Math.pow(score,0.2));
        }
        else if(type < 5){
            return new EnemyBlack((int) (Math.pow(score,0.1)));
        }
        else if(type < 103){
            return new EnemyRed(speed, (int) (Math.log((double) score /2)));
        }
        else{
            return new EnemyBlue(speed, (int) (Math.log((double) score /2)));
        }
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(playBg, 0, 0, 480, 800, null);
        g.drawImage(stateBg, 0, 0, null);

        paintPlayer(g);
        paintPlayerBullet(g);
        paintFlyingObjects(g);
        paintScore(g);
        paintState(g);

        //System.out.println("Painting: " + state);
    }

    public void paintPlayer(Graphics g){
        g.drawImage(player.getImage(), player.getX(), player.getY(), (int) (player.getHeight()*1.2), (int) (player.getWidth()*1.2),null);
    }

    public void paintPlayerBullet(Graphics g){
        for (FlyingObject b : bullets) {
            g.drawImage(b.getImage(), b.getX() - b.getWidth(), b.getY(), null);
        }
    }

    public void paintFlyingObjects(Graphics g){
        for (FlyingObject obj : objects) {
            g.drawImage(obj.getImage(), obj.getX(), obj.getY(), null);
        }
    }

    public void paintState(Graphics g){
        switch(state) {
            case 0:
                g.drawImage(startBg, 0, 0, null);
                break;
            case 1:
            default:
                break;
            case 2:
                g.drawImage(pauseBg, 0, 0, null);
                break;
            case 3:
                g.drawImage(gameoverBg, 0, 0, null);
                //System.out.println("drawing");
                g.setColor(Color.BLACK);
                Font scoreFont = new Font("Sansation.ttf", Font.PLAIN, 45);
                g.setFont(scoreFont);
                g.drawString(String.valueOf(score), 355, 315);
                break;
        }
        //g.drawImage(playerImg, player.getX(), player.getY(), null);
    }

    public void paintScore(Graphics g){
        if(player.getHealth() > 9){
            player.setHealth(9);
        }
        for(int i = 0; i < player.getHealth(); i ++){
            g.drawImage(healthBg, 550 + i*25, 135, null);
        }
        if(player.getSpell() > 9){
            player.setSpell(9);
        }
        for(int i = 0; i < player.getSpell(); i ++){
            g.drawImage(spellBg, 550 + i*25, 180, null);
        }
        g.setColor(Color.BLACK);
        Font scoreFont = new Font("Sansation.ttf", Font.PLAIN, 40);
        g.setFont(scoreFont);
        g.drawString(String.valueOf(score), 520, 105);
        Font powerFont = new Font("Sansation.ttf", Font.PLAIN, 32);
        g.setFont(powerFont);
        g.drawString(String.valueOf(player.getPower()), 565, 250);
    }

}
