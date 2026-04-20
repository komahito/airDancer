import javax.swing.JFrame;
import javax.swing.JPanel;

public class RenderingPanel extends JPanel implements Runnable {
    int width = 400, height = 300;

    @Override
    public void run() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(this);
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(width, height);
        jFrame.setVisible(true);

        while (true) {
            update(getGraphics());
        }
    }

}
