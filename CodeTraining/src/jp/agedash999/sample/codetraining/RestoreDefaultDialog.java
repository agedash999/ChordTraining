package jp.agedash999.sample.codetraining;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class RestoreDefaultDialog extends DialogPreference {

	public RestoreDefaultDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RestoreDefaultDialog(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
		boolean isRestore = false;
		if(positiveResult){
			isRestore = true;
		}
		edit.putBoolean(getContext().getString(R.string.key_restore_default), isRestore);
		edit.apply();
		super.onDialogClosed(positiveResult);
	}

}
