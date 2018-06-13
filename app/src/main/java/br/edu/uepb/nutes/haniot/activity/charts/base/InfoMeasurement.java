package br.edu.uepb.nutes.haniot.activity.charts.base;

import br.edu.uepb.nutes.haniot.R;

/**
 * Created by Fabio on 10/05/2018.
 */

public class InfoMeasurement {

    private String title;
    private String value;

    public InfoMeasurement(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
