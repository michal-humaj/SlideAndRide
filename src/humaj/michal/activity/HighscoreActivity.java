package humaj.michal.activity;

import humaj.michal.R;
import humaj.michal.gameoffifteen.HighscoreContract.HighscoreEntry;
import humaj.michal.gameoffifteen.HighscoreDbHelper;
import humaj.michal.uilogic.HighscoreRowWrapper;
import humaj.michal.util.ImageUtils;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

public class HighscoreActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final int HIGHSCORE_LOADER = 55;
	private static final int THUMB_WIDTH_IN_DP = 110;

	private HighscoreAdapter mAdapter;	
	private int mThumbWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
		setThumbWidth();
		getSupportLoaderManager().initLoader(HIGHSCORE_LOADER, null, this);
		ListView listView = (ListView) findViewById(R.id.listViewHighscore);
		mAdapter = new HighscoreAdapter(this, null, 0);
		listView.setAdapter(mAdapter);
	}

	class HighscoreAdapter extends CursorAdapter {

		public HighscoreAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			HighscoreRowWrapper wrapper = (HighscoreRowWrapper) row.getTag();
			int colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_IS_GALLERY_PIC);
			int isGalleryPic = cursor.getInt(colIndex);
			if (isGalleryPic == 1) {
				colIndex = cursor
						.getColumnIndex(HighscoreEntry.COLUMN_NAME_PIC_FILENAME);
				String imagePath = cursor.getString(colIndex);				
				Bitmap b = ImageUtils.decodeSampledBitmapFromFile(imagePath,
						mThumbWidth);
				if (b != null) {
					wrapper.getIv().setImageBitmap(b);
				}
			} else {
				colIndex = cursor
						.getColumnIndex(HighscoreEntry.COLUMN_NAME_PIC_RES_ID);
				int thumbID = cursor.getInt(colIndex);
				wrapper.getIv().setImageResource(thumbID);
			}
			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_3x3_TIME);
			String time = cursor.getString(colIndex);
			wrapper.getTvTime3x3().setText(time);

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_4x4_TIME);
			time = cursor.getString(colIndex);
			wrapper.getTvTime4x4().setText(time);

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_5x5_TIME);
			time = cursor.getString(colIndex);
			wrapper.getTvTime5x5().setText(time);

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_6x6_TIME);
			time = cursor.getString(colIndex);
			wrapper.getTvTime6x6().setText(time);

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_3x3_MOVES);
			int moves = cursor.getInt(colIndex);
			wrapper.getTvMoves3x3().setText(moves == 0 ? "" : moves + "");

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_4x4_MOVES);
			moves = cursor.getInt(colIndex);
			wrapper.getTvMoves4x4().setText(moves == 0 ? "" : moves + "");

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_5x5_MOVES);
			moves = cursor.getInt(colIndex);
			wrapper.getTvMoves5x5().setText(moves == 0 ? "" : moves + "");

			colIndex = cursor
					.getColumnIndex(HighscoreEntry.COLUMN_NAME_6x6_MOVES);
			moves = cursor.getInt(colIndex);
			wrapper.getTvMoves6x6().setText(moves == 0 ? "" : moves + "");

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.highscore_row, null);
			HighscoreRowWrapper wrapper = new HighscoreRowWrapper(row);
			row.setTag(wrapper);
			return row;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
		switch (loaderID) {
		case HIGHSCORE_LOADER:
			HighscoreDbHelper dbHelper = new HighscoreDbHelper(this);
			String rawQuery = "SELECT * FROM " + HighscoreEntry.TABLE_NAME;
			SQLiteCursorLoader loader = new SQLiteCursorLoader(
					getApplicationContext(), dbHelper, rawQuery, null);

			return loader;
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.changeCursor(cursor);		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);
		arg0.reset();
	}

	private void setThumbWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			mThumbWidth = (int) (0.75 * THUMB_WIDTH_IN_DP);
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			mThumbWidth = (int) (1.0 * THUMB_WIDTH_IN_DP);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			mThumbWidth = (int) (1.5 * THUMB_WIDTH_IN_DP);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			mThumbWidth = (int) (2.0 * THUMB_WIDTH_IN_DP);
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			mThumbWidth = (int) (3.0 * THUMB_WIDTH_IN_DP);
			break;
		}
	}
}
