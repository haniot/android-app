package br.edu.uepb.nutes.haniot.data.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GroupItemEvaluation extends ExpandableGroup<ItemEvaluation> {

    private int type;
    private String _idGroup;
    private long idGroup;

    public GroupItemEvaluation(String title, List<ItemEvaluation> items, int type) {
        super(title, items);
        this.type = type;
    }

    public GroupItemEvaluation(String title, List<ItemEvaluation> items, int type, String _idGroup, long idGroup) {
        super(title, items);
        this.type = type;
        this._idGroup = _idGroup;
        this.idGroup = idGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String get_idGroup() {
        return _idGroup;
    }

    public void set_idGroup(String _idGroup) {
        this._idGroup = _idGroup;
    }

    public long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(long idGroup) {
        this.idGroup = idGroup;
    }
}
