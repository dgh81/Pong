/**
 * Evt. implementér retningsændring:
 * Sæt retningsændring på bolden når den rammer forskellige dele af en paddle:
 * Se https://www.youtube.com/watch?v=IIrC5Qcb2G4&ab_channel=TheCodingTrain (ca 57:00 inde)
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * 0.5555);
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;


    GamePanel() {
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener (new AL());
        this.setPreferredSize(SCREEN_SIZE);
        gameThread = new Thread(this);
        // gameThread kalder automatisk på run():
        gameThread.start();

    }

    public void newBall() {

        random = new Random();
        ball = new Ball((GAME_WIDTH/2-BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
    }

    public void newPaddles() {

        paddle1 = new Paddle(0, (GAME_HEIGHT/2) - (PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT,1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT/2) - (PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT,2);

    }

    public void paint(Graphics g) {

        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }

    public void draw(Graphics g) {

        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }

    public void move() {

        paddle1.move();
        paddle2.move();
        ball.move();
    }

    public void checkCollision() {


        // Bounce ball off of top and bottom window edges:
        if (ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if (ball.y >= (GAME_HEIGHT-BALL_DIAMETER)) {
            ball.setYDirection(-ball.yVelocity);
        }

        // Bounce ball off of paddle:
        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            // OPTIONAL:
            // ball.xVelocity++;
            if (ball.yVelocity > 0) {
                // OPTIONAL:
                // ball.yVelocity++;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);

        }

        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            // OPTIONAL:
            // ball.xVelocity++;
            if (ball.yVelocity > 0) {
                // OPTIONAL:
                // ball.yVelocity++;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);

        }

        //Bounce paddles on window edges:
        if (paddle1.y <= 0) paddle1.y = 0;
        if (paddle1.y >= GAME_HEIGHT-PADDLE_HEIGHT) paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
        if (paddle2.y <= 0) paddle2.y = 0;
        if (paddle2.y >= GAME_HEIGHT-PADDLE_HEIGHT) paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;

        // Give player 1 point and creates new panel and ball
        if (ball.x <= 0) {
            score.player2++;
            newPaddles();
            newBall();
            //System.out.println("Score player2: " + score.player2);
        }
        if (ball.x > GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
            //System.out.println("Score player1: " + score.player1);
        }
    }

    // run() køres automatisk af Thread:
    public void run() {
        // GAME LOOP:
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        // 1.000.000.000 [nanosekunder pr sekund]
        // framerate er amountOfTicks [ticks pr sekund]
        // ns = [nanosekunder pr sekund / ticks pr sekund] = [nanosekunder pr tick]
        double delta = 0;
        while(true) {
            long now = System.nanoTime();
            // når "forskellen i tid" / "antal nano pr sekund" overstiger 1 (se if statement herunder),
            // har vi opbrugt 1 tick, og vil derfor move(), checkCollision() og repaint():
            delta += (now-lastTime)/ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                // delta = 1-1 = 0 -dvs nulstil delta (vores tick counter):
                delta--;
                //System.out.println("Game loop is running " + System.nanoTime());
            }
        }

    }

    public class AL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {

            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}
