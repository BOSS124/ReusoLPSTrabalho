import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import features.*;

public class Dashboard extends JFrame {
    Board board;
    ArrayList<Feature> features;

    private static final long serialVersionUID = 1L;

    public Dashboard() {
        super("LPS FastFood Dashboard");
    }

    private void carregarInstFeatures() {
        try {
            FileInputStream fis = new FileInputStream("lps.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj;
            while((obj = ois.readObject()) != null) {
                Class <?> c = (Class) obj;
                Constructor<?> constructor = c.getConstructor();
                features.add((Feature) constructor.newInstance());
            }
            ois.close();
        }
        catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "O arquivo lps.bin não existe", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception ex) {

        }
    }

    public Dashboard init() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        setMaximizedBounds(env.getMaximumWindowBounds());
        setResizable(false);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        features = new ArrayList<Feature>();
        carregarInstFeatures();

        board = new Board(getContentPane().getSize(), features);
        add(board);
        pack();

        return this;
    }

    private class Board extends JPanel {
        private static final long serialVersionUID = 2L;

        private int centerX = 250, centerY = 250;
        private BufferedImage img;

        public Board(Dimension dim, ArrayList<Feature> features) {
            super();
            setLocationRelativeTo(null);
            System.out.println(getHeight());
            System.out.println(dim.width + "/" + dim.height);
            img = new BufferedImage(dim.height, features.size() * dim.height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = (Graphics2D) img.getGraphics();

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

            int x = 0;
            for(Feature f : features) {
                String nome = f.toString();
                String desc = f.getDescricao();

                switch(f.getTipo()) {
                    case Feature.TIPO_COMUM:
                    g2d.setColor(Color.GREEN);
                    break;

                    case Feature.TIPO_VARIAVEL:
                    g2d.setColor(Color.YELLOW);
                    break;

                    default:
                    g2d.setColor(Color.WHITE);
                }

                g2d.drawRect(x, 0, getHeight(), getHeight());
                
                g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                FontMetrics fm = g2d.getFontMetrics();
                int strWidth = fm.stringWidth(nome);
                g2d.drawString(nome, x + (getHeight() / 2) - strWidth, 100);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.drawImage(img.getSubimage(centerX - (int) (getHeight() / 2), centerY - (int) (getHeight() / 2), getHeight(), getHeight()), null, 0, 0);
        }
    }

    

    public static void main(String[] args) {
        Dashboard dashboard = new Dashboard().init();
        dashboard.setVisible(true);
    }
}