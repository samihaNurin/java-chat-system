import java.util.ArrayList;
import java.util.List;

public class User extends Person implements ChatService {

    private String userID;
    private String userName;
    private String status;
    private List<Message> inbox;
    private List<Group> groups;

    public User(String name, int id, String userID, String userName) {
        super(name, id);
        this.userID = userID;
        this.userName = userName;
        this.status = "offline";
        this.inbox = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    @Override
    public void sendMessage(User receiver, String content) {
        Message msg = new Message(this, receiver, content);

        // 🔥 FIX: sender + receiver both store
        this.inbox.add(msg);
        receiver.receiveMessage(msg);
    }

    @Override
    public void receiveMessage(Message msg) {
        inbox.add(msg);
    }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void addGroup(Group group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
    }

    public List<Group> getGroups() { return groups; }

    public String getUserID() { return userID; }
    public String getUsername() { return userName; }
    public List<Message> getInbox() { return inbox; }

    @Override
    public void displayInfo() {
        System.out.println(userName);
    }
}