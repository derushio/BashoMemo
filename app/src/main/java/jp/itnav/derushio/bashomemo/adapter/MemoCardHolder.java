package jp.itnav.derushio.bashomemo.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 15/03/23.
 */
public class MemoCardHolder extends RecyclerView.ViewHolder {

	public CardView cardView;
	public ImageView memoImage;
	public TextView memoName;

	public MemoCardHolder(View itemView) {
		super(itemView);

		cardView = (CardView) itemView.findViewById(R.id.card);
		memoImage = (ImageView) itemView.findViewById(R.id.memo_image);
		memoName = (TextView) itemView.findViewById(R.id.memo_name);
	}
}
