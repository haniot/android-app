package br.edu.uepb.nutes.haniot.survey.base;

import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * Interface OnPageListener.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public interface OnPageListener {
    void onQuestionImageClick(@DrawableRes int id);

    void onClosePage();
}
