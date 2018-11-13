package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.Question;
import br.edu.uepb.nutes.haniot.model.QuestionOption;
import br.edu.uepb.nutes.haniot.model.QuestionOption_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class QuestionOptionDAO {

    public static QuestionOptionDAO instance;
    private static Box<QuestionOption> questionOptionBox;

    private QuestionOptionDAO () {}

    public static synchronized QuestionOptionDAO getInstance(@NonNull Context context){

        if (instance == null) instance = new QuestionOptionDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        questionOptionBox = boxStore.boxFor(QuestionOption.class);

        return instance;

    }

    public QuestionOption get(@NonNull String id){
        return questionOptionBox.query().equal(QuestionOption_.id, id).build().findFirst();
    }

    public boolean save(@NonNull QuestionOption questionOption){
        return questionOptionBox.put(questionOption) > 0;
    }

    public boolean update(@NonNull QuestionOption questionOption){
        QuestionOption questionOptionUp = get(questionOption.get_id());

        if (questionOptionUp == null){
            return false;
        }else{
            questionOption.setId(questionOptionUp.getId());
            if (questionOption.get_id() == null)questionOption.set_id(questionOptionUp.get_id());
            return save(questionOption);
        }
    }

    public boolean remove(@NonNull long id){
        return questionOptionBox.query().equal(QuestionOption_.id, id).build().remove() > 0;
    }

}
