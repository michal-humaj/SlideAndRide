package humaj.michal.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

public class TiledSquareImageView extends SquareImageView {

	private int mDifficulty = -1;
	private int mBorderWidth = -1;

	public TiledSquareImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TiledSquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TiledSquareImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		Rect rect = new Rect();
		double tileWidth = getWidth() / (double) mDifficulty;
		for (int j = 0; j < mDifficulty; j++) {
			for (int i = 0; i < mDifficulty; i++) {
				rect = ImageUtils.getRectForTile(rect, i, j, tileWidth);
				ImageUtils.drawBorder(canvas, rect.left, rect.top, rect.right,
						rect.bottom, mBorderWidth);
			}
		}
	}

	public void setDifficulty(int difficulty) {
		mDifficulty = difficulty;
		invalidate();
	}

	public void setBorderWidth(int borderWidth) {
		mBorderWidth = borderWidth;
		invalidate();
	}
}
