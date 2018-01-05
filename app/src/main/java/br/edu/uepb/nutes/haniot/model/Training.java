package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents object of a training.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Training {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String title;
    private long startDate;
    private long endDate;

    /**
     * RELATIONS
     */
    private ToOne<User> user;

    @Backlink(to = "training")
    private ToMany<Measurement> measurements;

    /**
     * {@link TrainingType()}
     */
    private int type;

    public Training() {
    }

    public Training(String title, long startDate, long endDate, int type) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(ToOne<User> user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ToMany<Measurement> measurements) {
        this.measurements = measurements;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) (startDate ^ (startDate >>> 32));
        result = 31 * result + (int) (endDate ^ (endDate >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Training))
            return false;

        Training other = (Training) o;

        return (other.getUser().equals(this.getUser())) &&
                other.getStartDate() == this.getStartDate() &&
                other.getEndDate() == this.getEndDate();
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", user=" + user +
                ", measurements=" + measurements +
                ", type=" + type +
                '}';
    }
}
