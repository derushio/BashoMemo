package jp.itnav.derushio.bashomemo.fragmentmemoviewer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 14/12/01.
 */
public class MemoFragment extends Fragment {
	public static final String MEMO_ARGMENT = "Memo";
	public static final String ARGMENT_NULL = "";

	private View rootView;
	private EditText editMemo;
	private OnKeyDownDisablePageChange onKeyDownDisablePageChange;
	private String memo;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		onKeyDownDisablePageChange = new OnKeyDownDisablePageChange();
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		}

		try {
			if (rootView == null) {
				rootView = inflater.inflate(R.layout.fragment_memo, container, false);
			}

			if (editMemo == null) {
				editMemo = (EditText) rootView.findViewById(R.id.editMemo);
				editMemo.setText(memo);
				editMemo.setOnKeyListener(onKeyDownDisablePageChange);

				String memo = getArguments().getString(MEMO_ARGMENT);
				setMemo(memo);
			}
			return rootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}

		return new View(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		editMemo.clearFocus();
		memo = editMemo.getText().toString();
	}

	public void setMemo(String memo) {
		if (editMemo != null) {
			editMemo.setText(memo);
		}
	}

	public String getMemo() {
		if (editMemo != null) {
			editMemo.clearFocus();
			memo = editMemo.getText().toString();
		}

		return memo;
	}

	public class OnKeyDownDisablePageChange implements EditText.OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch (keyCode) {
				case (KeyEvent.KEYCODE_DPAD_LEFT):
					// ←ボタンが押されたら
					if (editMemo.getSelectionEnd() == 0) {
						// 選択範囲の終わり（カーソルも含む）が左端だったら
						return true;
						// その他のonKeyの動作を無効にする（ページが変わらない）
					}
					break;
				case (KeyEvent.KEYCODE_DPAD_RIGHT):
					// →ボタンが押されたら
					int length = ((EditText) v).getText().length();
					if (editMemo.getSelectionStart() == length) {
						// 選択範囲の始まり（カーソルも含む）が右端だったら
						return true;
						// その他のonKeyの動作を無効にする（ページが変わらない）
					}
					break;
			}
			return false;
			// なにもない場合はカーソルを移動する
		}
	}
}
