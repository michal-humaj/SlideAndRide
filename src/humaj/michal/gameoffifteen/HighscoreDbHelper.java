package humaj.michal.gameoffifteen;

import humaj.michal.gameoffifteen.HighscoreContract.HighscoreEntry;
import humaj.michal.util.ImageUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HighscoreDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Highscore.db";
	public static final int NO_UPDATE = 33;
	public static final int INSERT = 44;
	public static final int UPDATE = 55;
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ HighscoreEntry.TABLE_NAME + " (" + HighscoreEntry._ID
			+ " INTEGER PRIMARY KEY,"
			+ HighscoreEntry.COLUMN_NAME_IS_GALLERY_PIC + INTEGER_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_PIC_RES_ID + INTEGER_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_PIC_FILENAME + TEXT_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_3x3_TIME + TEXT_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_4x4_TIME + TEXT_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_5x5_TIME + TEXT_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_6x6_TIME + TEXT_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_3x3_MOVES + INTEGER_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_4x4_MOVES + INTEGER_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_5x5_MOVES + INTEGER_TYPE
			+ COMMA_SEP + HighscoreEntry.COLUMN_NAME_6x6_MOVES + INTEGER_TYPE
			+ " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ HighscoreEntry.TABLE_NAME;

	public HighscoreDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public boolean writeHighscore(Intent intent, String solveTime,
			int movesCount) {
		int difficulty = intent.getIntExtra(ImageUtils.DIFFICULTY, -1);
		ContentValues values = new ContentValues();
		values.put("moves_" + difficulty, movesCount);
		values.put("time_" + difficulty, solveTime);
		SQLiteDatabase db = getReadableDatabase();
		String[] projection = { "moves_" + difficulty, "time_" + difficulty };
		Cursor c;
		String selection;
		String[] selectionArgs = new String[1];
		int choice = intent.getIntExtra(ImageUtils.PIC_TYPE, -1);
		if (choice == ImageUtils.PHONE_GALLERY) {
			String imagePath = intent.getStringExtra(ImageUtils.PICTURE);
			selection = HighscoreEntry.COLUMN_NAME_PIC_FILENAME + " LIKE ?";
			selectionArgs[0] = imagePath;
			values.put(HighscoreEntry.COLUMN_NAME_IS_GALLERY_PIC, 1);
			values.put(HighscoreEntry.COLUMN_NAME_PIC_FILENAME, imagePath);
		} else {
			int thumbID = intent.getIntExtra(ImageUtils.THUMBNAIL_ID, -1);
			selection = HighscoreEntry.COLUMN_NAME_PIC_RES_ID + " LIKE ?";
			selectionArgs[0] = String.valueOf(thumbID);
			values.put(HighscoreEntry.COLUMN_NAME_IS_GALLERY_PIC, 0);
			values.put(HighscoreEntry.COLUMN_NAME_PIC_RES_ID, thumbID);
		}
		c = db.query(HighscoreEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);

		if (c.getCount() == 0) {
			SQLiteDatabase insertDB = getWritableDatabase();
			insertDB.insert(HighscoreEntry.TABLE_NAME, null, values);
			insertDB.close();
			db.close();
			return true;
		}

		c.moveToFirst();
		int colIdex = c.getColumnIndex("moves_" + difficulty);
		int highscoreMoves = c.getInt(colIdex);
		if (movesCount < highscoreMoves || highscoreMoves == 0) {
			SQLiteDatabase updateDB = getReadableDatabase();
			updateDB.update(HighscoreEntry.TABLE_NAME, values, selection,
					selectionArgs);
			updateDB.close();
			db.close();
			return true;
		}
		db.close();
		return false;
	}
}
