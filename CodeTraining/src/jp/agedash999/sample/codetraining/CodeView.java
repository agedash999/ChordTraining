package jp.agedash999.sample.codetraining;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CodeView
	implements SurfaceHolder.Callback, Runnable {

	private SurfaceHolder holder;
	private Thread thread;
	private static Context context;
	private Bitmap whiteRect;

	//ループ処理のステータス
	private int status;
	private final int STOP = 0;
	private final int PREPARE_START = 1;
	private final int COUNT = 2;
	private final int PLAYING = 3;
	private final int PREPARE_STOP = 4;


	public CodeView(Context context, SurfaceView sv){
		CodeView.context = context;

		//SurfaceViewからholderを取得し、インターフェイスを設定する。
		holder = sv.getHolder();
		holder.addCallback( this );

		this.whiteRect = BitmapFactory.decodeResource
				(CodeView.context.getResources(), R.drawable.white);

		//Threadの作成
		this.thread = new Thread(this);
	}

	public void startThread(){
		//スレッド開始処理
		status = PREPARE_START;
		this.thread.start();
	}

	public void stopThread(){
		//TODO スレッド一時停止処理
		status = PREPARE_STOP;
	}

	@Override
	public void run() {
		// TODO ループ処理

	}

	@Override
	public void surfaceChanged
	(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO 画面生成後の初期処理
        Canvas canvas = arg0.lockCanvas();
        canvas.drawColor(Color.GRAY);
        arg0.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO 画面破棄時の処理
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

	}

}
