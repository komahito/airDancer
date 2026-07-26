import java.io.File;
 
import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
 
public class MusicPlayer extends Application{
	MediaPlayer mp = null;
	Media m = null;

 
	public static void main(String[] args) {
		launch(args);
	}
 
	@Override
	public void start(Stage primaryStage) {
		primaryStage.show();
 
		//ファイルを読み込み
		m = new Media(new File("archersound-tokyo-rain-serenade-archer-sounds-321180.mp3").toURI().toString());
 
		//音声の再生等の操作を実行できるオブジェクト
		mp = new MediaPlayer(m);
		mp.setCycleCount(MediaPlayer.INDEFINITE);
 
		//再生開始
		mp.play();
	}
 
}