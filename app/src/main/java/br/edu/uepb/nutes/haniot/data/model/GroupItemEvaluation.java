package br.edu.uepb.nutes.haniot.data.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupItemEvaluation extends ExpandableGroup<ItemEvaluation> {

    public GroupItemEvaluation(String title, List<ItemEvaluation> items) {
        super(title, items);
    }

}
