import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChatController implements Initializable {

    // ── FXML fields ──────────────────────────────────────────────────
    @FXML private Label       loggedInLabel;
    @FXML private TextField   searchField;
    @FXML private Button      tabUsers, tabGroups;
    @FXML private ListView<String> contactList;
    @FXML private Label       statusLabel;

    @FXML private Label       chatAvatar, chatTitle, chatSubtitle;
    @FXML private ScrollPane  scrollPane;
    @FXML private VBox        messageBox;
    @FXML private TextField   inputField;

    @FXML private Label       infoName, infoID, infoStatus, infoExtra;
    @FXML private ListView<String> infoList;

    // ── State ─────────────────────────────────────────────────────────
    private boolean  showingUsers = true;
    private Object   selectedTarget = null;   // User or Group

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    // ── Init ──────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loggedInLabel.setText(AppData.loggedIn.getUsername());
        loadContacts();

        searchField.textProperty().addListener((o, old, nw) -> filterContacts(nw));
    }

    // ── Tab switching ─────────────────────────────────────────────────
    @FXML private void showUsers() {
        showingUsers = true;
        tabUsers.getStyleClass().add("tab-active");
        tabGroups.getStyleClass().remove("tab-active");
        loadContacts();
    }

    @FXML private void showGroups() {
        showingUsers = false;
        tabGroups.getStyleClass().add("tab-active");
        tabUsers.getStyleClass().remove("tab-active");
        loadContacts();
    }

    private void loadContacts() {
        contactList.getItems().clear();
        if (showingUsers) {
            for (User u : AppData.users) {
                if (!u.equals(AppData.loggedIn))
                    contactList.getItems().add(statusIcon(u.getStatus()) + "  " + u.getUsername());
            }
        } else {
            for (Group g : AppData.groups) {
                contactList.getItems().add("👥  " + g.getGroupName() + "  [" + g.getGroupID() + "]");
            }
        }
    }

    private void filterContacts(String query) {
        contactList.getItems().clear();
        if (showingUsers) {
            AppData.users.stream()
                    .filter(u -> !u.equals(AppData.loggedIn) &&
                            u.getUsername().toLowerCase().contains(query.toLowerCase()))
                    .forEach(u -> contactList.getItems()
                            .add(statusIcon(u.getStatus()) + "  " + u.getUsername()));
        } else {
            AppData.groups.stream()
                    .filter(g -> g.getGroupName().toLowerCase().contains(query.toLowerCase()))
                    .forEach(g -> contactList.getItems()
                            .add("👥  " + g.getGroupName() + "  [" + g.getGroupID() + "]"));
        }
    }

    // ── Contact selected ──────────────────────────────────────────────
    @FXML private void onContactSelected() {
        String selected = contactList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (showingUsers) {
            // Extract username (after the icon + 2 spaces)
            String uname = selected.substring(3).trim();
            User user = AppData.findUser(uname);
            if (user != null) {
                selectedTarget = user;
                openUserChat(user);
            }
        } else {
            // Match group by name
            String raw = selected.substring(4).trim(); // after "👥  "
            String gname = raw.contains("[") ? raw.substring(0, raw.indexOf("[")).trim() : raw;
            for (Group g : AppData.groups) {
                if (g.getGroupName().equals(gname)) {
                    selectedTarget = g;
                    openGroupChat(g);
                    break;
                }
            }
        }
    }

    // ── Open 1-to-1 chat ──────────────────────────────────────────────
    private void openUserChat(User user) {
        chatAvatar.setText(String.valueOf(user.getUsername().charAt(0)).toUpperCase());
        chatTitle.setText(user.getUsername());
        chatSubtitle.setText(statusIcon(user.getStatus()) + " " + user.getStatus());

        // Info panel
        infoName.setText(user.getUsername());
        infoID.setText("ID: " + user.getUserID());
        infoStatus.setText("Status: " + user.getStatus());
        infoExtra.setText("Groups: " + user.getGroups().size());
        infoList.getItems().setAll(
                user.getGroups().stream()
                        .map(g -> "• " + g.getGroupName())
                        .collect(Collectors.toList())
        );

        // Load conversation
        messageBox.getChildren().clear();
        List<Message> conv = getConversation(AppData.loggedIn, user);
        for (Message m : conv) renderMessage(m);
        scrollToBottom();
    }

    // ── Open group chat ───────────────────────────────────────────────
    private void openGroupChat(Group group) {
        chatAvatar.setText("G");
        chatTitle.setText(group.getGroupName());
        chatSubtitle.setText("Group ID: " + group.getGroupID()
                + "  •  " + group.getMembers().size() + " members");

        // Info panel
        infoName.setText(group.getGroupName());
        infoID.setText("ID: " + group.getGroupID());
        infoStatus.setText("Members: " + group.getMembers().size());
        infoExtra.setText("");
        infoList.getItems().setAll(
                group.getMembers().stream()
                        .map(u -> statusIcon(u.getStatus()) + " " + u.getUsername())
                        .collect(Collectors.toList())
        );

        // Load group messages
        messageBox.getChildren().clear();
        for (Message m : group.getGroupMessages()) renderMessage(m);
        scrollToBottom();
    }

    // ── Send message ──────────────────────────────────────────────────
    @FXML
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || selectedTarget == null) return;

        if (selectedTarget instanceof User) {
            User receiver = (User) selectedTarget;

            // 1. User message send
            AppData.loggedIn.sendMessage(receiver, text);

            // 2. Auto reply generate
            String reply = getAutoReply(text);

            // 3. Receiver (bot/user) reply back
            receiver.sendMessage(AppData.loggedIn, reply);

            // 4. Reload full conversation
            List<Message> conv = fullConversation(AppData.loggedIn, receiver);
            messageBox.getChildren().clear();
            for (Message m : conv) {
                renderMessage(m);
            }

        } else if (selectedTarget instanceof Group) {
            Group group = (Group) selectedTarget;

            // group message
            group.sendGroupMessage(AppData.loggedIn, text);

            messageBox.getChildren().clear();
            for (Message m : group.getGroupMessages()) {
                renderMessage(m);
            }
        }

        inputField.clear();
        scrollToBottom();
    }
    // ── Render a single message bubble ───────────────────────────────
    private void renderMessage(Message m) {
        boolean mine = m.getSender().equals(AppData.loggedIn);

        // Bubble text
        Text content = new Text(m.getContent());
        content.getStyleClass().add("bubble-text");

        Text time = new Text("  " + m.getTimestamp().format(FMT));
        time.getStyleClass().add("bubble-time");

        TextFlow bubble = new TextFlow(content, time);
        bubble.getStyleClass().add(mine ? "bubble-mine" : "bubble-other");
        bubble.setMaxWidth(420);

        // Sender name (for group / other side)
        HBox row = new HBox();
        if (mine) {
            row.setAlignment(Pos.CENTER_RIGHT);
            row.getChildren().add(bubble);
        } else {
            VBox wrap = new VBox(2);
            Label senderLbl = new Label(m.getSender().getUsername());
            senderLbl.getStyleClass().add("sender-label");
            wrap.getChildren().addAll(senderLbl, bubble);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().add(wrap);
        }
        messageBox.getChildren().add(row);
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private List<Message> getConversation(User a, User b) {
        return a.getInbox().stream()
                .filter(m -> m.getSender().equals(b))
                .collect(Collectors.toList());
        // NOTE: in a real app you'd keep a shared log; for demo we show received msgs
    }

    // Build full conversation from both inboxes
    @SuppressWarnings("unused")
    private List<Message> fullConversation(User a, User b) {
        return java.util.stream.Stream
                .concat(a.getInbox().stream().filter(m -> m.getSender().equals(b)),
                        b.getInbox().stream().filter(m -> m.getSender().equals(a)))
                .sorted(java.util.Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private String statusIcon(String status) {
        return switch (status.toLowerCase()) {
            case "online"  -> "🟢";
            case "away"    -> "🟡";
            case "offline" -> "⚫";
            default        -> "🔵";
        };
    }
    private String getAutoReply(String userMessage) {
        userMessage = userMessage.toLowerCase();

        if (userMessage.contains("hi") || userMessage.contains("hello")) {
            return "Hello! How can I help you?";
        } else if (userMessage.contains("how are you")) {
            return "I'm fine 😊 What about you?";
        } else if (userMessage.contains("bye")) {
            return "Goodbye! Have a nice day!";
        } else {
            return "Sorry, I didn't understand that.";
        }
    }
}
