package br.edu.uepb.nutes.haniot.data.objectbox;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@BaseEntity
public abstract class ActivityHabitsRecordOB {
    @Id
    private long id;

    @Index
    private String _id;

    public ActivityHabitsRecordOB() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", _id='" + _id + "\', ";
    }
}
