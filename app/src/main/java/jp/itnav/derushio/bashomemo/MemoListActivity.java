package jp.itnav.derushio.bashomemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import jp.itnav.derushio.bashomemo.adapter.MemoCardAdapter;
import jp.itnav.derushio.bashomemo.adapter.MemoDataSet;
import jp.itnav.derushio.bashomemo.database.MemoDataBaseHelper;
import jp.itnav.derushio.bashomemo.database.MemoDataBaseManager;


public class MemoListActivity extends ActionBarActivity {

	private Toolbar toolbar;

	private MemoDataBaseManager memoDataBaseManager;

	private RecyclerView memoList;
	private ArrayList<MemoDataSet> memoDataSet;
	private MemoCardAdapter memoCardAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_list);
		initializeToolbar();

		// レイアウト情報をLinearLayoutに設定
		memoList = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		memoList.setLayoutManager(linearLayoutManager);
		// レイアウト情報をLinearLayoutに設定

		// レイアウト情報を設定
		memoList.setHasFixedSize(true);
		memoList.setItemAnimator(new DefaultItemAnimator());
		// レイアウト情報を設定

		// メモのデータベースを読み込み
		memoDataBaseManager = new MemoDataBaseManager(this);
		// メモのデータベースを読み込み

		// メモのデータ情報を定義
		memoDataSet = new ArrayList<>();
		memoCardAdapter = new MemoCardAdapter(this, memoDataSet);
		memoList.setAdapter(memoCardAdapter);
		// メモのデータ情報を定義

		// 全てのデータをロード
		Cursor cursor = memoDataBaseManager.getAllDataCursor(MemoDataBaseHelper.ITEM_ID);
		cursor.moveToFirst();
		// 全てのデータをロード

		// データをパース
		for (int i = 0; i < cursor.getCount(); i++) {
			// サムネイルがある場合は読み込む
			Uri uri = null;

			if (cursor.getString(memoDataBaseManager.INDEX_PICTURE_URI) != null) {
				uri = Uri.parse(cursor.getString(memoDataBaseManager.INDEX_PICTURE_URI));
			}
			// サムネイルがある場合は読み込む

			final Long id = cursor.getLong(memoDataBaseManager.INDEX_ID);
			// idを読み込み

			// データセットに読み込んだ情報を追加
			memoDataSet.add(new MemoDataSet(cursor.getString(memoDataBaseManager.INDEX_TITLE), uri,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MemoListActivity.this, MemoViewerActivity.class);
							intent.putExtra(MemoViewerActivity.INTENT_EXTRA_ID, id);
							startActivity(intent);
						}
					},
					new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							memoDataBaseManager.deleteMemoData(id);
							Intent intent = new Intent(MemoListActivity.this, MemoListActivity.class);
							startActivity(intent);
							finish();
							return false;
						}
					}));
			// データセットに読み込んだ情報を追加

			cursor.moveToNext();
			// 次のデータ
		}

		memoCardAdapter.notifyDataSetChanged();
		// データをパース
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initializeToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar == null) {
			throw new IllegalStateException("Layout is required to include a Toolbar with id " +
					"'toolbar'");
		}

		toolbar.setTitle("場所メモったーよ！！");
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.inflateMenu(R.menu.menu_memo_list);
	}

	public void addMemo(View v) {
		Intent intent = new Intent(MemoListActivity.this, MemoViewerActivity.class);
		intent.putExtra(MemoViewerActivity.INTENT_EXTRA_ID, -1L);
		startActivity(intent);
	}
}
