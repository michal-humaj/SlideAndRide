package humaj.michal.activity;

import humaj.michal.R;
import humaj.michal.gameoffifteen.HighscoreContract.HighscoreEntry;
import humaj.michal.gameoffifteen.HighscoreDbHelper;
import humaj.michal.gameoffifteen.SurfaceRenderer;
import humaj.michal.uilogic.BackDialog;
import humaj.michal.uilogic.BackDialog.BackDialogListener;
import humaj.michal.uilogic.PauseDialog;
import humaj.michal.uilogic.WinDialog;
import humaj.michal.util.BitmapHolder;
import humaj.michal.util.ImageUtils;
import humaj.michal.util.SquareGameSurfaceView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends FragmentActivity implements OnTouchListener,
		BackDialogListener {

	private SquareGameSurfaceView mGameSurfaceView;
	private SurfaceRenderer mSurfaceRenderer;
	private ImageButton mBtnPreview;
	private boolean mDialogPaused = false;
	private boolean mQuitPressed = false;
	private int mDifficulty;
	private String mMovesBest;
	private String mTimeBest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		Intent intent = getIntent();
		mDifficulty = intent.getIntExtra(ImageUtils.DIFFICULTY, -1);
		Object config = getLastCustomNonConfigurationInstance();
		getHighscore();
		if (config != null) {
			mSurfaceRenderer = (SurfaceRenderer) config;
			mSurfaceRenderer.setTvMoves((TextView) findViewById(R.id.tvMoves));
			mSurfaceRenderer.setTimeHandler(new TimeHandler(Looper
					.getMainLooper(), (TextView) findViewById(R.id.tvTime),
					mTimeBest));
		} else {
			int borderWidth = intent.getIntExtra(ImageUtils.BORDER_WIDTH, -1);
			int surfaceWidth = intent.getIntExtra(ImageUtils.WIDTH, -1);
			mSurfaceRenderer = new SurfaceRenderer(
					(TextView) findViewById(R.id.tvMoves), mDifficulty,
					surfaceWidth, borderWidth, new TimeHandler(
							Looper.getMainLooper(),
							(TextView) findViewById(R.id.tvTime), mTimeBest));

			mSurfaceRenderer.setMovesBest(mMovesBest);
		}
		mSurfaceRenderer.getTvMoves().setText(
				mSurfaceRenderer.getMovesCount() + mMovesBest);
		Bitmap bitmap = BitmapHolder.getInstance().getBitmap();
		mGameSurfaceView = (SquareGameSurfaceView) findViewById(R.id.gameSurfaceView);
		mGameSurfaceView.setSurfaceRenderer(mSurfaceRenderer);
		mGameSurfaceView.setOnTouchListener(this);
		ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
		ivPreview.setImageBitmap(bitmap);
		PreviewListener previewListener = new PreviewListener();
		ivPreview.setOnTouchListener(previewListener);
		mBtnPreview = (ImageButton) findViewById(R.id.btnPreview);
		mBtnPreview.setOnTouchListener(previewListener);
		if (config == null)
			mSurfaceRenderer.shuffleTiles();

		WebView webView = (WebView) findViewById(R.id.leadboltAd);
		if (webView != null) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setBackgroundColor(Color.TRANSPARENT);
			String html = "<html><body style='margin:0;padding:0;'><script type='text/javascript' src='http://ad.leadboltads.net/show_app_ad.js?section_id=252524115'></script></body></html>";
			webView.loadData(html, "text/html", "utf-8");
		}
	}

	class PreviewListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView btn = (ImageView) v;
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mSurfaceRenderer.previewOn();
				if (btn != mBtnPreview)
					break;
				btn.setBackgroundResource(R.drawable.repeat_button_background_pressed);
				btn.setImageResource(R.drawable.ic_eye_pressed);
				break;
			case MotionEvent.ACTION_UP:
				mSurfaceRenderer.previewOff();
				if (btn != mBtnPreview)
					break;
				btn.setBackgroundResource(R.drawable.repeat_button_background);
				btn.setImageResource(R.drawable.ic_eye);
				break;
			}
			return true;
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return mSurfaceRenderer;
	}

	@Override
	protected void onPause() {
		mSurfaceRenderer.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mDialogPaused) {
			mSurfaceRenderer.resume();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent me) {
		if (mSurfaceRenderer.isSolved())
			return true;
		mSurfaceRenderer.onTouch(me);
		if (mSurfaceRenderer.isSolved()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean isNewHighscore = writeToDatabaseIfHighscore(
					mSurfaceRenderer.getSolveTime(),
					mSurfaceRenderer.getMovesCount());

			if (isNewHighscore) {
				SharedPreferences preferences = getSharedPreferences(
						ChoosePictureActivity.PREFS_NAME, 0);
				int picsUnlocked = preferences.getInt(
						ChoosePictureActivity.PICS_UNLOCKED, -1);
				Editor editor = preferences.edit();
				editor.putInt(ChoosePictureActivity.PICS_UNLOCKED,
						++picsUnlocked);
				editor.commit();
			}

			WinDialog dialog = WinDialog.newInstance(
					mSurfaceRenderer.getSolveTime(),
					mSurfaceRenderer.getMovesCount(), isNewHighscore);
			dialog.show(getSupportFragmentManager(), "WinDialogFragment");
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (!mSurfaceRenderer.isSolved()) {
			mDialogPaused = true;
			mSurfaceRenderer.pause();
			BackDialog dialog = new BackDialog();
			dialog.show(getSupportFragmentManager(), "BackDialogFragment");
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		mQuitPressed = true;
		super.onBackPressed();
	}

	public void onDialogDismiss() {
		if (!mQuitPressed)
			mSurfaceRenderer.resume();		
		mDialogPaused = false;
	}

	public void onToggleEmptySeen(View v) {
		mSurfaceRenderer.toggleEmptySeen();
	}

	public void a(View v) {

	}

	public void onPausePressed(View v) {
		mDialogPaused = true;
		mSurfaceRenderer.pause();
		PauseDialog dialog = new PauseDialog();
		dialog.show(getSupportFragmentManager(), "PauseDialogFragment");
	}

	public static class TimeHandler extends Handler {

		TextView tvTime;
		String timeBest;

		public TimeHandler(Looper mainLooper, TextView tv, String t) {
			super(mainLooper);
			tvTime = tv;
			timeBest = t;
		}

		@Override
		public void handleMessage(Message message) {
			String time = (String) message.obj;
			tvTime.setText(time + timeBest);
		}
	}

	private boolean writeToDatabaseIfHighscore(String solveTime, int movesCount) {
		HighscoreDbHelper dbHelper = new HighscoreDbHelper(this);
		return dbHelper.writeHighscore(getIntent(), solveTime, movesCount);
	}

	private void getHighscore() {
		HighscoreDbHelper dbHelper = new HighscoreDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = { "moves_" + mDifficulty, "time_" + mDifficulty };
		Cursor c;
		String selection;
		String[] selectionArgs = new String[1];
		Intent intent = getIntent();
		int choice = intent.getIntExtra(ImageUtils.PIC_TYPE, -1);
		if (choice == ImageUtils.PHONE_GALLERY) {
			String imagePath = intent.getStringExtra(ImageUtils.PICTURE);
			selection = HighscoreEntry.COLUMN_NAME_PIC_FILENAME + " LIKE ?";
			selectionArgs[0] = imagePath;
		} else {
			int thumbID = intent.getIntExtra(ImageUtils.THUMBNAIL_ID, -1);
			selection = HighscoreEntry.COLUMN_NAME_PIC_RES_ID + " LIKE ?";
			selectionArgs[0] = String.valueOf(thumbID);
		}
		c = db.query(HighscoreEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
		mTimeBest = "";
		mMovesBest = "";
		if (c.getCount() != 0) {
			c.moveToFirst();
			int columnIndex = c.getColumnIndex("moves_" + mDifficulty);
			if (c.getInt(columnIndex) != 0) {
				mMovesBest = "  Best: " + c.getInt(columnIndex);
			}
			columnIndex = c.getColumnIndex("time_" + mDifficulty);
			if (c.getString(columnIndex) != null) {
				mTimeBest = "  Best: " + c.getString(columnIndex);
			}
		}
		db.close();
	}
}
