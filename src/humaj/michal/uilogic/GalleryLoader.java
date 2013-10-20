package humaj.michal.uilogic;

import humaj.michal.activity.ChoosePictureActivity;
import humaj.michal.activity.ChoosePictureActivity.ThumbnailHandler;
import humaj.michal.util.ImageUtils;
import humaj.michal.util.SquareImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.os.Message;

public class GalleryLoader extends Thread {

	private volatile ConcurrentHashMap<SquareImageView, String> mQueue;

	private volatile boolean isKilled = false;

	private WeakReference<ChoosePictureActivity> mActivity;
	private ThumbnailHandler mHandler;
	private int mThumbWidth;

	public GalleryLoader(ChoosePictureActivity activity, ThumbnailHandler handler,
			int thumbWidth) {
		mActivity = new WeakReference<ChoosePictureActivity>(activity);
		mHandler = handler;
		mThumbWidth = thumbWidth;
		mQueue = new ConcurrentHashMap<SquareImageView, String>();
	}

	public synchronized void add(SquareImageView imageView, String fileName) {
		mQueue.put(imageView, fileName);
	}

	public synchronized void remove(SquareImageView iv) {
		mQueue.remove(iv);
	}

	public synchronized void kill() {
		isKilled = true;
		synchronized (this) {
			notify();			
		}
	}

	@Override
	public void run() {
		while (!isKilled) {
			if (mQueue.isEmpty()) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
				}
			}
			for (SquareImageView imageView : mQueue.keySet()) {
				final String fileName;
				synchronized (this) {
					fileName = mQueue.get(imageView);
					mQueue.remove(imageView);
				}
				if (fileName == null)
					continue;
				final Bitmap bitmap = ImageUtils.decodeSampledBitmapFromFile(
						fileName, mThumbWidth);	
				if (bitmap == null)
					continue;				
				mActivity.get().addBitmapToMemoryCache(fileName, bitmap);
				if (mQueue.get(imageView) == null) {
					final GalleryMessageObject holder = new GalleryMessageObject(
							bitmap, imageView);
					final Message msg = new Message();
					msg.obj = holder;
					mHandler.sendMessage(msg);
				}
			}			
		}
	}
}

