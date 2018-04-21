package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.view.View;

/**
 * Interface OnAnswerListener.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface OnAnswerListener {
    void onAnswer(View view, boolean value, int page);
}
