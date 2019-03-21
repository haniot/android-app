package br.edu.uepb.nutes.haniot.data.model;

public class PatientsType {

    class GenderType {
        public final static String MALE = "male";
        public final static String FEMALE = "female";

        public String getString(int type) {
            switch (type) {
                case 0:
                    return MALE;
                case 1:
                    return FEMALE;
                default:
                    return "";
            }
        }
    }
}
