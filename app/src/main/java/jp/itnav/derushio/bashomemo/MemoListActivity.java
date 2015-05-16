package jp.itnav.derushio.bashomemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import jp.itnav.derushio.bashomemo.adapter.MemoCardAdapter;
import jp.itnav.derushio.bashomemo.adapter.MemoCardHolder;
import jp.itnav.derushio.bashomemo.adapter.MemoDataSet;
import jp.itnav.derushio.bashomemo.database.MemoDataBaseHelper;
import jp.itnav.derushio.bashomemo.database.MemoDataBaseManager;


public class MemoListActivity extends AppCompatActivity {

	// View
	private Toolbar toolbar;
	private RecyclerView memoList;
	private ArrayList<MemoDataSet> memoDataSet;
	private MemoCardAdapter memoCardAdapter;
	private ImageView imageFloating;
	// View

	private MemoDataBaseManager memoDatabaseManager;

	private boolean deleteMode = false;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_list);
		initializeToolbar();

		// findViewById
		memoList = (RecyclerView) findViewById(R.id.recycler_view);
		imageFloating = (ImageButton) findViewById(R.id.image_button_floating);
		// findViewById

		// レイアウト情報をLinearLayoutに設定
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		memoList.setLayoutManager(linearLayoutManager);
		// レイアウト情報をLinearLayoutに設定

		// レイアウト情報を設定
		memoList.setHasFixedSize(true);
		memoList.setItemAnimator(new DefaultItemAnimator());
		// レイアウト情報を設定

		// メモのデータベースを読み込み
		memoDatabaseManager = new MemoDataBaseManager(this);
		// メモのデータベースを読み込み

		// メモのデータ情報を定義
		memoDataSet = new ArrayList<>();
		memoCardAdapter = new MemoCardAdapter(this, memoDataSet);
		memoList.setAdapter(memoCardAdapter);
		// メモのデータ情報を定義

		// 全てのデータをロード
		Cursor cursor = memoDatabaseManager.getAllDataCursor(MemoDataBaseHelper.ITEM_ID);
		cursor.moveToFirst();
		// 全てのデータをロード

		// データをパース
		for (int i = 0; i < cursor.getCount(); i++) {
			// サムネイルがある場合は読み込む
			Uri uri = null;

			if (cursor.getString(memoDatabaseManager.INDEX_PICTURE_URI) != null) {
				uri = Uri.parse(cursor.getString(memoDatabaseManager.INDEX_PICTURE_URI));
			}
			// サムネイルがある場合は読み込む

			final long id = cursor.getLong(memoDatabaseManager.INDEX_ID);
			// idを読み込み

			// データセットに読み込んだ情報を追加
			memoDataSet.add(new MemoDataSet(id, cursor.getString(memoDatabaseManager.INDEX_TITLE), uri,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MemoListActivity.this, MemoViewerActivity.class);
							intent.putExtra(MemoViewerActivity.INTENT_EXTRA_ID, id);
							startActivity(intent);
							// 通常クリックだった場合はそのメモを開く
						}
					},
					new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							MemoCardHolder memoCardHolder = new MemoCardHolder(v);
							memoCardHolder.cheker.check();
							imageFloating.setImageResource(R.mipmap.button_floating_trash);
							deleteMode = true;
							return true;
						}
					}));
			// データセットに読み込んだ情報を追加

			cursor.moveToNext();
			// 次のデータ
		}
		// データをパース

		memoCardAdapter.notifyDataSetChanged();
		// データセットが変わったことを通知する

	}

	private void initializeToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " + "'toolbar'");
		}
		toolbar.setTitle("場所メモったーよ！！");
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.inflateMenu(R.menu.menu_memo_list);
	}
	// ツールバーを初期化

	public void onClickFloatingButton(View v) {
		if (deleteMode) {

			final AppCompatDialog appCompatDialog = new AppCompatDialog(this);
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_memo, null, false);
			appCompatDialog.setContentView(view);
			appCompatDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			Button buttonOk = (Button) view.findViewById(R.id.button_ok);
			Button buttonCancel = (Button) view.findViewById(R.id.button_cancel);


			buttonOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < memoList.getChildCount(); i++) {
						MemoCardHolder holder = (MemoCardHolder) memoList.getChildViewHolder(memoList.getChildAt(i));
						if (holder.getChecked()) {
							memoDatabaseManager.deleteMemoData(holder.id);
						}
					}
					appCompatDialog.dismiss();

					Intent intent = new Intent(MemoListActivity.this, MemoListActivity.class);
					startActivity(intent);
				}
			});

			buttonCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					appCompatDialog.dismiss();
				}
			});

			appCompatDialog.show();
		} else {
			Intent intent = new Intent(MemoListActivity.this, MemoViewerActivity.class);
			intent.putExtra(MemoViewerActivity.INTENT_EXTRA_ID, -1L);
			startActivity(intent);
		}
	}
	// メモ追加
}
