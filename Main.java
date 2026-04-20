import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String args[]){
        Thread runnableThread = new Thread(new RenderingPanel());
        // runnableThread.setDaemon(true);
        runnableThread.start();
    }

}
