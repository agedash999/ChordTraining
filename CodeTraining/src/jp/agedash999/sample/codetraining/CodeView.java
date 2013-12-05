package jp.agedash999.sample.codetraining;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CodeView
implements SurfaceHolder.Callback, Runnable {

	private SurfaceHolder holder;
	private Thread thread;
	private static Context context;
	private Bitmap whiteRect;
	private Bitmap lampR;
	private Bitmap lampB;
	private Bitmap parenthesis;
	private Paint paint = new Paint();
	private long baseTime;
	private int count;

	//設定値
	//　テンポ ＝　一分間(60000mills)に四分音符を何回打つか
	//　600000 / テンポ ＝ １拍の長さ
	private int tempo = 60000/96;
	private int rhythm = 4;

	//コード関連
	//	private Code[] codes;
	private List<Code> codelist = new ArrayList<Code>();
	private final int codeNumber = 4;

	public boolean twofiveFlag = false;

	//ループ処理のステータス
	private int status;
	private final int STOP = 0;
	private final int PREPARE_START = 1;
	private final int COUNT = 2;
	private final int PLAYING = 3;
	private final int PREPARE_STOP = 4;
	private final int ERROR = 999;

	//ログ関連
	private final String logTag = "CodeTraining.CodeView";

	//	private Random rnd = new Random();

	//画面表示位置関連
	private int canvasWidth;
	private int canvasHeight;
	private int[] size;
	private int[] srcX;
	private int[] srcY;
	private int[] dstX;
	private int[] dstY;
	private int lampSrcX;
	private int lampSrcY;
	private int lampDstX;
	private int lampDstY;


	public CodeView(Context context, SurfaceView sv){
		CodeView.context = context;

		//SurfaceViewからholderを取得し、インターフェイスを設定する。
		holder = sv.getHolder();
		holder.addCallback( this );

		whiteRect = BitmapFactory.decodeResource
				(CodeView.context.getResources(), R.drawable.white);
		lampR =  BitmapFactory.decodeResource
				(CodeView.context.getResources(), R.drawable.lamp_r);
		lampB =  BitmapFactory.decodeResource
				(CodeView.context.getResources(), R.drawable.lamp_b);

		//画面表示配列の初期化
		size = new int[codeNumber];
		srcX = new int[codeNumber];
		srcY = new int[codeNumber];
		dstX = new int[codeNumber];
		dstY = new int[codeNumber];

		//Threadの作成
		this.thread = new Thread(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 画面生成後の初期処理
		Canvas canvas = holder.lockCanvas();
		//		canvas.drawColor(Color.GRAY);
		setScreenParm(canvas);
		holder.unlockCanvasAndPost(canvas);
		doDraw();
	}

	private void setScreenParm(Canvas canvas) {
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
		int base = canvasWidth/100;
		size[0] = base * 25;
		size[1] = base * 15;
		size[2] = base * 15;
		size[3] = base * 15;

		srcY[0] = base * 6;
		dstY[0] = srcY[0] + size[0];

		srcY[1] = dstY[0] + base * 6;
		dstY[1] = srcY[1] + size[1];

		srcY[2] = dstY[1] + base * 6;
		dstY[2] = srcY[2] + size[2];

		srcY[3] = dstY[2] + base * 6;
		dstY[3] = srcY[3] + size[3];

		lampSrcY = dstY[0] + base * 3;
		lampDstY = lampSrcY + base * 5;

		srcX[0] = ( canvasHeight - size[0] ) / 2 ;
		dstX[0] = srcX[0] + size[0] ;

		dstX[1] = dstX[0];
		srcX[1] = dstX[1] - size[1] ;

		dstX[2] = dstX[0];
		srcX[2] = dstX[2] - size[2] ;

		dstX[3] = dstX[0];
		srcX[3] = dstX[3] - size[3] ;

		lampSrcX = srcX[0];
		lampDstX = lampSrcX + base * 5;

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

	public void clearCode(boolean refleshFlag){
		codelist.clear();
		if(refleshFlag){
			doDraw();
			Log.d(logTag, "clearCode");

		}
	}

	private Code createOneCode(Code prev){
		//コードを生成する
		CodeRoot root = null;
		CodeForm form = null;
		CodeTension[] tensions = null;
		if(prev == null || !twofiveFlag){
			//TODO 後ほど 設定に従って生成するロジック
			//Rootの生成

			int n = (int)(Math.random() * (EnumSet.allOf(CodeRoot.class).size()-1));
			root = CodeRoot.values()[n];
			n = (int)(Math.random() * (EnumSet.allOf(CodeForm.class).size()));
			if(n!=0){
				root = CodeRoot.values()[n-1];
			}
		}else{
			//TODO 後ほど 25フラグに応じて生成するロジック
			//TODO 後ほど 設定に従って生成するロジック
		}
		return new Code(root,form,tensions);
	}
	private void doDraw(){
		doDraw(lampR,0);
	}

	private void doDraw(Bitmap lamp,int alpha){
		//画面描画
		Canvas canvas = holder.lockCanvas();
		Rect srcRect = new Rect();
		Rect dstRect = new Rect();

		canvas.drawColor(Color.GRAY);

		//コードの描画
		for(int i = 0; i < codeNumber ; i++){
			srcRect.set(0,0,whiteRect.getWidth(),whiteRect.getHeight());
			dstRect.set(srcY[i],srcX[i],dstY[i],dstX[i]);
			canvas.drawBitmap(whiteRect, srcRect, dstRect, null);
			if(i < codelist.size()){
				Code code = codelist.get(i);
				canvas.drawBitmap(code.root.Image(), srcRect, dstRect, null);
				if(code.form!=null){
					canvas.drawBitmap(code.form.Image(), srcRect, dstRect, null);
				}
				if(code.tensions!=null && code.tensions.length!=0){
					canvas.drawBitmap(parenthesis, srcRect, dstRect, null);
					for(CodeTension tension : code.tensions){
						canvas.drawBitmap(tension.Image(), srcRect, dstRect, null);
					}
				}
			}
		}
		//TODO ランプの描画
		srcRect.set(0,0,lamp.getWidth(),lamp.getHeight());
		dstRect.set(lampSrcY,lampSrcX,lampDstY,lampDstX);
		paint.setAlpha(alpha);
		canvas.drawBitmap(lamp,srcRect,dstRect, paint);


		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void run() {
		// TODO ループ処理
		while(thread != null){
			if(status==STOP){

			}else if(status==PREPARE_START){
				//コード初期生成
				for(int i = codelist.size();i<codeNumber;i++){
					if(i<=0){
						codelist.add(i, createOneCode(null));
					}else{
						codelist.add(i, createOneCode(codelist.get(i-1)));
					}
				}
				//コード表示
				doDraw();
				//カウント開始
				status = COUNT;
				baseTime = System.currentTimeMillis();
				count = 1;
			}else if(status==COUNT){
				//カウント表示処理
				long time = System.currentTimeMillis();
				int alpha = 0;
				Bitmap lamp = lampR;
				if(time < baseTime + tempo){
					//カウント更新なし
					lamp = lampR;
					alpha = (int)(255 * (1.0 - ((double)time - baseTime)/tempo));
				}else if(count < rhythm){
					//カウント更新
					baseTime += tempo;
					count++;
					lamp = lampR;
//					alpha = (int)(255 * (1.0 - ((double)time - baseTime)/tempo));
					alpha = 255;
				}else{
					//カウント終了
					status = PLAYING;
					baseTime += tempo;
					count = 1;
					lamp = lampB;
//					alpha = (int)(255 * (1.0 - ((double)time - baseTime)/tempo));
					alpha = 255;
				}
				doDraw(lamp,alpha);
			}else if(status==PLAYING){
				//カウント表示
				long time = System.currentTimeMillis();
				int alpha = 0;
				if(time < baseTime + tempo){
					//カウント更新なし
					alpha = (int)(255 * (1.0 - ((double)time - baseTime)/tempo));
				}else if(count < rhythm){
					//カウント更新
					baseTime += tempo;
					count++;
//					alpha = (int)(255 * (1.0 - ((double)time - baseTime)/tempo));
					alpha = 255;
				}else{
					//次のコード
					//コード移動・追加生成
					codelist.remove(0);
					codelist.add(codeNumber-1,
							createOneCode(codelist.get(codeNumber-2)));
					baseTime += tempo;
					count = 1;
//					alpha = (int)(255 * (1.0 - ((double)time - baseTime)/tempo));
					alpha = 255;
				}
				doDraw(lampB,alpha);

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

	enum CodeRoot{
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

		private CodeRoot(int imageID){
			this.image = BitmapFactory.decodeResource
					(CodeView.context.getResources(), imageID);
		}
	}

	enum CodeForm{
		sevens(R.drawable.sevens),
		MajSevens(R.drawable.maj_sevens),
		MinSevens(R.drawable.min_sevens),
		MinMSevens(R.drawable.min_m_sevens),
		diminish(R.drawable.diminish);
		public final Bitmap image;
		public Bitmap Image(){return image;};

		private CodeForm(int imageID){
			this.image = BitmapFactory.decodeResource
					(CodeView.context.getResources(), imageID);
		}
	}

	enum CodeTension{
		//TODO テンションは未実装。
		;
		public final Bitmap image;
		public Bitmap Image(){return image;};

		private CodeTension(int imageID){
			this.image = BitmapFactory.decodeResource
					(CodeView.context.getResources(), imageID);
		}

	}

	class Code{
		public CodeRoot root;
		public CodeForm form;
		public CodeTension[] tensions;

		public Code(CodeRoot root,CodeForm form, CodeTension[] tensions){
			this.root = root;
			this.form = form;
			int i = 0;
			if(tensions!=null){
				for(CodeTension tension : tensions){
					tensions[i] = tension;
					i++;
				}
			}
		}
	}
}
