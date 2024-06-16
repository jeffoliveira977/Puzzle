import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable, MouseListener, MouseMotionListener {
    private BufferStrategy bs;
    private Thread thread;
    private static final long TIME_FRAME = 1000 / 60;
    private static JFrame frame = null;
    private long lastFrameTime = System.currentTimeMillis();
    static boolean isRunning = true;

    public Game() {
        new GemsManager();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void run() {

        createBufferStrategy(3);
        bs = getBufferStrategy();

        requestFocus();

        lastFrameTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        while (isRunning) {
            do {
                do {
                    long elapsedTime = currentTime - lastFrameTime;

                    Graphics g = null;
                    try {
                        g = bs.getDrawGraphics();
                        render(g);
                    } finally {
                        g.dispose();
                    }

                    while (elapsedTime < TIME_FRAME) {
                        try {
                            Thread.sleep(TIME_FRAME - elapsedTime);
                        } catch (InterruptedException e) {
                        }

                        currentTime = System.currentTimeMillis();
                        elapsedTime = currentTime - lastFrameTime;

                        update(elapsedTime);
                        lastFrameTime = System.currentTimeMillis();
                    }
                } while (bs.contentsRestored());
                bs.show();
            } while (bs.contentsLost());
        }
        bs.dispose();
    }

    public void update(long dt) {
        GemsManager.update(dt);
    }

    public void render(Graphics g) {
        GemsManager.render(g);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {GemsManager.mousePressed(e);}

    public void mouseReleased(MouseEvent e) {
        GemsManager.mouseReleased(e);
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {GemsManager.mouseMoved(e);}

    public void mouseDragged(MouseEvent e) {}

    public synchronized void start() {
        thread = new Thread(this, "Display");
        thread.start();
    }

    public static void main(String[] args) {
        Game game = new Game();
        frame = new JFrame("Puzzle Quest");
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1038, 788);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.start();
    }
}