package mx.com.broadcastv.ui.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import mx.com.broadcastv.R;

public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }

    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);

    }
}
