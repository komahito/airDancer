package com.komahito.app;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
 
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Arrays;


public class CalcSpecComponent extends Component {

    // 定数
    private final String    fileNameWav    = "/assets/sounds/archersound-tokyo-rain-serenade-archer-sounds-321180.wav";        // 解析用の音声ファイルへのパス
    // "2_23_AM.wav"
    // "archersound-tokyo-rain-serenade-archer-sounds-321180.wav"
    private final int binOutNum = 10; // 離散フーリエ変換後のスペクトルを表示する際のビン数
    private final double maxSec = 60*5;// 60*5; // 取得する音声の最大時間(s)
    private final int dftSampleSize = 1024; // 離散フーリエ変換を行う際のサンプル数
    private final int calcFps = 30;

    private double currTpfs = 0;
    // 取得する音声情報用の変数
    private AudioFormat     format                  = null;
    private double[]        valuesActual            = null;
    private double[]        valuesImaginal          = null;
    private double[]        spectrumActual          = null;
    private double[]        spectrumImaginal        = null;
    private double[]       spectrumAmplitude       = null;
    private int binInNum = 0; // 取得した音声の標本数
    private int binInPerSec = 0; // 1秒間に取得した標本数

    private double lastT = 0;
    private int head = 0;
    private int tail = 0;

    private Media musicMedia = null;
    private MediaPlayer musicPlayer = null;
    private boolean endMusic = false;

    public CalcSpecComponent () {
        initialize();
        try {
            musicMedia = new Media( getClass().getResource(fileNameWav).toExternalForm() ); // new File(fileName).toURI().toString());
            if (musicMedia == null) throw new Exception("musicPlayer failed to load media.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        musicPlayer = new MediaPlayer(musicMedia);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.setOnEndOfMedia( () -> {
            java.util.Arrays.fill(spectrumAmplitude, 0.0d);
            musicPlayer.play();
            } );

        spectrumActual       = new double[ binOutNum ];
        spectrumImaginal     = new double[ binOutNum ];
        spectrumAmplitude = new double[binOutNum];
    }

    @Override
    public void onAdded() {
        FXGL.set("SpecAmplitudes", spectrumAmplitude);
        musicPlayer.play();
    }

    @Override
    public void onUpdate(double tpf) {
        currTpfs += tpf;
        if (currTpfs < (1.0 / (double) calcFps)) return;
        else currTpfs = 0.0;
        double currSec = musicPlayer.getCurrentTime().toSeconds();

        // System.out.println("head: " + head + "tail" + tail);
        // tail = (int) ((double) head + (double) binInPerSec * (currSec - lastT)); //head + binInPerSec * (t - lastT);
        
        // head = tail;
        tail = (int) (currSec * (double) binInPerSec);
        head = (int) ((double) tail - (double) tpf * (double) binInPerSec);
        if ( tail - head <= 0 || tail > binInNum || head < 0) return;
        DFT( valuesActual, head, tail, spectrumActual , spectrumImaginal , false );
        
        // 各周波数ごとの振幅
        // java.util.Arrays.fill(spectrumAmplitude, 0.0d);
        for (int i = 0; i < binOutNum; i++) {
            spectrumAmplitude[i] = Math.hypot(spectrumActual[i], spectrumImaginal[i]);
        }

        // print spectrumAmplitude array
        // for (int i = 0; i < binOutNum; i++) {
        //     System.out.print(Math.log(spectrumAmplitude[i] + 1) + " ");
        // }
        // System.out.println();

        // FXGL.set("SpecAmplitudes", spectrumAmplitude);

        lastT = currSec;
    }
     
    private void initialize()
    {
        try{
            // 音声ストリームを取得
            // File                file    = new File( fileName );
        
            AudioInputStream is = AudioSystem.getAudioInputStream( getClass().getResource(fileNameWav)); // file );
         
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
            if (binInNum == 0) binInNum = maxBinIn;
            System.out.println("binInNum: " + binInNum);

            // 音声ストリームを閉じる
            is.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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
        // if ( tail - head <= 0 || tail > binInNum || head < 0) {
        //     for (int kIdx = 0; kIdx < binOutNum; kIdx++) {
        //         outActual[kIdx] = 0.0d;
        //         outImaginal[kIdx] = 0.0d;
        //     }
        //     return;
        // }

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

}