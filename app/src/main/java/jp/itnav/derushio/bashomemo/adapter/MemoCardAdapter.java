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
 * CardViewとActivityを繋ぐクラス
 */
public class MemoCardAdapter extends RecyclerView.Adapter<MemoCardHolder> {
	private Context mContext;
	private ArrayList<MemoDataSet> mMemoDataSet;
	// このデータセットの数だけCardViewを生成する

	public MemoCardAdapter(Context context, ArrayList<MemoDataSet> memoDataSets) {
		mContext = context;
		mMemoDataSet = memoDataSets;
	}

	@Override
	public MemoCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_memo, viewGroup, false);
		// cardview_memoを生成し、画面に描画するためのviewGroupに紐付け、
		// そしてfalseにしているため、cardview_memoのレイアウトがそのまま帰ってくるのでviewに突っ込む
		// （trueにしているとViewGroupが帰ってきてしまい、findViewByIdがうまく通らない）
		return new MemoCardHolder(view);
		// viewの情報からMemoCardHolderに突っ込む（中でfindViewByIdなどの初期化をしている）
	}
	// 一つあたりのCardViewを生成

	@Override
	public void onBindViewHolder(MemoCardHolder memoCardHolder, int i) {
		final MemoDataSet data = mMemoDataSet.get(i);
		// ポジション情報からデータセットを読み込み

		memoCardHolder.mMemoName.setText(data.mMemoName);
		// カードホルダーの名前にデータセットの名前を代入

		BitmapFactory.Options options = new BitmapFactory.Options();
		// サムネイルなのに大サイズの画像をいちいち読み込んでいたらメモリがいくらあっても足りない
		// なので、サンプリング（ピクセルを抜いて読む）して、画像サイズを大幅に小さくする
		options.inJustDecodeBounds = true;
		// trueにすることで、実際の画像は読まれず、情報だけ取ってこれる
		Bitmap bitmap;
		// 読み込む対象Bitmap

		if (data.mMemoImageUri != null) {
			// mMemoImageUriが存在したら
			BitmapFactory.decodeFile(data.mMemoImageUri.getPath(), options);
			// ファイルをデコード（情報だけ取ってこられていて、実際は読まれていない（optionsによって））

			options.inSampleSize = calculateInSampleSize(options, 50, 50);
			// サンプルサイズを確定

			options.inJustDecodeBounds = false;
			// falseにすることにより、実際に画像を読む
			bitmap = BitmapFactory.decodeFile(data.mMemoImageUri.getPath(), options);
			// 画像をサンプリングして読む
		} else {
			BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_photo, options);
			// 無いとき用の画像の情報を読み込む

			options.inSampleSize = calculateInSampleSize(options, 50, 50);
			// サンプルサイズを確定

			options.inJustDecodeBounds = false;
			// falseにすることにより、実際に画像を読む
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_photo, options);
			// 画像をサンプリングして読む
		}

		memoCardHolder.mMemoImage.setImageBitmap(bitmap);
		// 読み込んだ画像をセットする

		if (data.mOnClickListener != null) {
			memoCardHolder.mCardView.setOnClickListener(data.mOnClickListener);
			// クリックした時の反応をセット
		}

		if (data.mOnLongClickListener != null) {
			memoCardHolder.mCardView.setOnLongClickListener(data.mOnLongClickListener);
			// 長くクリックした時の反応をセット
		}
	}

	@Override
	public int getItemCount() {
		if (mMemoDataSet != null) {
			return mMemoDataSet.size();
			// データセットのサイズからいくらアイテムあるか判断
		} else {
			return 0;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// オプションと求める幅と高さからサンプルサイズを確定
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		// デフォルトはフルサイズ

		if (height > reqHeight || width > reqWidth) {
			// フルサイズよりも小さくする必要があるなら
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
			// 幅の小さい方でサンプリングサイズを確定
		}
		return inSampleSize;
	}
}
