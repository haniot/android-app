package br.edu.uepb.nutes.haniot.server.historical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents history object containing data coming from server.
 *
 * @param <E>
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HistoricalData<E> implements Data<E> {

    private E item;
    private List<E> subItems;

    public HistoricalData() {
    }

    public HistoricalData(E item, List<E> subItems) {
        this.item = item;
        this.subItems = subItems;
    }

    @Override
    public E getItem() {
        return item;
    }

    @Override
    public List<E> getSubItems() {
        return subItems;
    }

    @Override
    public boolean addSubItem(E item) {
        if (subItems == null) subItems = new ArrayList<>();
        return subItems.add(item);
    }

    @Override
    public boolean addAllSubItems(List<E> subItems) {
        if (subItems == null) subItems = new ArrayList<>();
        return subItems.addAll(subItems);
    }

    @Override
    public int subItemsSize() {
        if (subItems == null) return 0;
        return subItems.size();
    }

    @Override
    public boolean removeSubItem(E item) {
        if (subItems == null) return false;
        return subItems.remove(item);
    }

    @Override
    public String toString() {
        return "HistoricalData{" +
                "item=" + item +
                ", subItems=" + Arrays.toString(subItems.toArray()) +
                '}';
    }
}
