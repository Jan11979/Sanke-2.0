import javax.swing.*;
import java.awt.*;
import java.awt.EventQueue;
import javax.swing.JFrame;


public class Snake extends JFrame {

    public Snake() {

        initUI();
    }

    private void initUI() {

        add(new GUI());

        setResizable(false);
        pack();

        setTitle("Snake 2.0");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

