package jp.itnav.derushio.bashomemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 15/03/23.
 */
public class MemoCardAdapter extends RecyclerView.Adapter<MemoCardHolder> {
	private Context mContext;
	private ArrayList<MemoDataSet> mMemoDataSet;

	public MemoCardAdapter(Context context, ArrayList<MemoDataSet> memoDataSets) {
		mContext = context;
		mMemoDataSet = memoDataSets;
	}

	@Override
	public MemoCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_memo, viewGroup, false);
		return new MemoCardHolder(view);
	}

	@Override
	public void onBindViewHolder(MemoCardHolder memoCardHolder, int i) {
		final MemoDataSet data = mMemoDataSet.get(i);

		memoCardHolder.mMemoName.setText(data.mMemoName);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap;

		if (data.mMemoImageUri != null) {
			BitmapFactory.decodeFile(data.mMemoImageUri.getPath(), options);

			options.inSampleSize = calculateInSampleSize(options, 50, 50);

			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(data.mMemoImageUri.getPath(), options);
		} else {
			BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_photo, options);

			options.inSampleSize = calculateInSampleSize(options, 50, 50);

			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_photo, options);
		}

		memoCardHolder.mMemoImage.setImageBitmap(bitmap);

		if (data.mOnClickListener != null) {
			memoCardHolder.mCardView.setOnClickListener(data.mOnClickListener);
		}
		if (data.mOnLongClickListener != null) {
			memoCardHolder.mCardView.setOnLongClickListener(data.mOnLongClickListener);
		}
	}

	@Override
	public int getItemCount() {
		if (mMemoDataSet != null) {
			return mMemoDataSet.size();
		} else {
			return 0;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
}
