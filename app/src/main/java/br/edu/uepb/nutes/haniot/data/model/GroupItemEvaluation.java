package br.edu.uepb.nutes.haniot.data.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupItemEvaluation extends ExpandableGroup<ItemEvaluation> {

    private int type;
    private String idGroup;

    public GroupItemEvaluation(String title, List<ItemEvaluation> items, int type) {
        super(title, items);
        this.type = type;
    }

    public GroupItemEvaluation(String title, List<ItemEvaluation> items, int type, String idGroup) {
        super(title, items);
        this.type = type;
        this.idGroup = idGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }
}
