package br.edu.uepb.nutes.haniot.server.historical;

import java.util.List;

/**
 * Represents the object that contains data coming from the server.
 *
 * @param <E>
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface Data<E> {
    public E getItem();

    public List<E> getSubItems();

    public boolean addSubItem(E item);

    public boolean addAllSubItems(List<E> subItems);

    public int subItemsSize();

    public boolean removeSubItem(E item);
}
