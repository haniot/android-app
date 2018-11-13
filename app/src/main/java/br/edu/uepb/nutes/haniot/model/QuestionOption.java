package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

@Entity
public class QuestionOption {

    @Id
    private long id;

    @Index
    private String _id;

    private String option;

    ToOne<Question> question;

    public QuestionOption (String option){
        if (!option.isEmpty() && !option.equals("")){
            this.option = option;
        }else{
            return;
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
