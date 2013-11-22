package jp.agedash999.sample.codetraining;

import android.app.Activity;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			//TODO 251無効化 ハンドリング方法問題ないか
			//TODO ボタン切替 ハンドリング方法問題ないか
			v.setEnabled(false);
			findViewById(R.id.btn_stop).setEnabled(true);

			codeView.startThread();
			break;

		case R.id.btn_stop:
			//TODO 251有効化 ハンドリング方法問題ないか
			//TODO ボタン切替 ハンドリング方法問題ないか
			v.setEnabled(false);
			findViewById(R.id.btn_start).setEnabled(true);

			codeView.stopThread();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {

		switch (cb.getId()) {
		case R.id.cbx_251:
			//TODO チェックボックス変更時の処理

			break;

		default:
			break;
		}
	}
}
