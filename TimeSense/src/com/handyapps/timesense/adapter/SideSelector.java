package com.handyapps.timesense.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.handyapps.timesense.R;
import com.handyapps.timesense.util.ResourceUtils;

public class SideSelector extends View {
    private static String TAG = SideSelector.class.getCanonicalName();

    public static char[] ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final int BOTTOM_PADDING = 10;

    private SectionIndexer selectionIndexer = null;
    private ListView list;
    private Paint paint;
    private String[] sections = {};

    private Context context;
    
    public SideSelector(Context context) {
        super(context);
        init();
        
        this.context = context;
    }

    public SideSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressLint("ResourceAsColor")
	private void init() {
        setBackgroundColor(ResourceUtils.getColor(getContext(), R.color.White));
        paint = new Paint();
        paint.setColor(ResourceUtils.getColor(getContext(), R.color.Gray));
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics()));
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setListView(ListView _list) {
        list = _list;
        selectionIndexer = (SectionIndexer) _list.getAdapter();

        Object[] sectionsArr = selectionIndexer.getSections();
        sections = new String[sectionsArr.length];
        for (int i = 0; i < sectionsArr.length; i++) {
            sections[i] = sectionsArr[i].toString();
        }

    }

    /*
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int y = (int) event.getY();
        float selectedIndex = ((float) y / (float) getPaddedHeight()) * ALPHABET.length;

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (selectionIndexer == null) {
                selectionIndexer = (SectionIndexer) list.getAdapter();
            }
            int position = selectionIndexer.getPositionForSection((int) selectedIndex);
            if (position == -1) {
                return true;
            }
            list.setSelection(position);
        }
        return true;
    }*/

    @Override
    protected void onDraw(Canvas canvas) {

        int viewHeight = getPaddedHeight();
        float charHeight = ((float) viewHeight) / (float) sections.length;

        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < sections.length; i++) {
            canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight + (i * charHeight), paint);
        }
        super.onDraw(canvas);
    }

    private int getPaddedHeight() {
        return (int) (getHeight() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BOTTOM_PADDING, getContext().getResources().getDisplayMetrics()));
    }
}

