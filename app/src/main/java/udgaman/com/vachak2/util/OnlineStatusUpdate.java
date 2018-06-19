package udgaman.com.vachak2.util;

/**
 * Created by shivamawasthi on 9/5/16.
 */

public class OnlineStatusUpdate {

    private String phonenumber;
    private String status;
    private String friend;
    private String friendStatus;

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
