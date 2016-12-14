package net.numa08.updaterealmadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoTextView extends TextView implements Target {
    public PicassoTextView(Context context) {
        super(context);
    }

    public PicassoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PicassoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d("hoge", "failed");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        setCompoundDrawablesWithIntrinsicBounds(placeHolderDrawable, null, null, null);
    }
}
