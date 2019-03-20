package br.edu.uepb.nutes.haniot.model;

import java.util.ArrayList;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;

@Entity
public class Quiz {
    @Id
    private long id;

    @Index
    private String _id;

    private String title;
    private String name;
    private String path;
    private String created;
    private String modified;


//    private ArrayList<Question> questions;

    @Backlink(to = "quiz")
    ToMany<Question> questions;

    public Quiz(String title, String path) {

        //Testa se o titulo não é vazio
        if (!title.isEmpty() && !title.equals("")) {
            this.title = title;
        } else {
            return;
        }

        //Testa se o array de questões está vazio
//        if (!questions.isEmpty() && questions.size() > 0){
//            this.questions = questions;
//        }else{
//            return;
//        }

        //Testa se o path não é vazio
        if (!path.isEmpty() && !path.equals("")) {
            this.path = path;
        } else {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
