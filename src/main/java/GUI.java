import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.text.NumberFormat;

public class GUI extends JPanel implements ActionListener {

    private class Item {
        public int type;
        public int x;
        public int y;

        public boolean samePos(Item other) {
            return ((x == other.x) && (y == other.y));
        }

        public void copy(Item other) {
            x = other.x;
            y = other.y;
            type = other.type;
        }
    }

    private final int B_WIDTH = 1000;
    private final int B_HEIGHT = 500;
    private final int MAX_TAIL = 100;
    private final int MAX_EGGS = 50;
    private final int MIN_TAIL = 3;
    private final int ITEM_SIZE = 25;
    private final int MIN_DELAY = 10;
    private final int MAX_DELAY = 290;
    private final int EGG_COUNTER = 10;

    private final int EGG_EMPTY = 0;
    private final int EGG_TAIL_ADD = 1;
    private final int EGG_TAIL_SUB = 2;
    private final int EGG_SPEED_ADD = 3;
    private final int EGG_SPEED_SUB = 4;
    private final int EGG_LAST_TYPE = 5;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private Timer timer;
    private Image head;
    private Image tail;
    private Image crashHead;
    private Image eggTailAdd;
    private Image eggTailSub;
    private Image eggSpeedAdd;
    private Image eggSpeedSub;


    private boolean crash = false;

    private int delay = (MIN_DELAY + MAX_DELAY) / 2;

    private int counterEgg = EGG_COUNTER;
    private int sizeTail;
    Item[] listSanke = new Item[MAX_TAIL];
    Item[] listEggs = new Item[MAX_EGGS];

    /** ToDO
    *   Score fehlt noch
    */
    private int score = 0;


    public GUI() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();

        for (int i = 0; i < MAX_TAIL; i++)
            listSanke[i] = new Item();
        for (int i = 0; i < MAX_EGGS; i++) {
            listEggs[i] = new Item();
            listEggs[i].type = EGG_EMPTY;
        }


        init();
    }

    private void loadImages() {

        ImageIcon ii1 = new ImageIcon("src/main/resources/dot_GreyX.png");
        tail = ii1.getImage();

        ImageIcon ii3 = new ImageIcon("src/main/resources/dot_Face.png");
        head = ii3.getImage();

        ImageIcon ii4 = new ImageIcon("src/main/resources/dot_FaceChash.png");
        crashHead = ii4.getImage();

        ImageIcon iiegg = new ImageIcon("src/main/resources/dot_BlueEgg.png");
        eggTailAdd = iiegg.getImage();
        ImageIcon iiegg2 = new ImageIcon("src/main/resources/dot_PurpleEgg.png");
        eggTailSub = iiegg2.getImage();
        ImageIcon iiegg3 = new ImageIcon("src/main/resources/dot_YellowEgg.png");
        eggSpeedAdd = iiegg3.getImage();
        ImageIcon iiegg4 = new ImageIcon("src/main/resources/dot_GreenEgg.png");
        eggSpeedSub = iiegg4.getImage();
    }

    private void init() {
        crash = false;
        sizeTail = MIN_TAIL;

        for (int i = 0; i < sizeTail; i++) {
            listSanke[i].x = 50 - i * ITEM_SIZE;
            listSanke[i].y = 50;
            listSanke[i].type = 0;
        }

//        locateApple();

        timer = new Timer(delay, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        checkCollision();
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        for (int i = 0; i < MAX_EGGS; i++) {
            if (listEggs[i].type != EGG_EMPTY) {
                switch (listEggs[i].type) {
                    case EGG_TAIL_ADD:
                        g.drawImage(eggTailAdd, listEggs[i].x, listEggs[i].y, this);
                        break;
                    case EGG_TAIL_SUB:
                        g.drawImage(eggTailSub, listEggs[i].x, listEggs[i].y, this);
                        break;
                    case EGG_SPEED_ADD:
                        g.drawImage(eggSpeedAdd, listEggs[i].x, listEggs[i].y, this);
                        break;
                    case EGG_SPEED_SUB:
                        g.drawImage(eggSpeedSub, listEggs[i].x, listEggs[i].y, this);
                        break;
                }
            }
        }

        for (int i = 0; i < sizeTail; i++) {
            if (i == 0) {
                g.drawImage(head, listSanke[i].x, listSanke[i].y, this);
            } else {
                g.drawImage(tail, listSanke[i].x, listSanke[i].y, this);
            }
        }


        Toolkit.getDefaultToolkit().sync();


        drawPoints(g);

        if (crash) {
            g.drawImage(crashHead, listSanke[1].x, listSanke[1].y, this);
            gameOver(g);
        }
    }

    private void drawPoints(Graphics g) {
        String msg = "Points: " + NumberFormat.getIntegerInstance().format(score) + " Speed: " + ( 300 - delay );
        Font font = new Font("Helvetica", Font.BOLD, 12);
        FontMetrics metr = getFontMetrics(font);
        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(msg, 12, 12 );
    }


    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 42);
        FontMetrics metr = getFontMetrics(font);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }


    private void move() {

        if (!crash) {
            for (int i = sizeTail; i > 0; i--) {
                listSanke[i].copy(listSanke[i - 1]);
            }

            if (leftDirection) {
                listSanke[0].x -= ITEM_SIZE;
            }
            if (rightDirection) {
                listSanke[0].x += ITEM_SIZE;
            }
            if (upDirection) {
                listSanke[0].y -= ITEM_SIZE;
            }
            if (downDirection) {
                listSanke[0].y += ITEM_SIZE;
            }
        }
    }

    private void checkCollision() {

        for (int i = sizeTail; i > 0; i--) {

            if ((i > 4) && listSanke[0].samePos(listSanke[i]))
                crash = true;
        }

        if ((listSanke[0].x >= B_WIDTH) || (listSanke[0].x < 0))
            crash = true;
        if ((listSanke[0].y >= B_HEIGHT) || (listSanke[0].y < 0))
            crash = true;


        if (crash) {
            timer.stop();
        }
    }

    private void checkEgg() {
        for (int i = 0; i < MAX_EGGS; i++) {
            if (listEggs[i].type != EGG_EMPTY)
                if (listSanke[0].samePos(listEggs[i])) {
                    switch (listEggs[i].type) {
                        case EGG_TAIL_ADD:
                            if (sizeTail < MAX_TAIL)
                                sizeTail++;
                            break;
                        case EGG_TAIL_SUB:
                            if (sizeTail > MIN_TAIL)
                                sizeTail--;
                            break;
                        case EGG_SPEED_ADD:
                            if (delay < MAX_DELAY)
                                delay += 5;
                            timer.setInitialDelay(delay);
                            break;
                        case EGG_SPEED_SUB:
                            if (delay > MIN_DELAY)
                                delay -= 5;
                            timer.setInitialDelay(delay);
                            break;
                    }
                    listEggs[i].type = EGG_EMPTY;
                }
        }
    }

    void dropEgg() {
        Random r = new Random();
        int i = r.nextInt(0, MAX_EGGS);

        int rX = r.nextInt(2, (B_WIDTH / (ITEM_SIZE + 2)));
        int rY = r.nextInt(2, (B_HEIGHT / (ITEM_SIZE + 2)));
        int rt = r.nextInt(EGG_TAIL_ADD, EGG_LAST_TYPE);

        listEggs[i].x = rX * ITEM_SIZE;
        listEggs[i].y = rY * ITEM_SIZE;
        listEggs[i].type = rt;
        return;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (crash == false) {
            counterEgg--;
            if (counterEgg == 0) {
                counterEgg = EGG_COUNTER;
                dropEgg();
            }

            score += sizeTail - 2;

            checkEgg();

            move();
        }


        repaint();
    }


    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
