import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Timer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Mixer.Info;
 
public class Test implements Runnable {
	private Thread th;

	public static void main(String[] args) {
		new Test();
	}

	private Test() {
		th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		Clip clip = createClip(new File("archersound-tokyo-rain-serenade-archer-sounds-321180.wav"));
		//ここで再生メソッドの呼び出し
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Clip createClip(File path) {
		//指定されたURLのオーディオ入力ストリームを取得
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){ //  new AudioFormat(44100, 16, 2, true, false) , AudioSystem.getAudioInputStream(path)
			
			//ファイルの形式取得
			AudioFormat af = ais.getFormat();
			
			//単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			
			//指定された Line.Info オブジェクトの記述に一致するラインを取得
			Clip c = (Clip)AudioSystem.getLine(dataLine);
			
			//再生準備完了
			c.open(ais);
			
			return c;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
	}

}