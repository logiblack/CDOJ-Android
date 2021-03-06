package cn.edu.uestc.acm.cdoj.ui.custom_view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.edu.uestc.acm.cdoj.R;

/**
 * Created by 14779 on 2017-8-11.
 */

public class ContestStatusTextView extends TextView {
    public ContestStatusTextView(Context context) {
        super(context);
    }

    public ContestStatusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContestStatusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContestStatusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (text.equals("Ended")) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.contestStatus_Ended));
            return;
        }
        if (text.equals("Running"))
            setTextColor(ContextCompat.getColor(getContext(), R.color.contestStatus_Running));
    }
}
