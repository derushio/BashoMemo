package jp.itnav.derushio.bashomemo.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 15/03/23.
 * AdapterからCardViewを操作するためのクラス
 */
public class MemoCardHolder extends RecyclerView.ViewHolder {

	public CardView mCardView;
	public ImageView mMemoImage;
	public TextView mMemoName;

	public MemoCardHolder(View itemView) {
		super(itemView);

		mCardView = (CardView) itemView.findViewById(R.id.card);
		mMemoImage = (ImageView) itemView.findViewById(R.id.memo_image);
		mMemoName = (TextView) itemView.findViewById(R.id.memo_name);
		// 中身からfindViewByIdしていじれるようにする
	}
}
