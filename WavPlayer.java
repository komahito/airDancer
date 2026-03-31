import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavPlayer {

	private byte buf[] = new byte[1024];
	private int len;

	public void play(InputStream wavfile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		AudioInputStream wav = AudioSystem.getAudioInputStream(wavfile);
		AudioFormat format = wav.getFormat();
		// prepare audio output
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		// prepare audio output
		line.open(format, 1024);
		line.start();
		// play audio
		while ((len = wav.read(buf)) != -1)
			line.write(buf, 0, len);
		// shut down audio
		line.drain();
		line.stop();
		line.close();
	}

}