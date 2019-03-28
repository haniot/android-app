package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.Quiz;
import br.edu.uepb.nutes.haniot.data.model.Quiz_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class QuizDAO {

    public static QuizDAO instance;
    private static Box<Quiz> quizBox;

    private QuizDAO(){

    }

    public static synchronized QuizDAO getInstance(@NonNull Context context){

        if (instance == null) instance = new QuizDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        quizBox = boxStore.boxFor(Quiz.class);

        return instance;

    }

    public Quiz get(@NonNull String _id){
        return quizBox.query().equal(Quiz_._id, _id).build().findFirst();
    }

    public boolean save(@NonNull Quiz quiz){
        return quizBox.put(quiz) > 0 ;
    }

    public boolean update (@NonNull Quiz quiz){

        Quiz quizUp = get(quiz.get_id());
        if (quizUp == null){
            return false;
        }else{
            //Seta o ID para atualizar;
            quiz.setId(quizUp.getId());
            //Seta o _ID(string) para atualizar
            if (quiz.get_id() == null) quiz.set_id(quizUp.get_id());
            return save(quiz);
        }

    }

    public boolean remove (@NonNull long id){
        return quizBox.query().equal(Quiz_.id, id).build().remove() > 0 ;
    }

}
