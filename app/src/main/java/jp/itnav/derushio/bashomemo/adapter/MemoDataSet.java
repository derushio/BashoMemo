package jp.itnav.derushio.bashomemo.adapter;

import android.net.Uri;
import android.view.View;

/**
 * Created by derushio on 15/03/25.
 */

public class MemoDataSet {
	public String memoNme;
	public Uri memoImageUri;
	public View.OnClickListener onClickListener;
	public View.OnLongClickListener onLongClickListener;

	public MemoDataSet() {

	}

	public MemoDataSet(String memoNme, Uri memoImageUri, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		this.memoNme = memoNme;
		this.memoImageUri = memoImageUri;
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}
}
