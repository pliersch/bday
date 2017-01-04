package de.liersch.android.bday.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageHelper {

  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float percent) {
    int length = bitmap.getWidth();
    if (bitmap.getHeight() > length) {
      length = bitmap.getHeight();
    }
    int pixels = (int) (length * percent);
    return getRoundedCornerBitmap(bitmap, pixels);
  }

  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
        .getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }
}
