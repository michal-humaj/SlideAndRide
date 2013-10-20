package humaj.michal.util;

import humaj.michal.R;

import java.io.IOException;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImageUtils {

	public static final Integer[] mPictureIDs = { R.drawable.p001,
			R.drawable.p002, R.drawable.p003, R.drawable.p004, R.drawable.p005,
			R.drawable.p006, R.drawable.p007, R.drawable.p008, R.drawable.p009,
			R.drawable.p010, R.drawable.p011, R.drawable.p012, R.drawable.p013,
			R.drawable.p014, R.drawable.p015, R.drawable.p016, R.drawable.p017,
			R.drawable.p018, R.drawable.p019, R.drawable.p020 };

	public static final Integer[][] mSymbolsIDs = {
			{ R.drawable.s01d3, R.drawable.s02d3, R.drawable.s03d3 },
			{ R.drawable.s01d4, R.drawable.s02d4, R.drawable.s03d4 },
			{ R.drawable.s01d5, R.drawable.s02d5, R.drawable.s03d5 },
			{ R.drawable.s01d6, R.drawable.s02d6, R.drawable.s03d6 } };

	public static final Integer[] pictureThumbIDs = { R.drawable.t001,
			R.drawable.t002, R.drawable.t003, R.drawable.t004, R.drawable.t005,
			R.drawable.t006, R.drawable.t007, R.drawable.t008, R.drawable.t009,
			R.drawable.t010, R.drawable.t011, R.drawable.t012, R.drawable.t013,
			R.drawable.t014, R.drawable.t015, R.drawable.t016, R.drawable.t017,
			R.drawable.t018, R.drawable.t019, R.drawable.t020 };

	public static final Integer[] symbolThumbIDs = { R.drawable.st01,
			R.drawable.st02, R.drawable.st03 };

	public static final String PIC_TYPE = "PIC_TYPE";
	public static final String PICTURE = "PICTURE";
	public static final String THUMBNAIL_ID = "THUMBNAIL_ID";
	public static final String DIFFICULTY = "DIFFICULTY";
	public static final String BORDER_WIDTH = "BORDER_WIDTH";
	public static final String WIDTH = "WIDTH";

	public static final int DEFAULT_PICTURE = 1111;
	public static final int PHONE_GALLERY = 2222;
	public static final int SYMBOL = 3333;

	private static Paint up;
	private static Paint down;
	private static Paint right;
	private static Paint left;

	static {
		up = new Paint();
		up.setStrokeWidth(0);
		up.setARGB(128, 255, 255, 255);

		left = new Paint();
		left.setStrokeWidth(0);
		left.setARGB(76, 255, 255, 255);

		down = new Paint();
		down.setStrokeWidth(0);
		down.setARGB(128, 0, 0, 0);

		right = new Paint();
		right.setStrokeWidth(0);
		right.setARGB(76, 0, 0, 0);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		int bitmapDimension = options.outHeight < options.outWidth ? options.outHeight
				: options.outWidth;
		options.inSampleSize = Math.round((float) bitmapDimension / reqWidth);
		options.inJustDecodeBounds = false;
		Bitmap b = BitmapFactory.decodeResource(res, resId, options);
		return Bitmap.createScaledBitmap(b, reqWidth, reqWidth, true);
	}

	public static Bitmap decodeSampledBitmapFromFile(String fileName,
			int reqWidth) {
		BitmapRegionDecoder decoder;
		try {
			decoder = BitmapRegionDecoder.newInstance(fileName, false);
		} catch (IOException e) {
			return null;
		}
		int width = decoder.getWidth();
		int height = decoder.getHeight();
		int regionWidth = width < height ? width : height;
		int x1 = (width - regionWidth) / 2;
		int y1 = (height - regionWidth) / 2;
		final Rect rect = new Rect(x1, y1, x1 + regionWidth, y1 + regionWidth);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = Math.round((float) regionWidth / reqWidth);
		Bitmap b = decoder.decodeRegion(rect, options);
		return Bitmap.createScaledBitmap(b, reqWidth, reqWidth, true);
	}

	public static Bitmap getBitmapFromIntent(Intent intent, Resources res,
			int width, int difficulty) {
		int choice = intent.getIntExtra(PIC_TYPE, -1);
		if (choice == DEFAULT_PICTURE) {
			int position = intent.getIntExtra(PICTURE, -1);
			return ImageUtils.decodeSampledBitmapFromResource(res,
					mPictureIDs[position], width);
		} else if (choice == PHONE_GALLERY) {
			String fileName = intent.getStringExtra(PICTURE);
			return ImageUtils.decodeSampledBitmapFromFile(fileName, width);
		} else if (choice == SYMBOL) {
			int position = intent.getIntExtra(PICTURE, -1);
			return ImageUtils.decodeSampledBitmapFromResource(res,
					mSymbolsIDs[difficulty - 3][position], width);
		} else {
			return null;
		}
	}

	public static Rect getRectForTile(Rect rect, int x, int y, double tileWidth) {
		rect.left = (int) Math.round(x * tileWidth);
		rect.top = (int) Math.round(y * tileWidth);
		rect.right = (int) Math.round((x + 1) * tileWidth);
		rect.bottom = (int) Math.round((y + 1) * tileWidth);
		return rect;
	}

	public static void drawBorder(Canvas canvas, int x1, int y1, int x2,
			int y2, int borderWidth) {

		for (int k = 0; k < borderWidth; k++) {
			canvas.drawLine(x1 + k, y1 + k, x2 - 1 - k, y1 + k, up);
		}
		for (int k = 0; k < borderWidth; k++) {
			canvas.drawLine(x1 + 1 + k, y2 - k - 1, x2 - k, y2 - k - 1, down);
		}
		for (int k = 0; k < borderWidth; k++) {
			canvas.drawLine(x1 + k, y1 + 1 + k, x1 + k, y2 - k, left);
		}
		for (int k = 0; k < borderWidth; k++) {
			canvas.drawLine(x2 - k - 1, y1 + k, x2 - k - 1, y2 - 1 - k, right);
		}
	}
}
