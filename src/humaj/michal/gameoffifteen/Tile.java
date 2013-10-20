package humaj.michal.gameoffifteen;

import humaj.michal.util.ImageUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Tile implements Runnable {

	private static int mNumFrames;
	private static int mBorderWidth;

	private final Rect src;
	private Rect dst;
	private int supposedX;
	private int supposedY;
	private Thread t = null;
	private Rect to;

	public Tile(Rect src, Rect dst, int x, int y) {
		this.src = src;
		this.dst = dst;
		supposedX = x;
		supposedY = y;
	}

	public static void setBorderWidth(int borderWidth) {
		mBorderWidth = borderWidth;
	}

	public static int getBorderWidth() {
		return mBorderWidth;
	}

	public static void setNumFrames(int n) {
		mNumFrames = n;
	}

	@Override
	public void run() {
		double xIncrement;
		double yIncrement;
		int origX;
		int origY;
		synchronized (this) {
			origX = dst.left;
			origY = dst.top;
			xIncrement = (to.left - dst.left) / (double) mNumFrames;
			yIncrement = (to.top - dst.top) / (double) mNumFrames;
		}
		for (int i = 1; i < mNumFrames; i++) {
			synchronized (this) {
				if (i < mNumFrames) {
					dst.offsetTo((int) Math.round(i * xIncrement + origX),
							(int) Math.round(i * yIncrement + origY));
				}
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		synchronized (this) {
			dst = to;
		}
	}

	public boolean slide(Rect to) {
		if (t == null || !t.isAlive()) {
			synchronized (this) {
				this.to = to;
				t = new Thread(this);
				t.start();
				return true;
			}
		}
		return false;
	}

	public synchronized void draw(Canvas c, Bitmap b) {
		c.drawBitmap(b, src, dst, null);
		ImageUtils.drawBorder(c, dst.left, dst.top, dst.right, dst.bottom,
				mBorderWidth);
	}

	public synchronized boolean isSolved(int x, int y) {
		if (x != supposedX)
			return false;
		if (y != supposedY)
			return false;
		return true;
	}

	public synchronized void setDst(Rect dst) {
		this.dst.set(dst);
	}

	public synchronized Rect getDst() {
		return dst;
	}

	public synchronized Thread getThread() {
		return t;
	}
}
