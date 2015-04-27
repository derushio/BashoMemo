package jp.itnav.derushio.bashomemo.fragmentmemoviewer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 14/12/01.
 */
public class MemoFragment extends Fragment {
	private View mRootView;
	private EditText mEditMemo;
	public String mMemo;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

	public String getMemo() {
		if (mEditMemo != null) {
			mEditMemo.clearFocus();
			mMemo = mEditMemo.getText().toString();
		}

		return mMemo;
	}
}
