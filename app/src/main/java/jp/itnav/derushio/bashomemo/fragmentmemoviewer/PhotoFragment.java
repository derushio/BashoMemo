package jp.itnav.derushio.bashomemo.fragmentmemoviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.itnav.derushio.bashomemo.R;
import jp.itnav.derushio.bashomemo.database.FileManager;

/**
 * Created by derushio on 14/11/30.
 */
public class PhotoFragment extends Fragment {
	private static final int REQUEST_CAMERA = 0;
	private static final int REQUEST_GALLERY = 1;
	private View mRootView;
	private ImageView mPhotoImage;

	private FileManager mFileManager;
	public long mId;
	private Uri mUri;
	private Uri mTempUri;
	private Bitmap mBitmap;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {

			if (mTempUri != null) {
				File file = new File(mTempUri.getPath());
				file.delete();
			}

			switch (requestCode) {
				case (REQUEST_CAMERA):
					mTempUri = mFileManager.getCacheUri(FileManager.INDEX_CAMERA);
					try {
						mUri = mFileManager.outputImage(BitmapFactory.decodeFile(mTempUri.getPath()));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case (REQUEST_GALLERY):
					mTempUri = data.getData();
					Log.d("uri", mTempUri.toString());
					try {
						mUri = mFileManager.outputImage(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mTempUri));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
			}

			mBitmap = BitmapFactory.decodeFile(mUri.getPath());
			mPhotoImage.setImageBitmap(mBitmap);
			mTempUri = mUri;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mFileManager = new FileManager(getActivity());
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) {
				parent.removeView(mRootView);
			}
		}

		try {
			if (mRootView == null) {
				mRootView = inflater.inflate(R.layout.fragment_photo, container, false);
				mPhotoImage = (ImageView) mRootView.findViewById(R.id.imagePhoto);
				mPhotoImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
//						Intent intent = new Intent();
//						intent.setAction(Intent.ACTION_PICK);
//						intent.setType("image/*");
//						startActivity(intent);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
						alertDialogBuilder.setTitle("画像取得");
						alertDialogBuilder.setMessage("画像取得方法を選んでください");
						alertDialogBuilder.setPositiveButton("カメラ", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// カメラで撮影
								Intent intentCamera = new Intent();
								intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
								intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mFileManager.getCacheUri(FileManager.INDEX_CAMERA));
								startActivityForResult(intentCamera, REQUEST_CAMERA);
							}
						});
						alertDialogBuilder.setNeutralButton("ギャラリー", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// ギャラリーから選択
								Intent intentDocument = new Intent(Intent.ACTION_GET_CONTENT);
								intentDocument.setType("image/*");
								intentDocument.addCategory(Intent.CATEGORY_OPENABLE);
								startActivityForResult(intentDocument, REQUEST_GALLERY);
							}
						});
						alertDialogBuilder.show();
					}
				});

				try {
					Log.d("uri", mUri.toString());
					mBitmap = BitmapFactory.decodeFile(mUri.getPath());
					mPhotoImage.setImageBitmap(mBitmap);
					mTempUri = mUri;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return mRootView;
		} catch (InflateException e) {
			e.printStackTrace();
		}

		return new View(getActivity());
	}

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri mUri) {
		mTempUri = mUri;
		this.mUri = mTempUri;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}
}