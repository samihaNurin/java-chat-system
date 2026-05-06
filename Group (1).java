import java.util.ArrayList;

// Group class
public class Group {

    private String groupID;
    private String groupName;
    private ArrayList<User> members;

    // 🔥 NEW: message store করার জন্য list
    private ArrayList<Message> groupMessages;

    public Group(String groupID, String groupName) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.members = new ArrayList<>();
        this.groupMessages = new ArrayList<>(); //
    }

    // Add a member to this group
    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            user.addGroup(this);
        }
    }

    //  message save + send
    public void sendGroupMessage(User sender, String content) {

        Message msg = new Message(sender, null, "[" + groupName + "] " + content);

        // 👉 store message
        groupMessages.add(msg);

        // 👉 send to all
        for (User member : members) {
            if (!member.equals(sender)) {
                member.receiveMessage(msg);
            }
        }
    }

    // 🔥 NEW METHOD (THIS FIXES YOUR ERROR)
    public ArrayList<Message> getGroupMessages() {
        return groupMessages;
    }

    // --- Getters ---
    public String getGroupID() { return groupID; }
    public String getGroupName() { return groupName; }
    public ArrayList<User> getMembers() { return members; }
}