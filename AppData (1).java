import java.util.ArrayList;
import java.util.List;

// Central data store – all users, groups, messages live here
public class AppData {

    public static final List<User>    users    = new ArrayList<>();
    public static final List<Group>   groups   = new ArrayList<>();
    public static User                loggedIn = null;

    public static void init() {
        // Create users
        User alice = new User("Alice",   1, "USR001", "alice");
        User bob   = new User("Bob",     2, "USR002", "bob99");
        User carol = new User("Carol",   3, "USR003", "carol_c");
        User dave  = new User("Dave",    4, "USR004", "dave_d");

        alice.setStatus("online");
        bob.setStatus("online");
        carol.setStatus("away");
        dave.setStatus("offline");

        users.add(alice);
        users.add(bob);
        users.add(carol);
        users.add(dave);

        // Create groups
        Group javaClass   = new Group("GRP001", "Java OOP Class");
        Group projectTeam = new Group("GRP002", "CEA Project Team");
        Group studyGroup  = new Group("GRP003", "Evening Study");

        javaClass.addMember(alice);
        javaClass.addMember(bob);
        javaClass.addMember(carol);
        javaClass.addMember(dave);

        projectTeam.addMember(alice);
        projectTeam.addMember(bob);
        projectTeam.addMember(dave);

        studyGroup.addMember(carol);
        studyGroup.addMember(alice);

        groups.add(javaClass);
        groups.add(projectTeam);
        groups.add(studyGroup);

        // Seed some messages
        alice.sendMessage(bob,   "Hey Bob! Ready for the project?");
        bob.sendMessage(alice,   "Yes! Let's go ");
        alice.sendMessage(carol, "Carol, did you finish the UML?");
        carol.sendMessage(alice, "Almost done!");

        javaClass.sendGroupMessage(alice,   "Class reminder: submission tomorrow!");
        projectTeam.sendGroupMessage(bob,   "Please push your code tonight.");
        studyGroup.sendGroupMessage(carol,  "Study session at 8 PM!");

        // Default logged-in user
        loggedIn = alice;
    }

    public static User findUser(String username) {
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        return null;
    }
}
