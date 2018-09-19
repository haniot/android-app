package br.edu.uepb.nutes.haniot.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;
import android.widget.TextView;

public class ItemGrid extends AppCompatButton{

    private Drawable icon;
    private TextView description;
    private TextView name;
    private Context context;
    public Activity activity;

    public ItemGrid(Context context) {
        super(context);
        this.context = context;
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
