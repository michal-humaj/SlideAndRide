package humaj.michal.uilogic;

import humaj.michal.activity.ChoosePictureActivity.ThumbnailHandler;
import humaj.michal.util.SquareImageView;

import java.util.concurrent.ConcurrentHashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

public class PictureLoader extends Thread {

	private volatile ConcurrentHashMap<SquareImageView, Integer> mQueue;

	private volatile boolean isKilled = false;

	private Resources mResources;
	private ThumbnailHandler mHandler;

	public PictureLoader(Resources res, ThumbnailHandler handler) {
		mResources = res;
		mHandler = handler;

		mQueue = new ConcurrentHashMap<SquareImageView, Integer>();
	}

	public synchronized void add(SquareImageView imageView, Integer id) {
		mQueue.put(imageView, id);
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
				final Integer id;
				synchronized (this) {
					id = mQueue.get(imageView);
					mQueue.remove(imageView);
				}
				if (id == null)
					continue;
				final Bitmap bitmap = BitmapFactory.decodeResource(mResources,
						id);
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
