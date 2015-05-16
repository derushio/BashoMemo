package jp.itnav.derushio.bashomemo.adapter;

import android.net.Uri;
import android.view.View;

/**
 * Created by derushio on 15/03/25.
 * メモをCardViewに入れるときの情報をまとめたクラス
 */

public class MemoDataSet {

	// CardView情報
	public long id;
	public String memoName;
	public Uri memoImageUri;
	public View.OnClickListener onClickListener;
	public View.OnLongClickListener onLongClickListener;
	// CardView情報

	private MemoDataSet() {
	}

	public MemoDataSet(long id, String memoName, Uri memoImageUri, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		this.id = id;
		this.memoName = memoName;
		this.memoImageUri = memoImageUri;
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
		// 付加情報を自分に取り込む
	}
}
