package humaj.michal.activity;

import humaj.michal.R;
import humaj.michal.util.BitmapHolder;
import humaj.michal.util.ImageUtils;
import humaj.michal.util.TiledSquareImageView;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;

public class MainActivity extends Activity {

	private static final int PICS_UNLOCKED_ON_INSTALL = 50;
	private TiledSquareImageView mIV;
	private CheckBox checkBox3x3;
	private CheckBox checkBox4x4;
	private CheckBox checkBox5x5;
	private CheckBox checkBox6x6;

	Intent mIntent;

	private int mDifficulty;
	private int mWidth;
	private int mBorderWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIV = (TiledSquareImageView) findViewById(R.id.iv);
		checkBox3x3 = (CheckBox) findViewById(R.id.checkBox3x3);
		checkBox4x4 = (CheckBox) findViewById(R.id.checkBox4x4);
		checkBox5x5 = (CheckBox) findViewById(R.id.checkBox5x5);
		checkBox6x6 = (CheckBox) findViewById(R.id.checkBox6x6);
		setPicWidthAndTileBorderWidth();
		mIntent = getIntent();
		mDifficulty = mIntent.getIntExtra(ImageUtils.DIFFICULTY, -1);
		BitmapHolder bh = BitmapHolder.getInstance();
		Bitmap bitmap = ImageUtils.getBitmapFromIntent(mIntent, getResources(),
				mWidth, mDifficulty);
		if (bitmap == null) {
			Random random = new Random(System.currentTimeMillis());
			mDifficulty = random.nextInt(4) + 3;
			int randomIndex = random.nextInt(ImageUtils.mPictureIDs.length);
			mIntent.putExtra(ImageUtils.PIC_TYPE, ImageUtils.DEFAULT_PICTURE);
			mIntent.putExtra(ImageUtils.PICTURE, randomIndex);
			mIntent.putExtra(ImageUtils.DIFFICULTY, mDifficulty);
			bitmap = ImageUtils.decodeSampledBitmapFromResource(getResources(),
					ImageUtils.mPictureIDs[randomIndex], mWidth);
			mIntent.putExtra(ImageUtils.THUMBNAIL_ID,
					ImageUtils.pictureThumbIDs[randomIndex]);
		}
		bh.setBitmap(bitmap);
		checkRightCheckBox();
		mIV.setImageBitmap(bitmap);
		mIV.setDifficulty(mDifficulty);
		mIV.setBorderWidth(mBorderWidth);

		SharedPreferences preferences = getSharedPreferences(
				ChoosePictureActivity.PREFS_NAME, 0);
		int picsUnlocked = preferences.getInt(
				ChoosePictureActivity.PICS_UNLOCKED, -1);
		if (picsUnlocked == -1) {
			Editor editor = preferences.edit();
			editor.putInt(ChoosePictureActivity.PICS_UNLOCKED,
					PICS_UNLOCKED_ON_INSTALL);
			editor.commit();
		}

		WebView webView = (WebView) findViewById(R.id.leadboltAd);
		if (webView != null) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setBackgroundColor(Color.TRANSPARENT);
			String html = "<html><body style='margin:0;padding:0;'><script type='text/javascript' src='http://ad.leadboltads.net/show_app_ad.js?section_id=252524115'></script></body></html>";
			webView.loadData(html, "text/html", "utf-8");
		}

	}

	@Override
	protected void onDestroy() {
		BitmapHolder.getInstance().getBitmap().recycle();
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		mIntent = intent;
		BitmapHolder bh = BitmapHolder.getInstance();
		bh.getBitmap().recycle();
		Bitmap bitmap = ImageUtils.getBitmapFromIntent(mIntent, getResources(),
				mWidth, mDifficulty);
		mIV.setImageBitmap(bitmap);
		bh.setBitmap(bitmap);
		super.onNewIntent(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
		mIV = (TiledSquareImageView) findViewById(R.id.iv);
		checkBox3x3 = (CheckBox) findViewById(R.id.checkBox3x3);
		checkBox4x4 = (CheckBox) findViewById(R.id.checkBox4x4);
		checkBox5x5 = (CheckBox) findViewById(R.id.checkBox5x5);
		checkBox6x6 = (CheckBox) findViewById(R.id.checkBox6x6);
		checkRightCheckBox();
		mIV.setImageBitmap(BitmapHolder.getInstance().getBitmap());
		mIV.setDifficulty(mDifficulty);
		mIV.setBorderWidth(mBorderWidth);
		WebView webView = (WebView) findViewById(R.id.leadboltAd);
		if (webView != null) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setBackgroundColor(Color.TRANSPARENT);
			String html = "<html><body style='margin:0;padding:0;'><script type='text/javascript' src='http://ad.leadboltads.net/show_app_ad.js?section_id=252524115'></script></body></html>";
			webView.loadData(html, "text/html", "utf-8");
		}
	}

	public void onChoosePicture(View v) {
		Intent intent = new Intent(this, ChoosePictureActivity.class);
		intent.putExtra(ImageUtils.DIFFICULTY, mDifficulty);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	public void onPlay(View v) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(ImageUtils.DIFFICULTY, mDifficulty);
		intent.putExtra(ImageUtils.WIDTH, mWidth);
		intent.putExtra(ImageUtils.BORDER_WIDTH, mBorderWidth);
		int choice = mIntent.getIntExtra(ImageUtils.PIC_TYPE, -1);
		intent.putExtra(ImageUtils.PIC_TYPE, choice);
		if (choice == ImageUtils.PHONE_GALLERY) {
			intent.putExtra(ImageUtils.PICTURE,
					mIntent.getStringExtra(ImageUtils.PICTURE));
		} else {
			intent.putExtra(ImageUtils.THUMBNAIL_ID,
					mIntent.getIntExtra(ImageUtils.THUMBNAIL_ID, -1));			
		}
		startActivity(intent);
	}

	public void onHighscore(View v) {
		Intent intent = new Intent(this, HighscoreActivity.class);
		startActivity(intent);
	}

	public void onDiffChanged(View v) {
		checkBox3x3.setChecked(false);
		checkBox4x4.setChecked(false);
		checkBox5x5.setChecked(false);
		checkBox6x6.setChecked(false);
		((CheckBox) v).setChecked(true);

		if (v == checkBox3x3) {
			mDifficulty = 3;
			mIV.setDifficulty(3);
		} else if (v == checkBox4x4) {
			mDifficulty = 4;
			mIV.setDifficulty(4);
		} else if (v == checkBox5x5) {
			mDifficulty = 5;
			mIV.setDifficulty(5);
		} else if (v == checkBox6x6) {
			mDifficulty = 6;
			mIV.setDifficulty(6);
		}

		int choice = mIntent.getIntExtra(ImageUtils.PIC_TYPE, -1);
		if (choice == ImageUtils.SYMBOL) {
			int position = mIntent.getIntExtra(ImageUtils.PICTURE, -1);
			BitmapHolder bh = BitmapHolder.getInstance();
			bh.getBitmap().recycle();
			Bitmap bitmap = ImageUtils.decodeSampledBitmapFromResource(
					getResources(),
					ImageUtils.mSymbolsIDs[mDifficulty - 3][position], mWidth);
			bh.setBitmap(bitmap);
			mIV.setImageBitmap(bitmap);
		}
	}

	private void checkRightCheckBox() {
		switch (mDifficulty) {
		case 3:
			checkBox3x3.setChecked(true);
			break;
		case 4:
			checkBox4x4.setChecked(true);
			break;
		case 5:
			checkBox5x5.setChecked(true);
			break;
		case 6:
			checkBox6x6.setChecked(true);
			break;
		}
	}

	private void setPicWidthAndTileBorderWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		mWidth = height < width ? height : width;
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			mBorderWidth = 1;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			mBorderWidth = 1;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			mBorderWidth = 2;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			mBorderWidth = 2;
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			mBorderWidth = 3;
			break;
		}
	}
}
