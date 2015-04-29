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

	private View mRootView;
	private EditText mEditMemo;
	private OnKeyDownDisablePageChange mOnKeyDownDisablePageChange;
	private String mMemo;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mOnKeyDownDisablePageChange = new OnKeyDownDisablePageChange();
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) {
				parent.removeView(mRootView);
			}
		}

		try {
			if (mRootView == null) {
				mRootView = inflater.inflate(R.layout.fragment_memo, container, false);
			}

			if (mEditMemo == null) {
				mEditMemo = (EditText) mRootView.findViewById(R.id.editMemo);
				mEditMemo.setText(mMemo);
				mEditMemo.setOnKeyListener(mOnKeyDownDisablePageChange);

				String memo = getArguments().getString(MEMO_ARGMENT);
				setMemo(memo);
			}
			return mRootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}

		return new View(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		mEditMemo.clearFocus();
		mMemo = mEditMemo.getText().toString();
	}

	public void setMemo(String memo) {
		if (mEditMemo != null) {
			mEditMemo.setText(memo);
		}
	}

	public String getMemo() {
		if (mEditMemo != null) {
			mEditMemo.clearFocus();
			mMemo = mEditMemo.getText().toString();
		}

		return mMemo;
	}

	public class OnKeyDownDisablePageChange implements EditText.OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch (keyCode) {
				case (KeyEvent.KEYCODE_DPAD_LEFT):
					// ←ボタンが押されたら
					if (mEditMemo.getSelectionEnd() == 0) {
						// 選択範囲の終わり（カーソルも含む）が左端だったら
						return true;
						// その他のonKeyの動作を無効にする（ページが変わらない）
					}
					break;
				case (KeyEvent.KEYCODE_DPAD_RIGHT):
					// →ボタンが押されたら
					int length = ((EditText) v).getText().length();
					if (mEditMemo.getSelectionStart() == length) {
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
