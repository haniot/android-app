package br.edu.uepb.nutes.haniot.data.model;

public class FoodType {

    public static final String FISH_CHICKEN_MEAT = "fish_chicken_meat";
    public static final String SODA = "soda";
    public static final String SALAD_VEGETABLE = "salad_vegetable";
    public static final String FRIED_SALT_FOOD = "fried_salt_food";
    public static final String MILK = "milk";
    public static final String BEAN = "bean";
    public static final String FRUITS = "fruits";
    public static final String CANDY_SUGAR_COOKIE = "candy_sugar_cookie";
    public static final String BURGER_SAUSAGE = "burger_sausage";

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
            case FISH_CHICKEN_MEAT:
                return FISH_CHICKEN_BEEF_PTBR;
            case SODA:
                return SODA_PTBR;
            case SALAD_VEGETABLE:
                return SALAD_PTBR;
            case FRIED_SALT_FOOD:
                return FRIED_PTBR;
            case MILK:
                return MILK_PTBR;
            case BEAN:
                return BEAN_PTBR;
            case FRUITS:
                return FRUITS_PTBR;
            case CANDY_SUGAR_COOKIE:
                return GOODIES_PTBR;
            case BURGER_SAUSAGE:
                return HAMBURGER_SAUSAGE_OTHERS_PTBR;
            default:
                return "";
        }
    }

}
