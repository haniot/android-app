package br.edu.uepb.nutes.haniot.survey.base;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/**
 * BaseConfigPage implementation.
 *
 * @param <T>
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public abstract class BaseConfigPage<T> {
    public String titleStr,
            descriptionStr;

    public int layout,
            title,
            description,
            image,
            colorBackground,
            titleColor,
            descriptionColor,
            pageNumber,
            drawableClose,
            drawablePause;

    public boolean zoomDisabled;

    public BaseConfigPage() {
        this.layout = 0;
        this.title = 0;
        this.description = 0;
        this.image = 0;
        this.colorBackground = 0;
        this.titleColor = 0;
        this.descriptionColor = 0;
        this.drawableClose = 0;
        this.drawablePause = 0;
        this.zoomDisabled = true;
    }

    /**
     * Set resource layout.
     *
     * @param layout
     * @return T
     */
    public T layout(@LayoutRes int layout) {
        this.layout = layout;
        return (T) this;
    }

    /**
     * Set title.
     *
     * @param title int
     * @return T
     */
    public T title(@StringRes int title) {
        this.title = title;
        return (T) this;
    }

    /**
     * Set title.
     *
     * @param title {@link String}
     * @return T
     */
    public T title(String title) {
        this.titleStr = title;
        return (T) this;
    }

    /**
     * Set description.
     *
     * @param description int
     * @return T
     */
    public T description(@StringRes int description) {
        this.description = description;
        return (T) this;
    }

    /**
     * Set description.
     *
     * @param description {@link String}
     * @return T
     */
    public T description(String description) {
        this.descriptionStr = description;
        return (T) this;
    }

    /**
     * Set title and color.
     *
     * @param title
     * @param titleColor
     * @return T
     */
    public T title(@StringRes int title, @ColorInt int titleColor) {
        this.title = title;
        this.titleColor = titleColor;
        return (T) this;
    }

    /**
     * Set title and color.
     *
     * @param title      {@link String}
     * @param titleColor int
     * @return T
     */
    public T title(String title, @ColorInt int titleColor) {
        this.titleStr = title;
        this.titleColor = titleColor;
        return (T) this;
    }

    /**
     * Set description and color.
     *
     * @param description
     * @param descriptionColor
     * @return T
     */
    public T description(@StringRes int description, @ColorInt int descriptionColor) {
        this.description = description;
        this.descriptionColor = descriptionColor;
        return (T) this;
    }

    /**
     * Set description and color.
     *
     * @param description      {@link String}
     * @param descriptionColor int
     * @return T
     */
    public T description(String description, @ColorInt int descriptionColor) {
        this.descriptionStr = description;
        this.descriptionColor = descriptionColor;
        return (T) this;
    }

    /**
     * Set image image.
     *
     * @param image
     * @return T
     */
    public T image(@DrawableRes int image) {
        this.image = image;
        return (T) this;
    }

    /**
     * Set background color
     *
     * @param colorBackground
     * @return T
     */
    public T colorBackground(@ColorInt int colorBackground) {
        this.colorBackground = colorBackground;
        return (T) this;
    }

    /**
     * Set title color.
     *
     * @param titleColor
     * @return T
     */
    public T titleColor(@ColorInt int titleColor) {
        this.titleColor = titleColor;
        return (T) this;
    }

    /**
     * Set description color.
     *
     * @param descriptionColor
     * @return T
     */
    public T descriptionColor(@ColorInt int descriptionColor) {
        this.descriptionColor = descriptionColor;
        return (T) this;
    }

    /**
     * Set page number.
     *
     * @param pageNumber
     * @return T
     */
    public T pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return (T) this;
    }

    /**
     * Set image button close.
     *
     * @param drawableClose
     * @return T
     */
    public T buttonClose(int drawableClose) {
        this.drawableClose = drawableClose;
        return (T) this;
    }

    /**
     * Set image button pause.
     *
     * @param drawablePause
     * @return T
     */
    public T buttonPause(int drawablePause) {
        this.drawablePause = drawablePause;
        return (T) this;
    }

    /**
     * Enable zoom in question image.
     *
     * @return ConfigPage
     */
    public T enableZoomImage() {
        this.zoomDisabled = false;
        return (T) this;
    }

    @Override
    public String toString() {
        return "BaseConfigPage{" +
                "titleStr='" + titleStr + '\'' +
                ", descriptionStr='" + descriptionStr + '\'' +
                ", layout=" + layout +
                ", title=" + title +
                ", description=" + description +
                ", image=" + image +
                ", colorBackground=" + colorBackground +
                ", titleColor=" + titleColor +
                ", descriptionColor=" + descriptionColor +
                ", pageNumber=" + pageNumber +
                ", drawableClose=" + drawableClose +
                ", drawablePause=" + drawablePause +
                ", zoomDisabled=" + zoomDisabled +
                '}';
    }

    public abstract Fragment build();
}
