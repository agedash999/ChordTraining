package jp.agedash999.sample.codetraining;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity
extends Activity
implements OnClickListener, OnCheckedChangeListener {

	private SurfaceView sv; //標準のSV？
	private CodeView codeView;

	//ログ関連
	private final String logTag = "CodeTraining.MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(logTag, "onCreate");
		//定番
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//TODO 後ほど：設定読み込み・DB初期化

		//SurfaceView設定
		sv = (SurfaceView)findViewById(R.id.sv_main);
		codeView = new CodeView(this, sv);

		//画像読み込み→enumのところで読み込むので不要

		//リスナー設定
		((Button)findViewById(R.id.btn_start)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_stop)).setOnClickListener(this);
		((CheckBox)findViewById(R.id.cbx_251)).setOnCheckedChangeListener(this);

		changeUIforWaiting();
	}

	@Override
	protected void onPause() {
		changeUIforWaiting();
		codeView.stopPlaying();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO メニュー設定
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_start:
			changeUIforPlaying();
			codeView.startPlaying();
			break;

		case R.id.btn_stop:
			v.setEnabled(false);
			changeUIforWaiting();
			codeView.stopPlaying();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {

		switch (cb.getId()) {
		case R.id.cbx_251:
			//TODO 後ほど）チェックボックス変更時の処理
			codeView.clearCode(true);

			break;

		default:
			break;
		}
	}

	private void changeUIforPlaying(){
		//TODO ボタン切替 ハンドリング方法問題ないか
		((Button)findViewById(R.id.btn_start)).setEnabled(false);
		((Button)findViewById(R.id.btn_stop)).setEnabled(true);
		((CheckBox)findViewById(R.id.cbx_251)).setEnabled(false);
	}

	private void changeUIforWaiting(){
		//TODO ボタン切替 ハンドリング方法問題ないか
		((Button)findViewById(R.id.btn_start)).setEnabled(true);
		((Button)findViewById(R.id.btn_stop)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbx_251)).setEnabled(true);
	}
}
