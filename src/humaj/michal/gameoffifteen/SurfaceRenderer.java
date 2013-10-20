package humaj.michal.gameoffifteen;

import humaj.michal.activity.GameActivity.TimeHandler;
import humaj.michal.util.BitmapHolder;
import humaj.michal.util.ImageUtils;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class SurfaceRenderer implements Runnable {

	private Thread mThread = null;

	private SurfaceHolder mSurfaceHolder;

	private TextView mTvMoves;

	private int mDifficulty;
	private double mSurfaceTileWidth;
	private double mBitmapTileWidth;

	private Tile[][] mTile;
	private int mEmptyTileX;

	private int mEmptyTileY;

	private Timer mTimer;
	private int mMoves = 0;

	private boolean mIsPaused = true;
	private boolean mIsShuffling = true;
	private boolean mIsSolved = false;
	private boolean mPreview = false;
	private boolean mEmptySeen = false;

	private Rect mBitmapRect;
	private Rect mSurfaceRect;
	private Paint mAlphaPaint;

	private Rect mBgRect;
	private Rect mBgRectOnBitmap;
	private Paint mBgPaint;

	private Rect mTempRect = new Rect();

	private String mMovesBest;

	public SurfaceRenderer(TextView tv, int difficulty, int surfaceWidth,
			int borderWidth, TimeHandler handler) {
		mTvMoves = tv;
		mDifficulty = difficulty;
		mSurfaceTileWidth = surfaceWidth / (double) mDifficulty;
		Bitmap bitmap = BitmapHolder.getInstance().getBitmap();
		mBitmapTileWidth = bitmap.getWidth() / (double) mDifficulty;
		mTile = new Tile[difficulty][difficulty];
		Tile.setBorderWidth(borderWidth);
		mEmptyTileX = difficulty - 1;
		mEmptyTileY = difficulty - 1;
		mTimer = new Timer(handler);
		mBitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		mSurfaceRect = new Rect(0, 0, surfaceWidth, surfaceWidth);
		mAlphaPaint = new Paint();
		mAlphaPaint.setAlpha(5);
		mBgRect = new Rect(mSurfaceRect);
		mBgRectOnBitmap = new Rect(mBitmapRect);
		mBgPaint = new Paint();
		mBgPaint.setAlpha(76);
		for (int j = 0; j < mDifficulty; j++) {
			for (int i = 0; i < mDifficulty; i++) {
				mTile[i][j] = new Tile(ImageUtils.getRectForTile(new Rect(), i,
						j, mBitmapTileWidth), ImageUtils.getRectForTile(
						new Rect(), i, j, mSurfaceTileWidth), i, j);
			}
		}
	}

	@Override
	public void run() {
		Bitmap bitmap = BitmapHolder.getInstance().getBitmap();
		while (!mIsPaused) {
			if (!mSurfaceHolder.getSurface().isValid())
				continue;
			long startTime = System.currentTimeMillis();
			Canvas c = mSurfaceHolder.lockCanvas();
			drawTiles(c, bitmap);
			mSurfaceHolder.unlockCanvasAndPost(c);
			int toSleep = (int) (startTime + 20 - System.currentTimeMillis());
			if (toSleep > 0) {
				try {
					Thread.sleep(toSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void pause() {
		if (!mIsPaused) {
			mTimer.pause();
			mIsPaused = true;
			while (true) {
				try {
					mThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			mThread = null;
			if (!mSurfaceHolder.getSurface().isValid())
				return;
			Canvas c = mSurfaceHolder.lockCanvas();
			c.drawARGB(255, 0, 0, 0);
			mSurfaceHolder.unlockCanvasAndPost(c);
		}
	}

	public void resume() {		
		if (mIsPaused) {
			if (!mIsShuffling) {
				mTimer.resume();
			}
			mIsPaused = false;
			mThread = new Thread(this);
			mThread.start();
		}
	}

	public void setSurfaceHolder(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	public void previewOn() {
		mPreview = true;
	}

	public void previewOff() {
		mPreview = false;
	}

	public void toggleEmptySeen() {
		mEmptySeen = !mEmptySeen;
	}

	public boolean isSolved() {
		return mIsSolved;
	}

	public void onTouch(MotionEvent me) {
		if (mIsShuffling)
			return;
		if (mIsSolved)
			return;
		int x = (int) (me.getX() / mSurfaceTileWidth);
		int y = (int) (me.getY() / mSurfaceTileWidth);
		x = x < mDifficulty ? x : mDifficulty - 1;
		y = y < mDifficulty ? y : mDifficulty - 1;
		if (x != mEmptyTileX && y != mEmptyTileY)
			return;
		if (x == mEmptyTileX && y == mEmptyTileY)
			return;				
		mTempRect = ImageUtils.getRectForTile(mTempRect, mEmptyTileX,
				mEmptyTileY, mSurfaceTileWidth);
		mBgRect = ImageUtils.getRectForTile(mBgRect, x, y, mSurfaceTileWidth);
		mBgRect.union(mTempRect);
		mTempRect = ImageUtils.getRectForTile(mTempRect, mEmptyTileX,
				mEmptyTileY, mBitmapTileWidth);
		mBgRectOnBitmap = ImageUtils.getRectForTile(mBgRectOnBitmap, x, y,
				mBitmapTileWidth);
		mBgRectOnBitmap.union(mTempRect);
		int i = mEmptyTileX;
		int j = mEmptyTileY;
		int dx = (int) Math.signum(x - mEmptyTileX);
		int dy = (int) Math.signum(y - mEmptyTileY);
		do {
			mMoves++;
			i += dx;
			j += dy;
			Rect to = ImageUtils.getRectForTile(new Rect(), mEmptyTileX,
					mEmptyTileY, mSurfaceTileWidth);
			if (mTile[i][j].slide(to)) {
				mTile[mEmptyTileX][mEmptyTileY] = mTile[i][j];
				mEmptyTileX = i;
				mEmptyTileY = j;
			}
		} while (i != x || j != y);
		mTvMoves.setText(mMoves + mMovesBest);
		if (checkIfSolved()) {
			mIsSolved = true;
			mTimer.pause();
		}
	}

	public void setTvMoves(TextView mTvMoves) {
		this.mTvMoves = mTvMoves;
	}

	public TextView getTvMoves(){
		return mTvMoves;		
	}
	
	public void setTimeHandler(TimeHandler handler) {
		mTimer.setHandler(handler);
	}

	public int getMovesCount() {
		return mMoves;
	}

	public String getSolveTime() {
		return mTimer.getFormattedTime();
	}

	public void shuffleTiles() {
		Thread t = new Thread() {
			private static final byte UP = 0;
			private static final byte DOWN = 1;
			private static final byte LEFT = 2;
			private static final byte RIGHT = 3;

			@Override
			public void run() {
				Random random = new Random(System.currentTimeMillis());
				byte previousDirection = -1;
				int x = -1;
				int y = -1;
				int shuffleCount = (int) Math.pow(mDifficulty, 4) / 7 + 30;
				Tile.setNumFrames(8 - mDifficulty);
				for (int i = 0; i < shuffleCount; i++) {
					Rect to = ImageUtils.getRectForTile(new Rect(),
							mEmptyTileX, mEmptyTileY, mSurfaceTileWidth);
					boolean foundGood = false;
					do {
						int direction = random.nextInt(4);
						if (mEmptyTileX == 0 && direction == RIGHT)
							continue;
						if (mEmptyTileY == 0 && direction == DOWN)
							continue;
						if (mEmptyTileX == mDifficulty - 1 && direction == LEFT)
							continue;
						if (mEmptyTileY == mDifficulty - 1 && direction == UP)
							continue;
						if (direction == oppositeDirection(previousDirection))
							continue;
						switch (direction) {
						case UP:
							x = mEmptyTileX;
							y = mEmptyTileY + 1;
							previousDirection = UP;
							break;
						case DOWN:
							x = mEmptyTileX;
							y = mEmptyTileY - 1;
							previousDirection = DOWN;
							break;
						case LEFT:
							x = mEmptyTileX + 1;
							y = mEmptyTileY;
							previousDirection = LEFT;
							break;
						case RIGHT:
							x = mEmptyTileX - 1;
							y = mEmptyTileY;
							previousDirection = RIGHT;
							break;
						}
						foundGood = true;
					} while (!foundGood);
					if (mTile[x][y].slide(to)) {
						mTile[mEmptyTileX][mEmptyTileY] = mTile[x][y];
						mEmptyTileX = x;
						mEmptyTileY = y;
						try {
							mTile[x][y].getThread().join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				Tile.setNumFrames(7);
				mIsShuffling = false;
				mTimer.resume();
			}

			private byte oppositeDirection(byte dir) {
				switch (dir) {
				case UP:
					return DOWN;
				case DOWN:
					return UP;
				case RIGHT:
					return LEFT;
				case LEFT:
					return RIGHT;
				}
				return LEFT;
			}
		};
		t.start();
	}

	protected void drawTiles(Canvas c, Bitmap bitmap) {
		if (mEmptySeen) {
			c.drawARGB(255, 255, 255, 255);
			c.drawBitmap(bitmap, mBgRectOnBitmap, mBgRect, mBgPaint);
		} else {
			c.drawARGB(255, 0, 0, 0);
		}
		for (int j = 0; j < mDifficulty; j++) {
			for (int i = 0; i < mDifficulty; i++) {
				if (i == mEmptyTileX && j == mEmptyTileY)
					continue;
				mTile[i][j].draw(c, bitmap);
			}
		}
		if (mPreview) {
			c.drawBitmap(bitmap, mBitmapRect, mSurfaceRect, null);
			Rect rect = new Rect();
			int borderWidth = Tile.getBorderWidth();
			for (int j = 0; j < mDifficulty; j++) {
				for (int i = 0; i < mDifficulty; i++) {
					rect = ImageUtils.getRectForTile(rect, i, j,
							mSurfaceTileWidth);
					ImageUtils.drawBorder(c, rect.left, rect.top, rect.right,
							rect.bottom, borderWidth);
				}
			}
		}
		if (mIsSolved) {
			c.drawBitmap(bitmap, mBitmapRect, mSurfaceRect, mAlphaPaint);
			int alpha = mAlphaPaint.getAlpha();
			if (alpha < 255) {
				mAlphaPaint.setAlpha(alpha + 10);
			}
		}
	}

	private boolean checkIfSolved() {
		for (int j = 0; j < mDifficulty; j++) {
			for (int i = 0; i < mDifficulty; i++) {
				if (i == mEmptyTileX && j == mEmptyTileY)
					continue;
				if (!mTile[i][j].isSolved(i, j))
					return false;
			}
		}
		return true;
	}

	public void setMovesBest(String a) {
		mMovesBest = a;
	}
}
