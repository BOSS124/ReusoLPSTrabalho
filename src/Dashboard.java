import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setSize(env.getMaximumWindowBounds().getSize());
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        features = new ArrayList<Feature>();
        carregarInstFeatures();

        board = new Board(features);
        add(board);
        pack();

        return this;
    }

    private class Board extends JPanel implements MouseListener, MouseMotionListener {
        private static final long serialVersionUID = 2L;

        private int centerX;
        private boolean mousePressed;
        private int clickX;
        private BufferedImage img;

        private ArrayList<Feature> features;

        public Board(ArrayList<Feature> features) {
            super();
            this.features = features;
            mousePressed = false;
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void assembleBoard() {
            img = new BufferedImage(features.size() * getHeight(), getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = (Graphics2D) img.getGraphics();

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

            int x = 0;
            for(Feature f : features) {
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

                Font fonteNome = new Font("Times New Roman", Font.BOLD, 30);
                Font fonteDesc = new Font("Times New Roman", Font.PLAIN, 20);
                
                g2d.drawRect(x, 0, getHeight(), getHeight());
                g2d.setFont(fonteNome);
                FontMetrics fm = g2d.getFontMetrics();
                String nome = f.toString();
                int nomeWidth = fm.stringWidth(nome);
                g2d.drawString(nome, x + (getHeight() / 2) - (nomeWidth / 2), 100);

                g2d.setFont(fonteDesc);
                fm = g2d.getFontMetrics();
                String desc = f.getDescricao();
                String[] lines = desc.split("\n");
                int y = 200;
                for(int i = 0; i < lines.length; i++) {
                    g2d.drawString(lines[i], x + 20, y);
                    y += fm.getHeight();
                }

                x += getHeight();
            }

            centerX = 0;
        }



        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(getHeight() > 100) {
                if(img == null) assembleBoard();

                Graphics2D g2d = (Graphics2D) g;

                BufferedImage sub = img.getSubimage(centerX, 0, getWidth(), getHeight());
                g2d.drawImage(sub, null, 0, 0);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            clickX = e.getX();
            mousePressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int dx = clickX - e.getX();
            if(((centerX + dx) > 0) && ((centerX + dx + getWidth()) < img.getWidth())) {
                centerX += dx;
                clickX = e.getX();
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    

    public static void main(String[] args) {
        Dashboard dashboard = new Dashboard().init();
        dashboard.setVisible(true);
    }
}