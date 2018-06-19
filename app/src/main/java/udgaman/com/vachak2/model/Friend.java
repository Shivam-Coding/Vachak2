package udgaman.com.vachak2.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by shivamawasthi on 8/19/16.
 */

public class Friend {
    private String name;
    private String number;
    private String status;
    private String userNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    @Override
    public boolean equals(Object o) {
        Friend friend = (Friend)o;
        return this.number.equals(friend.getNumber());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.number != null ? this.number.hashCode() : 0);
        return hash;
    }

}
