package br.edu.uepb.nutes.haniot.survey.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BasePage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public abstract class BasePage<T extends BaseConfigPage> extends Fragment implements IBasePage<T> {
    protected Unbinder unbinder;
    protected boolean isBlocked;
    protected int pageNumber;
    protected OnPageListener mPageListener;
    private Session session;
    private final String SEPARATOR_ITEMS = "#";

    @Nullable
    @BindView(R.id.question_title)
    public TextView titleTextView;

    @Nullable
    @BindView(R.id.question_description)
    public TextView descTextView;

    @Nullable
    @BindView(R.id.question_image)
    public PhotoView questionImageView;

    @Nullable
    @BindView(R.id.close_imageButton)
    public ImageButton closeImageButton;

    @Nullable
    @BindView(R.id.box_title)
    public LinearLayout boxTitle;

    @Nullable
    @BindView(R.id.box_description)
    public LinearLayout boxDescription;

    @Nullable
    @BindView(R.id.box_image)
    public LinearLayout boxImage;

    @Nullable
    @BindView(R.id.box_input)
    public LinearLayout boxInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        session = new Session(getActivity());

        if (boxTitle != null && titleTextView != null) {
            if (getConfigsPage().title != 0)
                titleTextView.setText(getConfigsPage().title);
            else if (getConfigsPage().titleStr != null && !getConfigsPage().titleStr.isEmpty())
                titleTextView.setText(getConfigsPage().titleStr);
            else
                boxTitle.setVisibility(View.GONE);

            if (getConfigsPage().titleColor != 0)
                titleTextView.setTextColor(getConfigsPage().titleColor);
        }

        if (boxDescription != null && descTextView != null) {
            if (getConfigsPage().description != 0)
                descTextView.setText(getConfigsPage().description);
            else if (getConfigsPage().descriptionStr != null && !getConfigsPage().descriptionStr.isEmpty())
                descTextView.setText(getConfigsPage().descriptionStr);
            else
                boxDescription.setVisibility(View.GONE);

            if (getConfigsPage().descriptionColor != 0)
                descTextView.setTextColor(getConfigsPage().descriptionColor);
        }

        if (closeImageButton != null) {
            if (getConfigsPage().drawableClose != 0) {
                closeImageButton.setImageResource(getConfigsPage().drawableClose);
                closeImageButton.setOnClickListener(e -> mPageListener.onClosePage());
            } else closeImageButton.setVisibility(View.GONE);
        }

        if (boxImage != null && questionImageView != null) {
            if (getConfigsPage().image != 0) {
                questionImageView.setImageResource(getConfigsPage().image);

                // enable/disable zoom
                questionImageView.setZoomable(!getConfigsPage().zoomDisabled);
            } else boxImage.setVisibility(View.GONE);
        }

        initView();

        return view;
    }

    /**
     * Get instance the library app intro.
     *
     * @return AppIntro
     */
    private AppIntro getAppIntroInstance() {
        return (AppIntro) getContext();
    }

    /**
     * Get instance current page.
     *
     * @return AppIntroViewPager
     */
    private AppIntroViewPager getPageInstance() {
        return getAppIntroInstance().getPager();
    }

    @Override
    public void blockPage() {
        this.isBlocked = true;
        getAppIntroInstance().setNextPageSwipeLock(this.isBlocked);
    }

    @Override
    public void unlockPage() {
        this.isBlocked = false;
        getAppIntroInstance().setNextPageSwipeLock(this.isBlocked);
    }

    /**
     * Next page.
     */
    @Override
    public void nextPage() {
        unlockPage();
        new Handler().post(() -> getPageInstance().goToNextSlide());
    }

    /**
     * Check if page is blocked.
     *
     * @return boolean
     */
    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Select page number.
     *
     * @return int
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Retrieve extra items saved in sharedPreferences.
     *
     * @param key {@link String}
     * @return List<String>
     */
    public List<String> getItemsExtraSharedPreferences(String key) {
        String extra = session.getString(key);
        return Arrays.asList(extra.split(SEPARATOR_ITEMS));
    }

    /**
     * Save extra items in sharedPreferences.
     * Items are saved as {@link String} separated by {@link #SEPARATOR_ITEMS}
     *
     * @param key  {@link String}
     * @param item {@link String}
     * @return boolean
     */
    public boolean saveItemExtraSharedPreferences(String key, String item) {
        if (getItemsExtraSharedPreferences(key).size() > 0)
            return session.putString(key, session.getString(key)
                    .concat(SEPARATOR_ITEMS).concat(item));
        return session.putString(key, item);
    }

    /**
     * Remove all items in sharedPreferences.
     *
     * @param key  {@link String}
     * @param item {@link String}
     */
    public void removeItemExtraSharedPreferences(String key, String item) {
        List<String> _temp = new ArrayList<>(getItemsExtraSharedPreferences(key));
        if (_temp.size() <= 0) return;

        _temp.remove(new String(item));
        if (_temp.equals(getItemsExtraSharedPreferences(key))) return;

        /**
         * Clean items.
         * Save items without the item removed.
         */
        cleanItemsExtraSharedPreferences(key);
        for (String s : _temp)
            saveItemExtraSharedPreferences(key, s);
    }

    /**
     * Remove all items in sharedPreferences.
     *
     * @param key {@link String}
     * @return boolean
     */
    public boolean cleanItemsExtraSharedPreferences(String key) {
        return session.removeString(key);
    }
}
