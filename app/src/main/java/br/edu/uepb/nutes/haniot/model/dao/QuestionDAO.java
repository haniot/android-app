package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.Question;
import br.edu.uepb.nutes.haniot.model.Question_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class QuestionDAO {

    public static QuestionDAO instance;
    private static Box<Question> questionBox;

    private QuestionDAO (){

    }

    public static synchronized QuestionDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new QuestionDAO();

        BoxStore boxStore = ((App)context.getApplicationContext()).getBoxStore();
        questionBox = boxStore.boxFor(Question.class);

        return instance;
    }

    public Question get(@NonNull String _id){
        return questionBox.query().equal(Question_.id, _id).build().findFirst();
    }

    public boolean save(@NonNull Question question){
        return questionBox.put(question) > 0;
    }

    public boolean update(@NonNull Question question){
        Question questionUp = get(question.get_id());

        if (questionUp == null){
            return false;
        }else{
            question.setId(questionUp.getId());
            if (question.get_id() == null) question.set_id(questionUp.get_id());

            return save(question);
        }

    }

    public boolean remove(@NonNull long id){
        return questionBox.query().equal(Question_.id, id).build().remove() > 0;
    }

}
