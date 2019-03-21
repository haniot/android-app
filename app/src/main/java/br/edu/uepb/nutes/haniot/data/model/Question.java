package br.edu.uepb.nutes.haniot.data.model;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Question {

    @Id
    private long id;

    @Index
    private String _id;

    ToOne<Quiz> quiz;

    @Backlink(to = "question")
    ToMany<QuestionOption> options;

    //Tipos de questão suportados
    public final int MULTIPLE_CHOICE = 1;
    public final int SIMPLE_CHOICE = 2;
    public final int BOOLEAN_CHOICE = 3;

    //Atributo key usado para guardar o tipo da questão
    private int key;

    //Atributo label usado para guardar o título da questão
    private String label;

    //Atributo options guarda as opções da questão
//    private ArrayList<String> options;

    /*public Question(int questionType, String title){

        //Este if faz a validação do tipo da questão
        if (questionType == this.SIMPLE_CHOICE || questionType == this.BOOLEAN_CHOICE || questionType == this.MULTIPLE_CHOICE){
            this.key = questionType;
        }else{
            return;
        }

        //Este if testa se o título não é vazio
        if (!title.isEmpty() && !title.equals("")){
            this.label = title;
        }else {
            return;
        }

        //Este if testa se o array de opções não é vazio
//        if (!options.isEmpty() && options.size()>0 ){
//            this.options = options;
//        }else{
//            return;
//        }

    }*/

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

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

//    public ArrayList<String> getOptions() {
//        return options;
//    }
//
//    public void setOptions(ArrayList<String> options) {
//        this.options = options;
//    }

}
