import java.util.ArrayList;
import java.util.List;

// Admin class - extends User (which already extends Person & implements ChatService)
public class Admin extends User {

    private List<User> managedUsers;

    public Admin(String name, int id, String userID, String userName) {
        super(name, id, userID, userName);
        this.managedUsers = new ArrayList<>();
        setStatus("admin-online");
    }

    // Add a new user to the system
    public void addUser(User user) {
        if (!managedUsers.contains(user)) {
            managedUsers.add(user);
            System.out.println("✅ Admin " + getUsername() + " added user: " + user.getUsername());
        } else {
            System.out.println("⚠️  User " + user.getUsername() + " already exists.");
        }
    }

    // Manage / display all registered users
    public void manageUsers() {
        System.out.println("\n📋 ===== Registered Users (managed by " + getUsername() + ") =====");
        if (managedUsers.isEmpty()) {
            System.out.println("  No users registered yet.");
        } else {
            for (User u : managedUsers) {
                u.displayInfo();
            }
        }
        System.out.println("==========================================================\n");
    }

    public List<User> getManagedUsers() { return managedUsers; }

    @Override
    public void displayInfo() {
        System.out.println("🔑 Admin | ID: " + getUserID()
                + " | Name: " + getUsername()
                + " | Status: " + getStatus()
                + " | Manages: " + managedUsers.size() + " users");
    }
}
