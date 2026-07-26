

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
 
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
 
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
/**
 * 音声(wav)データの波形を見るプログラム
 * ただし、Wav(PCM・リトルエディアン)形式で保存された
 * ファイルのチャンネル１のみ出力
 * 
 * @author karura
 *
 */
public class WaveSpectrum extends Application
{
    // 定数
    private final String    fileName    = "/home/hitoha/AirDancer/airDancer/airdancer/src/main/resources/assets/sounds/archersound-tokyo-rain-serenade-archer-sounds-321180.wav";        // チャートに表示する音声ファイルへのパス
    private final int binOutNum = 10; // 離散フーリエ変換後のスペクトルを表示する際のビン数
    private final double maxSec = 1.5;// 60*5; // 取得する音声の最大時間(s)
    private final int dftSampleSize = 1024; // 離散フーリエ変換を行う際のサンプル数
    private final int fps = 30;

    // 取得する音声情報用の変数
    private AudioFormat     format                  = null;
    private double[]        valuesActual            = null;
    private double[]        valuesImaginal          = null;
    private double[]        spectrumActual          = null;
    private double[]        spectrumImaginal        = null;
    private double[]       spectrumAmplitude       = null;
    private int binInNum = 0; // 取得した音声の標本数
    private int binInPerSec = 0; // 1秒間に取得した標本数

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
         
        // フォント色がおかしくなることへの対処
        System.setProperty( "prism.lcdtext" , "false" );
         
        // // シーングラフの作成
        // HBox        root    = new HBox();
        // VBox        box1    = new VBox();
        // VBox        box2    = new VBox();
        // root.getChildren().addAll( box1 , box2 );
        // // シーンの作成
        // Scene       scene   = new Scene( root , 800 , 400 );

         
        // 音声データを読込
        System.out.println( "loading wav data..." );
        initialize();
        // 元音声波形をチャート表示
        // box1.getChildren().add( createLineChart( "audio waveform" , valuesActual ) );            // 折れ線グラフの追加
          
        double lastT = 0;
        int head = 0;
        int tail = 0;
        for (double t = 0; t < 0.8; t += (1.0 / (double) fps) ){ // (1 / fps) ) { 
            // 離散フーリエ変換後の波形をチャート表示
            System.out.println( "caliculating DFT..." );
            spectrumActual       = new double[ binOutNum ];
            spectrumImaginal     = new double[ binOutNum ];

            tail = (int) ((double) head + (double) binInPerSec * (t - lastT)); //head + binInPerSec * (t - lastT);
            DFT( valuesActual, head, tail, spectrumActual , spectrumImaginal , true );
            head = tail;

            // 各周波数ごとの振幅
            spectrumAmplitude = new double[binOutNum];
            for (int i = 0; i < binOutNum; i++) {
                spectrumAmplitude[i] = Math.hypot(spectrumActual[i], spectrumImaginal[i]);
            }

            // print spectrumAmplitude array
            for (int i = 0; i < binOutNum; i++) {
                System.out.print(spectrumAmplitude[i] + " ");
            }
            System.out.println();

            lastT = t;
        }

        //  // 離散フーリエ変換後のスペクトルをチャート表示
        // box2.getChildren().add( createLineChart( "spectral(real part)" , spectrumActual ) );            // 折れ線グラフの追加
        // box2.getChildren().add( createLineChart( "spectral(imaginary part)" , spectrumImaginal ) );          // 折れ線グラフの追加
        // box1.getChildren().add( createLineChart( "spectral(amplitude)" , spectrumAmplitude ) );          // 折れ線グラフの追加
    
        // // ウィンドウ表示
        // primaryStage.setScene( scene );
        // primaryStage.show();
        
        
         
    }
     
    /**
     * 音声ファイルを読み込み、メタ情報とサンプリング・データを取得
     * @throws Exception
     */
    protected void initialize() throws Exception
    {
        // 音声ストリームを取得
        File                file    = new File( fileName );
        AudioInputStream    is      = AudioSystem.getAudioInputStream( file );
         
        // メタ情報の取得
        format = is.getFormat(); 
        System.out.println( format.toString() );
         
        // 取得する標本数を計算
        // 1秒間で取得した標本数がサンプルレートであることから計算
        binInPerSec = (int)( format.getSampleRate() );
        int maxBinIn = (int)( binInPerSec * maxSec );
         
        // 音声データの取得
        valuesActual    = new double[ maxBinIn ];
        valuesImaginal  = new double[ maxBinIn ];
        for( int i=0 ; i<maxBinIn ; i++ )
        {
            // 1標本分の値を取得
            int     size        = format.getFrameSize(); //　1フレーム当たり 〇〇byte だよ
            byte[]  data        = new byte[ size ];
            int     readedSize  = is.read(data);
             
            // データ終了でループを抜ける
            if( readedSize == -1 ){ 
                binInNum = i + 1;
                break;
            } 
             
            // 1標本分の値を取得
            switch( format.getSampleSizeInBits() )
            {
                case 8:
                    valuesActual[i]   = (int) data[0];
                    break;
                case 16:
                    // order(LITTLE_ENDIAN) リトルエンディアンに変換
                    // getShort() このバッファの現在位置から2 byteを読み込み、現在のbyte順序に従って、これらをshort値に変換します。位置の値は、そのたびに2ずつ増加します。
                    valuesActual[i]   = (int) ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getShort();
                    break;
                default:
            }
        }

        // 音声ストリームを閉じる
        is.close();

    }
     
    /**
     * 離散フーリエ変換
     * @param in フーリエ変換を行う実数配列
     * @param outActual 計算結果の実数部配列
     * @param outImaginal 計算結果の虚数部配列
     * @param winFlg 窓関数の使用フラグ
     */
    protected void DFT( double[] in , int head, int tail, double[] outActual , double[] outImaginal , boolean winFlg )
    {
        if ( tail - head == 0) {
            for (int kIdx = 0; kIdx < binOutNum; kIdx++) {
                outActual[kIdx] = 0.0d;
                outImaginal[kIdx] = 0.0d;
            }
            return;
        }
        // 離散フーリエ変換
        for( int kIdx=0 ; kIdx<binOutNum ; kIdx++ )
        {
            // 初期化
            outActual[kIdx]    = 0.0d;
            outImaginal[kIdx]  = 0.0d;

            // System.out.println("kIdx: " + kIdx + ", dftSampleSize: " + dftSampleSize + ", binOutNum: " + binOutNum);
            
            int k = kIdx * dftSampleSize / binOutNum;
            //  System.out.println("kIdx: " + kIdx + ", k: " + k);
            // 計算
            for( int n=head ; n<tail ; n++ )
            {
                // 入力値に窓関数を適用
                double normal   = ( !winFlg )? in[n]  : hanWindow( in[n] , n , head , tail );
                 
                // k次高周波成分を計算
                outActual[kIdx]    +=        normal * Math.cos( 2.0 * Math.PI * (double)n * (double)k / (double)(tail - head) );
                outImaginal[kIdx]  += -1.0 * normal * Math.sin( 2.0 * Math.PI * (double)n * (double)k / (double)(tail - head) );
            }
             
            // 残りの計算
            //outActual[kIdx]    /= length;
            //outImaginal[kIdx]  /= length;
        }
    }
     
     
    /**
     * 窓関数（ハン窓）
     * @param in 変換する値
     * @param i 配列中のインデックス
     * @param minIndex 配列の最小インデックス
     * @param maxIndex 配列の最大インデックス
     * @return
     */
    protected double hanWindow( double in , double i , double minIndex , double maxIndex )
    {
        // 入力値の正規化
        double normal   = i / ( maxIndex - minIndex );
         
        // ハン窓関数の値を取得
        double  han     =  0.5 - 0.5 * Math.cos( 2.0 * Math.PI * normal ); // exp(-x^2/sigma^2) のような形状になる
         
        return in * han;
    }
     
    // /**
    //  * 折れ線グラフで波形表示
    //  * @param title グラフのタイトル文字
    //  * @param values グラフに出力するデータ配列
    //  * @return 作成したグラフノード
    //  */
    // @SuppressWarnings("unchecked")
    // protected Node createLineChart( String title , double[] values )
    // {
    //     // 折れ線グラフ
    //     NumberAxis                  xAxis   = new NumberAxis();
    //     NumberAxis                  yAxis   = new NumberAxis();
    //     LineChart<Number, Number>   chart   = new LineChart<Number, Number>( xAxis , yAxis );
    //     chart.setMinWidth( 400 );
    //     chart.setMinHeight( 200 );
    //     // データを作成
    //     Series< Number , Number > series1    = new Series<Number, Number>();
    //     series1.setName( title  );

    //     for( int i=0 ; i<values.length ; i++ )
    //     {
    //         series1.getData().add( new XYChart.Data<Number, Number>( i , values[i] ) );
    //     }   
    //     // データを登録
    //     chart.getData().addAll( series1 );
         
    //     // 見た目を調整
    //     chart.setCreateSymbols(false);                                                          // シンボルを消去
    //     series1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 0.75px;");   // 線を細く

    //     return chart;
    // }
}