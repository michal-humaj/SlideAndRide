package humaj.michal.activity;

import humaj.michal.R;
import humaj.michal.uilogic.GalleryLoader;
import humaj.michal.uilogic.GalleryMessageObject;
import humaj.michal.uilogic.PictureLoader;
import humaj.michal.util.ImageUtils;
import humaj.michal.util.SquareImageView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ChoosePictureActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final String PICS_UNLOCKED = "PICS_UNLOCKED";
	public static final String PREFS_NAME = "prefs";
	public static final int CURSOR_LOADER = 0;
	public static final int THUMB_WIDTH_IN_DP = 105;

	public static int mPicsUnlocked;

	private int mDifficulty;

	private ThumbnailHandler mHandler;

	public static int mThumbWidth;
	private Bitmap mPlaceHolderBitmap = null;

	private Cursor mCursor;
	private int mDataColumnIndex;

	private GalleryAdapter mGalleryAdapter;
	private GalleryLoader mGalleryLoader;
	private PictureLoader mPictureLoader;

	private LruCache<String, Bitmap> mGalleryCache;
	private GridView mGvPictures;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_picture);
		mDifficulty = getIntent().getIntExtra(ImageUtils.DIFFICULTY, -1);
		mHandler = new ThumbnailHandler(Looper.getMainLooper());
		getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this);
		setupTabs();
		setThumbWidth();
		mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_placeholder);
		setupCache();
		setupGridViews();
	}

	@Override
	protected void onStop() {
		mGalleryLoader.kill();
		mPictureLoader.kill();
		super.onStop();
	}

	@Override
	protected void onStart() {
		mGalleryLoader = new GalleryLoader(this, mHandler, mThumbWidth);
		mPictureLoader = new PictureLoader(getResources(), mHandler);
		mGalleryLoader.start();
		mPictureLoader.start();
		super.onStart();
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
		mPicsUnlocked = preferences.getInt(PICS_UNLOCKED, -1);
		mGvPictures.setAdapter(new PictureAdapter());
	}

	public static class ThumbnailHandler extends Handler {

		public ThumbnailHandler(Looper mainLooper) {
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message message) {
			GalleryMessageObject holder = (GalleryMessageObject) message.obj;
			holder.imageView.setImageBitmap(holder.bitmap);
		}
	}

	class PictureAdapter extends BaseAdapter {

		public final Integer[] thumbIDs = { R.drawable.t001, R.drawable.t002,
				R.drawable.t003, R.drawable.t004, R.drawable.t005,
				R.drawable.t006, R.drawable.t007, R.drawable.t008,
				R.drawable.t009, R.drawable.t010, R.drawable.t011,
				R.drawable.t012, R.drawable.t013, R.drawable.t014,
				R.drawable.t015, R.drawable.t016, R.drawable.t017,
				R.drawable.t018, R.drawable.t019, R.drawable.t020 };

		public PictureAdapter() {
		}

		@Override
		public int getCount() {
			return mPicsUnlocked < thumbIDs.length ? mPicsUnlocked
					: thumbIDs.length;
		}

		@Override
		public Object getItem(int position) {
			return thumbIDs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			SquareImageView imageView;
			if (convertView == null) {
				imageView = new SquareImageView(getApplicationContext());
				imageView.setScaleType(SquareImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (SquareImageView) convertView;
			}
			imageView.setImageBitmap(mPlaceHolderBitmap);
			mPictureLoader.add(imageView, thumbIDs[position]);
			synchronized (mPictureLoader) {
				mPictureLoader.notify();
			}
			return imageView;
		}
	}

	class GalleryAdapter extends CursorAdapter {

		public GalleryAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			SquareImageView imageView = (SquareImageView) view;
			String fileName = cursor.getString(mDataColumnIndex);
			Bitmap bitmap = getBitmapFromMemCache(fileName);
			if (bitmap == null) {
				imageView.setImageBitmap(mPlaceHolderBitmap);
				mGalleryLoader.add(imageView, fileName);
				synchronized (mGalleryLoader) {
					mGalleryLoader.notify();
				}
			} else {
				imageView.setImageBitmap(bitmap);
				mGalleryLoader.remove(imageView);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			SquareImageView imageView = new SquareImageView(context);
			imageView.setScaleType(SquareImageView.ScaleType.CENTER_CROP);
			return imageView;
		}
	}

	class SymbolAdapter extends BaseAdapter {

		private final Integer[] thumbIDs = { R.drawable.st01, R.drawable.st02,
				R.drawable.st03 };

		public SymbolAdapter() {
		}

		@Override
		public int getCount() {
			return thumbIDs.length;
		}

		@Override
		public Object getItem(int position) {
			return thumbIDs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			SquareImageView imageView;
			if (convertView == null) {
				imageView = new SquareImageView(getApplicationContext());
				imageView.setScaleType(SquareImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (SquareImageView) convertView;
			}
			imageView.setImageResource(thumbIDs[position]);
			return imageView;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		switch (loaderID) {
		case CURSOR_LOADER:
			String[] projection = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA };
			return new CursorLoader(this,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					null, null, null);
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mDataColumnIndex = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		mGalleryAdapter.changeCursor(cursor);
		mCursor = cursor;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mGalleryAdapter.changeCursor(null);
	}

	public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mGalleryCache.put(key, bitmap);
		}
	}

	public synchronized Bitmap getBitmapFromMemCache(String key) {
		return mGalleryCache.get(key);
	}

	private void setupTabs() {
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();

		TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.gvDefaultPictures);
		spec.setIndicator(getString(R.string.tabDefaultPictures));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.gvPhoneGallery);
		spec.setIndicator(getString(R.string.tabPhoneGallery));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tag3");
		spec.setContent(R.id.gvSymbols);
		spec.setIndicator(getString(R.string.tabSymbols));
		tabs.addTab(spec);
	}

	private void setupGridViews() {
		mGvPictures = (GridView) findViewById(R.id.gvDefaultPictures);
		GridView gvGallery = (GridView) findViewById(R.id.gvPhoneGallery);
		GridView gvSymbols = (GridView) findViewById(R.id.gvSymbols);
		mGvPictures.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra(ImageUtils.PIC_TYPE, ImageUtils.DEFAULT_PICTURE);
				intent.putExtra(ImageUtils.THUMBNAIL_ID,
						ImageUtils.pictureThumbIDs[position]);
				intent.putExtra(ImageUtils.PICTURE, position);
				intent.putExtra(ImageUtils.DIFFICULTY, mDifficulty);
				startActivity(intent);
			}
		});
		gvGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra(ImageUtils.PIC_TYPE, ImageUtils.PHONE_GALLERY);
				intent.putExtra(ImageUtils.PICTURE,
						mCursor.getString(mDataColumnIndex));
				intent.putExtra(ImageUtils.DIFFICULTY, mDifficulty);
				startActivity(intent);
			}
		});
		gvSymbols.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra(ImageUtils.PIC_TYPE, ImageUtils.SYMBOL);
				intent.putExtra(ImageUtils.THUMBNAIL_ID,
						ImageUtils.symbolThumbIDs[position]);
				intent.putExtra(ImageUtils.PICTURE, position);
				intent.putExtra(ImageUtils.DIFFICULTY, mDifficulty);
				startActivity(intent);
			}
		});
		mGalleryAdapter = new GalleryAdapter(this, null, 0);
		gvGallery.setAdapter(mGalleryAdapter);
		mGvPictures.setAdapter(new PictureAdapter());
		gvSymbols.setAdapter(new SymbolAdapter());
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

	private void setupCache() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		mGalleryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				long byteCount = bitmap.getRowBytes() * bitmap.getHeight();
				return (int) (byteCount / 1024);
			}
		};
	}
}
