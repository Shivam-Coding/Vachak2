package udgaman.com.vachak2.util;

/**
 * Created by shivamawasthi on 9/3/16.
 */

public class RetrieveInfo {

    private String phoneNumber;
    private String deviceID;
    private String timeZone;
    private int id;
    private String friendNumber;

    public String getFriendNumber() {
        return friendNumber;
    }

    public void setFriendNumber(String friendNumber) {
        this.friendNumber = friendNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
