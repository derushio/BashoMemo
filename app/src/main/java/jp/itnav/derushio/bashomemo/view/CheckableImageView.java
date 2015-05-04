package jp.itnav.derushio.bashomemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import jp.itnav.derushio.bashomemo.R;

/**
 * Created by derushio on 15/04/30.
 */

public class CheckableImageView extends ImageView {

	private boolean isChecked = false;

	public CheckableImageView(Context context) {
		super(context);
	}

	public CheckableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public boolean check() {
		isChecked = !isChecked;
		if (isChecked) {
			setImageResource(R.drawable.cardview_checker_check);
		} else {
			setImageDrawable(null);
		}

		return isChecked;
	}

	public boolean getChecked() {
		return isChecked;
	}
}
