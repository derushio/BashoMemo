package jp.itnav.derushio.bashomemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by derushio on 15/01/13.
 */
public class MemoDataBaseManager {
	private SQLiteDatabase mMemoDataBase;
	private String mTableName = "memoDataBase";

	public int INDEX_ID;
	public int INDEX_DATE;
	public int INDEX_TITLE;
	public int INDEX_LATLNG;
	public int INDEX_PICTURE_URI;
	public int INDEX_MEMO;

	public MemoDataBaseManager(Context context) {
		mMemoDataBase = new MemoDataBaseHelper(context, mTableName, 1).getWritableDatabase();

		Cursor cursor = getAllDataCursor(MemoDataBaseHelper.ITEM_ID);
		cursor.moveToFirst();

		INDEX_ID = cursor.getColumnIndex(MemoDataBaseHelper.ITEM_ID.NAME);
		INDEX_DATE = cursor.getColumnIndex(MemoDataBaseHelper.ITEM_DATE.NAME);
		INDEX_TITLE = cursor.getColumnIndex(MemoDataBaseHelper.ITEM_TITLE.NAME);
		INDEX_LATLNG = cursor.getColumnIndex(MemoDataBaseHelper.ITEM_LATLNG.NAME);
		INDEX_PICTURE_URI = cursor.getColumnIndex(MemoDataBaseHelper.ITEM_PICTURE_URI.NAME);
		INDEX_MEMO = cursor.getColumnIndex(MemoDataBaseHelper.ITEM_MEMO.NAME);

		cursor.close();
	}

	public Cursor getAllDataCursor(Item orderByItem) {
		String orderBy = orderByItem.NAME + " ASC";
		return mMemoDataBase.query(mTableName, null, null, null, null, null, orderBy);
	}

	public long addMemoData(String title) {
		return addMemoData(title, null, null, "");
	}

	public long addMemoData(String title, LatLng latLng, Uri pictureUri, String memo) {
		ContentValues contentValues = new ContentValues();

		Time time = new Time(Time.getCurrentTimezone());
		time.setToNow();

		contentValues.put(MemoDataBaseHelper.ITEM_DATE.NAME, timeStatementGenerator(time));
		contentValues.put(MemoDataBaseHelper.ITEM_TITLE.NAME, title);
		if (latLng != null) {
			contentValues.put(MemoDataBaseHelper.ITEM_LATLNG.NAME, latLngStatementGenerator(latLng));
		}

		if (pictureUri != null) {
			contentValues.put(MemoDataBaseHelper.ITEM_PICTURE_URI.NAME, pictureUri.getPath());
		}
		contentValues.put(MemoDataBaseHelper.ITEM_MEMO.NAME, memo);

		return mMemoDataBase.insert(mTableName, "", contentValues);
	}

	public void deleteMemoData(long id) {
		String whereCause = MemoDataBaseHelper.ITEM_ID.NAME + "==?";
		String whereCauseArgs[] = {("" + id)};
		mMemoDataBase.delete(mTableName, whereCause, whereCauseArgs);
	}

	public void updateMemoData(long id, String title, LatLng latLng, Uri pictureUri, String memo) {
		ContentValues contentValues = new ContentValues();

		Time time = new Time(Time.getCurrentTimezone());
		time.setToNow();

		contentValues.put(MemoDataBaseHelper.ITEM_DATE.NAME, timeStatementGenerator(time));
		contentValues.put(MemoDataBaseHelper.ITEM_TITLE.NAME, title);

		if (latLng != null) {
			contentValues.put(MemoDataBaseHelper.ITEM_LATLNG.NAME, latLngStatementGenerator(latLng));
		}

		if (pictureUri != null) {
			contentValues.put(MemoDataBaseHelper.ITEM_PICTURE_URI.NAME, pictureUri.toString());
		}

		if (memo != null) {
			contentValues.put(MemoDataBaseHelper.ITEM_MEMO.NAME, memo);
		}

		String whereCause = MemoDataBaseHelper.ITEM_ID.NAME + "==?";
		String whereCauseArgs[] = {("" + id)};

		mMemoDataBase.update(mTableName, contentValues, whereCause, whereCauseArgs);
	}

	public Cursor findMemoDataById(long id) {
		String selection = MemoDataBaseHelper.ITEM_ID.NAME + "==?";
		String selectionArgs[] = {("" + id)};
		return mMemoDataBase.query(mTableName, null, selection, selectionArgs, null, null, null);
	}

	public String findTitleById(long id) {
		Cursor cursor = findMemoDataById(id);
		cursor.moveToFirst();

		return cursor.getString(INDEX_TITLE);
	}

	public Uri findPictureUriById(long id) {
		Cursor cursor = findMemoDataById(id);
		cursor.moveToFirst();

		String uriStatement = cursor.getString(INDEX_PICTURE_URI);
		if (uriStatement == null) {
			return null;
		}

		return Uri.parse(uriStatement);
	}

	public String findMemoById(long id) {
		Cursor cursor = findMemoDataById(id);
		cursor.moveToFirst();

		return cursor.getString(INDEX_MEMO);
	}

	public LatLng findLatLngById(long id) {
		Cursor cursor = findMemoDataById(id);
		cursor.moveToFirst();

		String latLngStatement = cursor.getString(INDEX_LATLNG);

		if (latLngStatement != null) {
			float lat = Float.valueOf(latLngStatement.substring(0, latLngStatement.indexOf(",") - 1));
			float lng = Float.valueOf(latLngStatement.substring(latLngStatement.indexOf(",") + 1, latLngStatement.length()));
			Log.d("glog", "" + lat + lng);
			return new LatLng(lat, lng);
		}

		return null;
	}

	private String timeStatementGenerator(Time time) {
		return time.year + "年" + (time.month + 1) + "月" +
				time.monthDay + "日" + time.weekDay + "曜日[" + time.hour + ":" + time.minute + "]";
	}

	private String latLngStatementGenerator(LatLng latLng) {
		return latLng.latitude + "," + latLng.longitude;
	}
}
