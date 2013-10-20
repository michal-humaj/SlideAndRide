package humaj.michal.uilogic;

import humaj.michal.util.SquareImageView;
import android.graphics.Bitmap;

public class GalleryMessageObject {
	
	public final Bitmap bitmap;
	public final SquareImageView imageView;
	
	public GalleryMessageObject(Bitmap bitmap, SquareImageView iv) {		
		this.bitmap = bitmap;
		this.imageView = iv;
	}
}
