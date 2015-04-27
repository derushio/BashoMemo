package jp.itnav.derushio.bashomemo.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by derushio on 15/01/30.
 */
public class FileManager {

	private Context mContext;

	public static final int INDEX_CAMERA = 0;
	public static final int INDEX_GALLERY = 1;
//	tempファイルが必要なアクション

	public static final String[] TEMP_NAME = {"temp1.jpg", "temp2.jpg"};
//	tempファイルの名前

	public FileManager(Context context) {
		this.mContext = context;

		File file = new File(getOutputImagePath());
		file.mkdirs();
	}

	public String getCachePath() {
		return (mContext.getExternalFilesDir("cache").getPath());
	}
//	キャッシュするためのPathを取得

	public Uri getCacheUri(int index) {
		String path = getCachePath();
		File file = new File(path, TEMP_NAME[index]);
		Uri uri = Uri.fromFile(file);

		return (uri);
	}
//	キャッシュするためのファイルのUri

	public String getOutputImagePath() {
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/bashomemo");
		if (file.exists() == false) {
			file.mkdirs();
		}

		return file.getPath();
	}
//	イメージ出力用のパスを取得

	public Uri outputImage(Bitmap bitmap) throws FileNotFoundException {
		String outputName = "" + System.currentTimeMillis() + ".jpg";
		File outputFile = new File(getOutputImagePath(), outputName);
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

//		MediaScannerConnection.scanFile(context, new String[]{outputFile.getPath()}, new String[]{"image/jpg"}, null);

		return Uri.parse(outputFile.getPath());
	}
//	イメージを出力し、ライブラリDBに登録
}
