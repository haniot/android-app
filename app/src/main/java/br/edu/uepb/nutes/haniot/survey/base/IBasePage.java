package br.edu.uepb.nutes.haniot.survey.base;

import android.view.View;

public interface IBasePage<T> {
    View getView();

    void nextPage();

    int getPageNumber();

    boolean isBlocked();

    void clearAnswer();

    void initView();

    void blockPage();

    void unlockPage();

    int getLayout();

    T getConfigsPage();

    View getComponentAnswer();
}
