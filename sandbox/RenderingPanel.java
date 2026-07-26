import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RenderingPanel extends JPanel implements Runnable {
    int width = 400, height = 300;

    public static void main (String[] args) {
        new RenderingPanel();
    }

    public RenderingPanel () {
        new Thread(this).start();
    }

    @Override
    public void run() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(this);
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(width, height);
        jFrame.setVisible(true);

        while (true) {
            repaint(); // Invoke update() / update() invoke paint()
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        // Dynamically calculate size information 
        Dimension size = getSize();         // diameter    
            int d = Math.min(size.width, size.height);    
            int x = (size.width - d)/2;    
            int y = (size.height - d)/2;       
            // draw circle (color already set to foreground) 
        g2.fillOval(x, y, d, d);     
        g2.setColor(Color.black);   
        g2.drawOval(x, y, d, d); 
    }

}
