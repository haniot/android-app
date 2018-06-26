package br.edu.uepb.nutes.haniot.elderly.assessment.pages;

import android.view.View;

public interface AssessmentPage {
    View getView();

    void nextPage();

    int getPageNumber();

    boolean isBlocked();

    void setAnswerRadio(boolean value);

    void setAnswerSpinner(String value);

    boolean getAnswerRadio();

    String getAnswerSpinner();

    void clearAnswer();
}
