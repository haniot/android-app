package br.edu.uepb.nutes.haniot.elderly.assessment.pages;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.Unbinder;

/**
 * BasePage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class BasePage extends Fragment {
    protected Unbinder unbinder;
    protected boolean isBlocked;
    protected int pageNumber;

    @Nullable
    @BindView(R.id.question_title)
    TextView titleTextView;

    @Nullable
    @BindView(R.id.question_description)
    TextView descTextView;

    @Nullable
    @BindView(R.id.question_image)
    ImageView imgTextView;

    @Nullable
    @BindView(R.id.close_imageButton)
    ImageButton closeImageButton;

    @Nullable
    @BindView(R.id.box_title)
    LinearLayout boxTitle;

    @Nullable
    @BindView(R.id.box_description)
    LinearLayout boxDescription;

    @Nullable
    @BindView(R.id.box_image)
    LinearLayout boxImage;
}
