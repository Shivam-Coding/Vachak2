package udgaman.com.vachak2.model;

/**
 * Created by shivamawasthi on 8/25/16.
 */


import java.util.HashSet;
import java.util.Set;


public class User {


    private int id;
    private String firstName;
    private String lastName;
    private String phone;



    private Country country;

    private String status;
    private String thumbnail;



    public User(){

    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Country getCountry() {
        return country;
    }


    public void setCountry(Country country) {
        this.country = country;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getThumbnail() {
        return thumbnail;
    }


    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


    @Override
    public String toString(){
        return this.firstName+" "+this.lastName;
    }



}