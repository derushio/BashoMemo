package jp.itnav.derushio.bashomemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import jp.itnav.derushio.bashomemo.R;
import jp.itnav.derushio.bashomemo.photo_sample.PhotoSample;

/**
 * Created by derushio on 15/03/23.
 * CardViewとActivityを繋ぐクラス
 */
public class MemoCardAdapter extends RecyclerView.Adapter<MemoCardHolder> {
	private Context mContext;
	private ArrayList<MemoDataSet> mMemoDataSets;
	// このデータセットの数だけCardViewを生成する

	public MemoCardAdapter(Context context, ArrayList<MemoDataSet> memoDataSets) {
		mContext = context;
		mMemoDataSets = memoDataSets;
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
	public void onBindViewHolder(final MemoCardHolder memoCardHolder, int i) {
		final MemoDataSet data = mMemoDataSets.get(i);
		// ポジション情報からデータセットを読み込み

		memoCardHolder.mMemoName.setText(data.mMemoName);
		// カードホルダーの名前にデータセットの名前を代入

		ViewTreeObserver viewTreeObserver = memoCardHolder.mMemoImage.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			private boolean isInitialized = false;

			@Override
			public void onGlobalLayout() {
				if (!isInitialized) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					// サムネイルなのに大サイズの画像をいちいち読み込んでいたらメモリがいくらあっても足りない
					// なので、サンプリング（ピクセルを抜いて読む）して、画像サイズを大幅に小さくする
					options.inJustDecodeBounds = true;
					// trueにすることで、実際の画像は読まれず、情報だけ取ってこれる
					Bitmap bitmap;
					// 読み込む対象Bitmap

					int width = memoCardHolder.mMemoImage.getWidth();
					int height = memoCardHolder.mMemoImage.getHeight();

					if (data.mMemoImageUri != null) {
						// mMemoImageUriが存在したら
						BitmapFactory.decodeFile(data.mMemoImageUri.getPath(), options);
						// ファイルをデコード（情報だけ取ってこられていて、実際は読まれていない（optionsによって））

						options.inSampleSize = PhotoSample.getSampleSize(options, width, height);
						// サンプルサイズを確定

						options.inJustDecodeBounds = false;
						// falseにすることにより、実際に画像を読む
						bitmap = BitmapFactory.decodeFile(data.mMemoImageUri.getPath(), options);
						// 画像をサンプリングして読む
					} else {
						BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_photo, options);
						// 無いとき用の画像の情報を読み込む

						options.inSampleSize = PhotoSample.getSampleSize(options, width, height);
						// サンプルサイズを確定

						options.inJustDecodeBounds = false;
						// falseにすることにより、実際に画像を読む
						bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_photo, options);
						// 画像をサンプリングして読む
					}

					memoCardHolder.mMemoImage.setImageBitmap(bitmap);
					// 読み込んだ画像をセットする

					isInitialized = true;
				}
			}
		});

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
		if (mMemoDataSets != null) {
			return mMemoDataSets.size();
			// データセットのサイズからいくらアイテムあるか判断
		} else {
			return 0;
			// データセットが定義されてなかったら0を返す
		}
	}
}
