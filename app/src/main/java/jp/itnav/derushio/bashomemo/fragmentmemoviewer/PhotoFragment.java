package jp.itnav.derushio.bashomemo.fragmentmemoviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.itnav.derushio.bashomemo.R;
import jp.itnav.derushio.bashomemo.photo_sample.PhotoSample;
import jp.itnav.derushio.photofilemanager.PhotoFileManager;

/**
 * Created by derushio on 14/11/30.
 * 写真を保存するフラグメント
 */
public class PhotoFragment extends Fragment {
	public static final String PHTO_URI_ARGMENT = "PhotoUri";
	public static final String ARGMENT_NULL = "";

	private static final int REQUEST_CAMERA = 0;
	private static final int REQUEST_GALLERY = 1;

	private static final String CAMERA_CACHE_NAME = "CAMERA_CACHE";

	private View rootView;
	private ImageView photoImage;

	private boolean isInitialized = false;
	private PhotoFileManager photoFileManager;
	public long id;
	private Uri uri;
	private Uri tempUri;
	private Bitmap bitmap;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		photoFileManager = new PhotoFileManager(getActivity());
		// PhotoFileManagerをインスタンス

		if (rootView != null) {
			// mRootViewが定義されていたら
			ViewGroup parent = (ViewGroup) rootView.getParent();
			// mRootViewの親情報を取得

			if (parent != null) {
				// 親が存在していたら
				parent.removeView(rootView);
				// 親からmRootViewを削除
			}
		}

		try {
			if (rootView == null) {
				// mRootViewが定義されていなかったら
				rootView = inflater.inflate(R.layout.fragment_photo, container, false);
				// containerにinflateして、R.layout.fragment_photo情報をmRootViewに代入（falseのため）

				photoImage = (ImageView) rootView.findViewById(R.id.imagePhoto);
				photoImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// mPhotoImageをクリックしたら
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
						alertDialogBuilder.setTitle("画像取得");
						alertDialogBuilder.setMessage("画像取得方法を選んでください");
						alertDialogBuilder.setPositiveButton("カメラ", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intentCamera = new Intent();
								intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
								intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFileManager.getCacheFile(CAMERA_CACHE_NAME)));
								startActivityForResult(intentCamera, REQUEST_CAMERA);
								// カメラで撮影
							}
						});
						alertDialogBuilder.setNeutralButton("ギャラリー", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intentDocument = new Intent(Intent.ACTION_GET_CONTENT);
								intentDocument.setType("image/*");
								intentDocument.addCategory(Intent.CATEGORY_OPENABLE);
								startActivityForResult(intentDocument, REQUEST_GALLERY);
								// ギャラリーから選択
							}
						});
						alertDialogBuilder.show();
						// ダイアログを表示
					}
				});

				ViewTreeObserver viewTreeObserver = photoImage.getViewTreeObserver();
				viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						String uriString = getArguments().getString(PHTO_URI_ARGMENT);
						if (!uriString.equals(ARGMENT_NULL)) {
							Uri uri = Uri.parse(uriString);
							setUri(uri);
						}
						photoImage.getViewTreeObserver().removeOnPreDrawListener(this);
						return false;
					}
				});
			}

			return rootView;
		} catch (InflateException e) {
			// inflateできなかった場合
			e.printStackTrace();
		}

		return new View(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {

			if (tempUri != null) {
				File file = new File(tempUri.getPath());
				file.delete();
			}

			switch (requestCode) {
				case (REQUEST_CAMERA):
					tempUri = Uri.fromFile(photoFileManager.getCacheFile(CAMERA_CACHE_NAME));
					try {
						String outputName = "" + System.currentTimeMillis() + ".jpg";
						File outputFile = new File(getOutputImagePath());
						uri = Uri.fromFile(photoFileManager.outputImage(BitmapFactory.decodeFile(tempUri.getPath()), outputFile, outputName, false));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case (REQUEST_GALLERY):
					tempUri = data.getData();
					Log.d("uri", tempUri.toString());
					try {
						String outputName = "" + System.currentTimeMillis() + ".jpg";
						File outputFile = new File(getOutputImagePath());
						uri = Uri.fromFile(photoFileManager.outputImage(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), tempUri), outputFile, outputName, false));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
			}

			setUri(uri);
		}
	}

	public void setImageUri(Uri uri) {
		if (photoImage != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			// サムネイルなのに大サイズの画像をいちいち読み込んでいたらメモリがいくらあっても足りない
			// なので、サンプリング（ピクセルを抜いて読む）して、画像サイズを大幅に小さくする
			options.inJustDecodeBounds = true;
			// trueにすることで、実際の画像は読まれず、情報だけ取ってこれる
			Bitmap bitmap;
			// 読み込む対象Bitmap

			BitmapFactory.decodeFile(uri.getPath(), options);
			// ファイルをデコード（情報だけ取ってこられていて、実際は読まれていない（optionsによって））

			options.inSampleSize = PhotoSample.getSampleSize(options, photoImage.getWidth(), photoImage.getHeight());
			// サンプルサイズを確定

			options.inJustDecodeBounds = false;
			// falseにすることにより、実際に画像を読む
			bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
			// 画像をサンプリングして読む
			photoImage.setImageBitmap(bitmap);
		}
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		if (photoImage != null) {
			this.uri = uri;
			setImageUri(this.uri);
			tempUri = this.uri;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	public String getOutputImagePath() {
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/bashomemo");
		if (file.exists() == false) {
			file.mkdirs();
		}

		return file.getPath();
	}
}