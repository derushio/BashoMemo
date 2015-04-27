package jp.itnav.derushio.bashomemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by derushio on 15/01/13.
 */
public class MemoDataBaseHelper extends SQLiteOpenHelper {
	public String TABLE_NAME;

	public static final Item ITEM_ID = new Item("item_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
	public static final Item ITEM_DATE = new Item("date", "TEXT");
	public static final Item ITEM_TITLE = new Item("title", "TEXT");
	public static final Item ITEM_LATLNG = new Item("latlng", "TEXT");
	public static final Item ITEM_PICTURE_URI = new Item("picture_uri", "TEXT");
	public static final Item ITEM_MEMO = new Item("memo", "TEXT");

	public static final Item[] ITEMS = {ITEM_ID, ITEM_DATE, ITEM_TITLE, ITEM_LATLNG, ITEM_PICTURE_URI, ITEM_MEMO};

	public MemoDataBaseHelper(Context context, String tableName, int version) {
		super(context, tableName, null, version);
		this.TABLE_NAME = tableName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreateTableStatement());
		Log.d("CREATE TABLE", TABLE_NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private String getCreateTableStatement() {
		String createTableStatement = "CREATE TABLE " + TABLE_NAME + "(";
		for (int i = 0; i < ITEMS.length; i++) {
			if (i != 0) {
				createTableStatement += ",";
			}
			createTableStatement += ITEMS[i].NAME + " " + ITEMS[i].TYPE;
		}
		createTableStatement += ");";

		return createTableStatement;
	}
}
