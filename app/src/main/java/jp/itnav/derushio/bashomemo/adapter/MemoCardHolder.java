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

	public long id;
	// Views
	public CardView cardView;
	public ImageView memoImage;
	public TextView memoName;
	public CheckableImageView cheker;
	// Views

	public MemoCardHolder(View itemView) {
		super(itemView);

		cardView = (CardView) itemView;
		memoImage = (ImageView) itemView.findViewById(R.id.memo_image);
		memoName = (TextView) itemView.findViewById(R.id.memo_name);
		cheker = (CheckableImageView) itemView.findViewById(R.id.checker);
		// 中身からfindViewByIdしていじれるようにする
	}

	public boolean getChecked(){
		return cheker.getChecked();
	}
}
