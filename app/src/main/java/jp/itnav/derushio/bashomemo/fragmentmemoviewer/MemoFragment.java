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
	private View rootView;
	private EditText editMemo;
	public String memo;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

	public String getMemo() {
		if (editMemo != null) {
			editMemo.clearFocus();
			memo = editMemo.getText().toString();
		}

		return memo;
	}
}
