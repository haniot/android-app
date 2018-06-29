package br.edu.uepb.nutes.haniot.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Image preview activity.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class ImagePreviewActivity extends AppCompatActivity {

    public static String IMAGE_PREVIEW = "image_preview";

    @BindView(R.id.close_image_preview_imageButton)
    ImageButton buttonCloseImagePreview;

    @BindView(R.id.image_preview_imageView)
    ImageView imagePreview;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);

        int imageResource = getIntent().getIntExtra(IMAGE_PREVIEW, -1);
        if (imageResource != -1) imagePreview.setImageResource(imageResource);

        buttonCloseImagePreview.setOnClickListener(v -> finish());
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
