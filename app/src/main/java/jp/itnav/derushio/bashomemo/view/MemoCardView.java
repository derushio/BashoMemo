package jp.itnav.derushio.bashomemo.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 15/03/23.
 */
public class MemoCardView extends CardView {

	public ImageView memoImage;
	public TextView memoText;

	public MemoCardView(Context context) {
		super(context);
		constructor(context);
	}

	public MemoCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		constructor(context);
	}

	private void constructor(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.cardview_memo, this);
		memoImage = (ImageView) root.findViewById(R.id.memo_image);
		memoText = (TextView) root.findViewById(R.id.memo_name);
	}
}
