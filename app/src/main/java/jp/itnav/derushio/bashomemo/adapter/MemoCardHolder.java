package jp.itnav.derushio.bashomemo.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.itnav.derushio.bashomemo.R;
import jp.itnav.derushio.bashomemo.view.CheckableImageView;

/**
 * Created by derushio on 15/03/23.
 * AdapterからCardViewを操作するためのクラス
 */
public class MemoCardHolder extends RecyclerView.ViewHolder {

	// Views
	public CardView mCardView;
	public ImageView mMemoImage;
	public TextView mMemoName;
	public CheckableImageView mChecker;
	// Views

	public MemoCardHolder(View itemView) {
		super(itemView);

		mCardView = (CardView) itemView;
		mMemoImage = (ImageView) itemView.findViewById(R.id.memo_image);
		mMemoName = (TextView) itemView.findViewById(R.id.memo_name);
		mChecker = (CheckableImageView) itemView.findViewById(R.id.checker);
		// 中身からfindViewByIdしていじれるようにする
	}
}
