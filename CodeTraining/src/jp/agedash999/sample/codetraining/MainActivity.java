package jp.agedash999.sample.codetraining;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity
extends Activity
implements OnClickListener, OnCheckedChangeListener, OnSeekBarChangeListener{

	private final int TEMPO_MIN = 40;
	private final int TEMPO_DEFA = 96;
	private final int TEMPO_MAX = 208;

	private final int RQC_SETTINGS = 0;

	private boolean reflesh_flag = false;
	private SurfaceView sv; //標準のSV？
	private CodeView codeView;
	private SeekBar skb_tempo;
	private TextView txv_tempo;


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

		//コントロールUIの取得
		skb_tempo = (SeekBar)findViewById(R.id.skb_tempo);
		txv_tempo = (TextView)findViewById(R.id.txv_tempo);

		//リスナー設定
		((Button)findViewById(R.id.btn_start)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_stop)).setOnClickListener(this);
		((CheckBox)findViewById(R.id.cbx_251)).setOnCheckedChangeListener(this);
		skb_tempo.setOnSeekBarChangeListener(this);

		skb_tempo.setMax(TEMPO_MAX - TEMPO_MIN);
		int tempo = codeView.loadTempoFromPreference(TEMPO_DEFA);
		skb_tempo.setProgress(tempo - TEMPO_MIN);
//		skb_tempo.setProgress(TEMPO_DEFA - TEMPO_MIN);
//		edt_tempo.setText(Integer.toString(TEMPO_DEFA));

		changeUIforWaiting();
	}

	@Override
	protected void onPause() {
		changeUIforWaiting();
		codeView.releaseSoundPool(getApplicationContext());
		codeView.changeTempo(skb_tempo.getProgress() + TEMPO_MIN, true);
		super.onPause();
	}

	@Override
	protected void onResume() {
		codeView.loadSoundPool(getApplicationContext());
		codeView.loadPreference();
		int tempo = codeView.loadTempoFromPreference(TEMPO_DEFA);
		skb_tempo.setProgress(tempo - TEMPO_MIN);
		if(reflesh_flag){
			codeView.clearCode(false);
			reflesh_flag = false;
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO メニュー設定
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, RQC_SETTINGS);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(
			int requestCode,
			int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RQC_SETTINGS == resultCode){
			codeView.loadPreference();
			reflesh_flag = true;
		}
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		int tempo = progress + TEMPO_MIN;
		codeView.changeTempo(tempo ,false);
		txv_tempo.setText(Integer.toString(tempo));
	}

//	public void onTextChanged(CharSequence s, int start, int before, int count) {
//		String str = s.toString();
//		int tempo = 56;
//		try{
//			tempo = Integer.parseInt(str);
//		}catch(NumberFormatException e){
//			//数値以外が入力された場合　基本ないはず
//		}
//		if(tempo<TEMPO_MIN){
//			tempo = TEMPO_MIN;
//		}else if(TEMPO_MAX < tempo){
//			tempo = TEMPO_MAX;
//		}
//		codeView.changeTempo(tempo ,true);
//		skb_tempo.setProgress(tempo-TEMPO_MIN);
//	}

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

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
