package humaj.michal.util;

import android.graphics.Bitmap;

public class BitmapHolder {

	private static BitmapHolder instance;

	private Bitmap bitmap;

	private BitmapHolder() {
	}

	public void setBitmap(Bitmap b) {
		this.bitmap = b;
	}

	public Bitmap getBitmap() {
		return this.bitmap;
	}

	public static synchronized BitmapHolder getInstance() {
		if (instance == null) {
			instance = new BitmapHolder();
		}
		return instance;
	}
}
