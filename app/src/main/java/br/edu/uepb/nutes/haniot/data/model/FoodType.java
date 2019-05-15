package br.edu.uepb.nutes.haniot.data.model;

import android.content.Context;
import android.content.res.Resources;

import br.edu.uepb.nutes.haniot.R;

public class FoodType {
    public static final String FISH_CHICKEN_BEEF = "fish, chicken or red meat";
    public static final String SODA = "soda";
    public static final String SALAD = "raw salad, greens or vegetables";
    public static final String FRIED = "fried";
    public static final String MILK = "milk";
    public static final String BEAN = "bean";
    public static final String FRUITS = "fruits";
    public static final String GOODIES = "goodies";
    public static final String HAMBURGER_SAUSAGE_OTHERS = "hamburger or sausages";

    public static final String FISH_CHICKEN_BEEF_PTBR = "Peixe, frango ou carne vermelha";
    public static final String SODA_PTBR = "Refrigerante";
    public static final String SALAD_PTBR = "Salada crua e vegetais";
    public static final String FRIED_PTBR = "Frituras";
    public static final String MILK_PTBR = "Leite";
    public static final String BEAN_PTBR = "Feijão";
    public static final String FRUITS_PTBR = "Fruta";
    public static final String GOODIES_PTBR = "Guloseimas";
    public static final String HAMBURGER_SAUSAGE_OTHERS_PTBR = "Hamgurgueres e enlatados";

    public static String getStringPTBR(String type) {
        switch (type) {
            case FISH_CHICKEN_BEEF:
                return FISH_CHICKEN_BEEF_PTBR;
            case SODA:
                return SODA_PTBR;
            case SALAD:
                return SALAD_PTBR;
            case FRIED:
                return FRIED_PTBR;
            case MILK:
                return MILK_PTBR;
            case BEAN:
                return BEAN_PTBR;
            case FRUITS:
                return FRUITS_PTBR;
            case GOODIES:
                return GOODIES_PTBR;
            case HAMBURGER_SAUSAGE_OTHERS:
                return HAMBURGER_SAUSAGE_OTHERS_PTBR;
            default:
                return "";
        }
    }

}
