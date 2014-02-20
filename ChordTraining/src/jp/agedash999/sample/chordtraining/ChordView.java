package jp.agedash999.sample.chordtraining;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ChordView
implements SurfaceHolder.Callback, Runnable {

	private SurfaceHolder holder;
	private Thread thread;
	private static Context context;
	//	private Bitmap whiteRect;
	private Bitmap lampR;
	private Bitmap lampB;
	private Bitmap parenthesis;
	private Paint lampPaint = new Paint();
	private Paint whitePaint = new Paint();
	private long baseTime;
	private int count;
	private SoundPool soundPool;
	private int bell;
	private int tick;

	//設定値
	//　テンポ ＝　一分間(60000mills)に四分音符を何回打つか
	//　600000 / テンポ ＝ １拍の長さ
	//private int tempo = 60000/96;
	private int millsPerNote;
	private int rhythm;
	private final String RHYTHM_DEFAULT = "4";
	private final String KEY_TEMPO = "key_tempo";


	//コード関連
	//	private Chord[] chords;
	private List<Chord> chordlist = new ArrayList<Chord>();
	private final int chordNumber = 4;

	private Map<String,Integer> chordFormScope = new HashMap<String, Integer>();
	private final String KEY_CHORD_FORM_SUM = "chord_form_sum";

	//ループ処理のステータス
	private int status;
	private final int STOP = 0;
	private final int PREPARE_START = 1;
	private final int COUNT = 2;
	private final int PLAYING = 3;
	private final int PREPARE_STOP = 4;
	private final int ERROR = 999;

	//ログ関連
	private final String logTag = "ChordTraining.ChordView";

	//	private Random rnd = new Random();

	//画面表示位置関連
	private int canvasWidth;
	private int canvasHeight;
	private int[] width;
	private int[] height;
	private int[] srcX;
	private int[] srcY;
	private int[] dstX;
	private int[] dstY;
	private int lampSrcX;
	private int lampSrcY;
	private int lampDstX;
	private int lampDstY;

	private int bgColor = Color.rgb(201, 201, 196);

	private Rect chordSrcRect;
	private Rect lampSrcRect;
	private RectF chordDstRectF;
	private Rect lampDstRect;
	private int r;

	public ChordView(Context context, SurfaceView sv){
		ChordView.context = context;

		//SurfaceViewからholderを取得し、インターフェイスを設定する。
		holder = sv.getHolder();
		holder.addCallback( this );

		//Threadの作成
		this.thread = new Thread(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//		whiteRect = BitmapFactory.dechordResource
		//		(ChordView.context.getResources(), R.drawable.white);
		lampR =  BitmapFactory.decodeResource
				(ChordView.context.getResources(), R.drawable.lamp_r);
		lampB =  BitmapFactory.decodeResource
				(ChordView.context.getResources(), R.drawable.lamp_b);

		chordSrcRect = new Rect();
		lampSrcRect = new Rect();
		chordDstRectF = new RectF();
		lampDstRect = new Rect();

		//画面表示配列の初期化
		width = new int[chordNumber];
		height = new int[chordNumber];
		srcX = new int[chordNumber];
		srcY = new int[chordNumber];
		dstX = new int[chordNumber];
		dstY = new int[chordNumber];

		// TODO 画面生成後の初期処理
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(bgColor);
		setScreenParm(canvas);
		holder.unlockCanvasAndPost(canvas);

		Bitmap chord1 = ChordRoot.values()[0].Image();
		chordSrcRect.set(0,0,chord1.getWidth(),chord1.getHeight());
		lampSrcRect.set(0,0,lampR.getWidth(),lampR.getHeight());
		lampDstRect.set(lampSrcY,lampSrcX,lampDstY,lampDstX);
		whitePaint.setColor(Color.WHITE);

		doDraw();
	}

	public void loadSoundPool(Context cont){
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		bell = soundPool.load(cont, R.raw.bell, 0);
		tick = soundPool.load(cont, R.raw.tick, 0);
	}

	public void releaseSoundPool(Context cont){
		stopPlaying();
		soundPool.release();
	}

	private void setScreenParm(Canvas canvas) {
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();

		int baseWidth = canvasWidth/100;
		width[0] = baseWidth * 30;
		width[1] = baseWidth * 20;
		width[2] = baseWidth * 20;
		width[3] = baseWidth * 20;

		srcY[0] = baseWidth * 3;
		dstY[0] = srcY[0] + width[0];

		srcY[1] = dstY[0] + baseWidth * 3;
		dstY[1] = srcY[1] + width[1];

		srcY[2] = dstY[1] + baseWidth * 3;
		dstY[2] = srcY[2] + width[2];

		srcY[3] = dstY[2] + baseWidth * 3;
		dstY[3] = srcY[3] + width[3];

		lampSrcY = dstY[0] + baseWidth * 5;
		lampDstY = lampSrcY + baseWidth * 5;

		int baseHeight = canvasHeight/100;
		height[0] = baseHeight * 75;
		height[1] = baseHeight * 50;
		height[2] = baseHeight * 50;
		height[3] = baseHeight * 50;


		srcX[0] = ( canvasHeight - height[0] ) / 2 ;
		dstX[0] = srcX[0] + height[0] ;

		dstX[1] = dstX[0];
		srcX[1] = dstX[1] - height[1] ;

		dstX[2] = dstX[0];
		srcX[2] = dstX[2] - height[2] ;

		dstX[3] = dstX[0];
		srcX[3] = dstX[3] - height[3] ;

		lampSrcX = srcX[0];
		lampDstX = srcX[0] + baseWidth * 5;

		r = height[0] / 10;

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO 画面破棄時の処理
	}


	public void startPlaying(){
		//スレッド開始処理
		status = PREPARE_START;
		Log.d(logTag, "Status Chenged: PREPARE_START");
		switch (thread.getState()) {
		case WAITING:
			synchronized (thread) {
				thread.notify();
			}
			break;

		case NEW:
			thread.start();
			break;

		case RUNNABLE:
			thread.start();
			break;

		default:
			Log.e(logTag, "UnExpected block");
			//TODO スレッドのハンドリングについて、要検討
		}
	}

	public void stopPlaying(){
		status = PREPARE_STOP;
		Log.d(logTag, "Status Chenged: PREPARE_STOP");
	}

	public void clearChord(boolean refleshFlag){
		chordlist.clear();
		if(refleshFlag){
			doDraw();
			Log.d(logTag, "clearChord");

		}
	}

	public void changeTempo(int tempo ,boolean isSavePreference){
		this.millsPerNote = 60000/tempo;
		if(isSavePreference){
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			pref.edit().putInt(KEY_TEMPO, tempo).apply();
		}
	}

	public int loadTempoFromPreference(int defaultTempo){
		int tempo = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_TEMPO, 0);
		if(tempo==0){
			tempo = defaultTempo;
		}
		return tempo;
	}


	public void loadPreference(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String key_rhythm = context.getString(R.string.key_rhythm);
		if(!pref.contains(key_rhythm)){

		}

		//リズム設定
		rhythm = Integer.parseInt(pref.getString(context.getString(R.string.key_rhythm), RHYTHM_DEFAULT));

		//コード種の設定取得・保存
		chordFormScope.clear();
		ChordForm[] forms = ChordForm.values();
		int freq = 0;
		int sum = 0;
		for(int i = 0;i < forms.length;i++){
			freq = Integer.parseInt(pref.getString(forms[i].name(), forms[i].defaultFreq()));
			chordFormScope.put(forms[i].name(),freq);
			sum += freq;
		}
		chordFormScope.put(KEY_CHORD_FORM_SUM, sum);

		//TODO テンション設定取得・保存
	}

	private Chord createOneChord(Chord prev){
		//コードを生成する
		ChordRoot root = null;
		ChordForm form = null;
		ChordTension[] tensions = null;
		{
			//ChordRootの決定
			int random_int;
			random_int = (int)(Math.random() * (EnumSet.allOf(ChordRoot.class).size()));
			root = ChordRoot.values()[random_int];

		}

		{
			//ChordFormの決定
			double random_double;
			random_double = Math.random() * chordFormScope.get(KEY_CHORD_FORM_SUM);
			ChordForm[] forms = ChordForm.values();
			int meter = 0;
			int i = 0;
			meter += chordFormScope.get(forms[i].name());
			while(i< forms.length && meter < random_double){
				i++;
				meter += chordFormScope.get(forms[i].name());
			}
			form = forms[i];
		}

		//			n =  (int)rand;
		//			n = (int)(Math.random() * (EnumSet.allOf(ChordForm.class).size()+1));
		//			if(n!=0){
		//				form = ChordForm.values()[n - 1];
		//			}
		return new Chord(root,form,tensions);
	}
	private void doDraw(){
		doDraw(lampR,0);
	}

	private void doDraw(Bitmap lamp,int alpha){
		//画面描画
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(bgColor);

		//コードの描画
		for(int i = 0; i < chordNumber ; i++){
			chordDstRectF.set(srcY[i],srcX[i],dstY[i],dstX[i]);
			//			canvas.drawRect(chordDstRect, whitePaint);
			canvas.drawRoundRect(chordDstRectF, r, r, whitePaint);
			//			canvas.drawBitmap(whiteRect, srcRect, dstRect, null);
			if(i < chordlist.size()){
				Chord chord = chordlist.get(i);
				canvas.drawBitmap(chord.root.Image(), chordSrcRect, chordDstRectF, null);
				if(chord.form!=null){
					canvas.drawBitmap(chord.form.Image(), chordSrcRect, chordDstRectF, null);
				}
				if(chord.tensions!=null && chord.tensions.length!=0){
					canvas.drawBitmap(parenthesis, chordSrcRect, chordDstRectF, null);
					for(ChordTension tension : chord.tensions){
						canvas.drawBitmap(tension.Image(), chordSrcRect, chordDstRectF, null);
					}
				}
			}
		}
		//TODO ランプの描画
		lampPaint.setAlpha(alpha);
		canvas.drawBitmap(lamp,lampSrcRect,lampDstRect, lampPaint);


		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void run() {
		// TODO ループ処理
		while(thread != null){
			if(status==STOP){

			}else if(status==PREPARE_START){
				//コード初期生成
				for(int i = chordlist.size();i<chordNumber;i++){
					if(i<=0){
						chordlist.add(i, createOneChord(null));
					}else{
						chordlist.add(i, createOneChord(chordlist.get(i-1)));
					}
				}
				//コード表示
				doDraw();
				//カウント開始
				status = COUNT;
				baseTime = System.currentTimeMillis();
				count = 0;
			}else if(status==COUNT){
				//カウント表示処理
				long time = System.currentTimeMillis();
				int alpha = 0;
				Bitmap lamp = null;
				int sound = 0;
				//最初のカウント
				if(count==0){
					lamp = lampB;
					alpha = 255;
					sound = tick;
					count = 1;
				}
				if(time < baseTime + millsPerNote/2){
					//カウント更新なし
				}else if(time < baseTime + millsPerNote){
					lamp = lampB;
					alpha = 50;
				}else if(count < rhythm){
					//カウント更新
					baseTime += millsPerNote;
					count++;
					lamp = lampB;
					alpha = 255;
					sound = tick;
				}else{
					//カウント終了
					status = PLAYING;
					baseTime += millsPerNote;
					count = 1;
					lamp = lampR;
					sound = bell;
					alpha = 255;
				}
				if(lamp!=null){
					if(sound!=0){
						soundPool.play(sound, 1.0F, 1.0F, 0, 0, 1.0F);
					}
					doDraw(lamp,alpha);
				}
			}else if(status==PLAYING){
				//カウント表示
				long time = System.currentTimeMillis();
				int alpha = 0;
				Bitmap lamp = null;
				int sound = 0;
				if(time < baseTime + millsPerNote/2){
					//カウント更新なし
				}else if(time < baseTime + millsPerNote){
					if(count==1){
						lamp = lampR;
					}else{
						lamp = lampB;
					}
					alpha = 50;
				}else if(count < rhythm){
					//カウント更新
					baseTime += millsPerNote;
					count++;
					lamp = lampB;
					sound = tick;
					alpha = 255;
				}else{
					//次のコード
					//コード移動・追加生成
					chordlist.remove(0);
					chordlist.add(chordNumber-1,
							createOneChord(chordlist.get(chordNumber-2)));
					baseTime += millsPerNote;
					count = 1;
					lamp = lampR;
					sound = bell;
					alpha = 255;
				}
				if(lamp!=null){
					if(sound!=0){
						soundPool.play(sound, 1.0F, 1.0F, 0, 0, 1.0F);
					}
					doDraw(lamp,alpha);
				}

			}else if(status==PREPARE_STOP){
				synchronized (thread) {
					try {
						status = STOP;
						//画面の初期化
						doDraw();
						thread.wait();
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						status = ERROR;
						Log.e(logTag, "InterruptedException");
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void surfaceChanged
	(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	enum ChordRoot{
		C(R.drawable.c),
		F(R.drawable.f),
		Bf(R.drawable.bf),
		Ef(R.drawable.ef),
		Af(R.drawable.af),
		Cs(R.drawable.cs),
		Fs(R.drawable.fs),
		B(R.drawable.b),
		E(R.drawable.e),
		A(R.drawable.a),
		D(R.drawable.d),
		G(R.drawable.g);
		public final Bitmap image;
		public Bitmap Image(){return image;};

		private ChordRoot(int imageID){
			this.image = BitmapFactory.decodeResource
					(ChordView.context.getResources(), imageID);
		}
	}

	enum ChordForm{
		major_chord(R.drawable.alpha,"4"),
		minor_chord(R.drawable.minor,"4"),
		sevens_chord(R.drawable.sevens,"2"),
		major_sevens_chord(R.drawable.maj_sevens,"2"),
		minor_sevens_chord(R.drawable.min_sevens,"2"),
		minor_m_sevens_chord(R.drawable.min_m_sevens,"0"),
		diminish_chord(R.drawable.diminish,"0");
		public final Bitmap image;
		public final String defaultFreq;
		public Bitmap Image(){return image;};
		public String defaultFreq(){return defaultFreq;};

		private ChordForm(int imageID,String defaultFreq){
			this.image = BitmapFactory.decodeResource
					(ChordView.context.getResources(), imageID);
			this.defaultFreq = defaultFreq;
		}
	}

	enum ChordTension{
		//TODO テンションは未実装。
		;
		public final Bitmap image;
		public Bitmap Image(){return image;};

		private ChordTension(int imageID){
			this.image = BitmapFactory.decodeResource
					(ChordView.context.getResources(), imageID);
		}

	}

	class Chord{
		public ChordRoot root;
		public ChordForm form;
		public ChordTension[] tensions;

		public Chord(ChordRoot root,ChordForm form, ChordTension[] tensions){
			this.root = root;
			this.form = form;
			int i = 0;
			if(tensions!=null){
				for(ChordTension tension : tensions){
					tensions[i] = tension;
					i++;
				}
			}
		}
	}
}
