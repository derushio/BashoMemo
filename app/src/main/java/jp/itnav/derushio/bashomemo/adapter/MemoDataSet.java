package jp.itnav.derushio.bashomemo.adapter;

import android.net.Uri;
import android.view.View;

/**
 * Created by derushio on 15/03/25.
 */

public class MemoDataSet {
	public String mMemoName;
	public Uri mMemoImageUri;
	public View.OnClickListener mOnClickListener;
	public View.OnLongClickListener mOnLongClickListener;

	public MemoDataSet() {

	}

	public MemoDataSet(String memoNme, Uri memoImageUri, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		mMemoName = memoNme;
		mMemoImageUri = memoImageUri;
		mOnClickListener = onClickListener;
		mOnLongClickListener = onLongClickListener;
	}
}
