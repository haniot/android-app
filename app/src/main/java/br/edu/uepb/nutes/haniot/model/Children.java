package br.edu.uepb.nutes.haniot.model;

public class Children {

    private long id;
    private String sex;
    private String color;
    private int age;
    private String registerDate;
    private long idProfessionalSponsor;

    public Children(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public long getIdProfessionalSponsor() {
        return idProfessionalSponsor;
    }

    public void setIdProfessionalSponsor(long idProfessionalSponsor) {
        this.idProfessionalSponsor = idProfessionalSponsor;
    }

}
