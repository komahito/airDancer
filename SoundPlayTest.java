import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayTest {

	public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {

		WavPlayer wavPlayer = new WavPlayer();
		wavPlayer.play(new BufferedInputStream(new FileInputStream(new File("archersound-tokyo-rain-serenade-archer-sounds-321180.wav"))));

	}

}



