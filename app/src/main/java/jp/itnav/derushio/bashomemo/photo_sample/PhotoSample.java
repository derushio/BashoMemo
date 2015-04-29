package jp.itnav.derushio.bashomemo.photo_sample;

import android.graphics.BitmapFactory;

/**
 * Created by derushio on 15/04/29.
 */
public class PhotoSample {
	public static int getSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
