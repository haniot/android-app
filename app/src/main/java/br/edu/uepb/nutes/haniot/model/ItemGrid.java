package br.edu.uepb.nutes.haniot.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class ItemGrid {

    private Drawable icon;
    private TextView description;
    private TextView name;
    private Context context;
    public Activity activity;

    public ItemGrid(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description.getText().toString();
    }

    public void setDescription(String description) {
        if(this.description == null){
            this.description = new TextView(context);
            this.description.setText(description);
        }
        this.description.setText(description);
    }

    public String getName() {
        return name.getText().toString();
    }

    public void setName(String name) {
        if(this.name == null){
            this.name = new TextView(context);
            this.name.setText(name);
        }
        this.name.setText(name);
    }

}
