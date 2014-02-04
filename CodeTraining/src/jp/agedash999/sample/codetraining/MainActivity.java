package jp.agedash999.sample.codetraining;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity
extends Activity
implements OnClickListener, OnCheckedChangeListener, OnSeekBarChangeListener, TextWatcher {

	private final int TEMPO_MIN = 40;
	private final int TEMPO_DEFO = 96;
	private final int TEMPO_MAX = 208;

	private SurfaceView sv; //標準のSV？
	private CodeView codeView;
	private SeekBar skb_tempo;
	private EditText edt_tempo;


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


		//コントロールUIの取得
		skb_tempo = (SeekBar)findViewById(R.id.skb_tempo);
		edt_tempo = (EditText)findViewById(R.id.edt_tempo);

		//リスナー設定
		((Button)findViewById(R.id.btn_start)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_stop)).setOnClickListener(this);
		((CheckBox)findViewById(R.id.cbx_251)).setOnCheckedChangeListener(this);
		skb_tempo.setOnSeekBarChangeListener(this);
		edt_tempo.addTextChangedListener(this);
		edt_tempo.setOnClickListener(this);

		skb_tempo.setMax(TEMPO_MAX - TEMPO_MIN);
		skb_tempo.setProgress(TEMPO_DEFO - TEMPO_MIN);
		edt_tempo.setText(Integer.toString(TEMPO_DEFO));

		changeUIforWaiting();
	}

	@Override
	protected void onPause() {
		changeUIforWaiting();
		codeView.doOnPause(getApplicationContext());
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		codeView.doOnResume(getApplicationContext());
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		int tempo = progress + TEMPO_MIN;
    	codeView.changeTempo(tempo);
		edt_tempo.setText(Integer.toString(tempo));
	}

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    	String str = s.toString();
    	int tempo = 56;
    	try{
    		tempo = Integer.parseInt(str);
    	}catch(NumberFormatException e){
    		//数値以外が入力された場合　基本ないはず
    	}
    	if(tempo<TEMPO_MIN){
    		tempo = TEMPO_MIN;
    	}else if(TEMPO_MAX < tempo){
    		tempo = TEMPO_MAX;
    	}
    	codeView.changeTempo(tempo);
    	skb_tempo.setProgress(tempo-TEMPO_MIN);
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

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
