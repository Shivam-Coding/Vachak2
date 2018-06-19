package udgaman.com.vachak2.model;

import java.io.Serializable;

/**
 * Created by shivamawasthi on 8/29/16.
 */

public class Message implements Serializable{

    private int id;
    private String toPhoneNumber;
    private String fromPhoneNumber;
    private String message;
    private String type;
    private String sent;
    private String delivery;
    private String seen;


    public String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber) {
        this.toPhoneNumber = toPhoneNumber;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
