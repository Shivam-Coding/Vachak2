package udgaman.com.vachak2.model;

/**
 * Created by shivamawasthi on 8/25/16.
 */

public class Country {

    private int id;
    private String numberCode;
    private String twoLetterCode;
    private String threeLetterCode;
    private String name;

    private User user;

    public Country(){

    }



    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }



    public String getNumberCode() {
        return numberCode;
    }
    public void setNumberCode(String numberCode) {
        this.numberCode = numberCode;
    }
    public String getTwoLetterCode() {
        return twoLetterCode;
    }
    public void setTwoLetterCode(String twoLetterCode) {
        this.twoLetterCode = twoLetterCode;
    }
    public String getThreeLetterCode() {
        return threeLetterCode;
    }
    public void setThreeLetterCode(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    public User getUser() {
        return user;
    }



    public void setUser(User user) {
        this.user = user;
    }



    @Override
    public String toString(){
        return this.name;
    }
}
