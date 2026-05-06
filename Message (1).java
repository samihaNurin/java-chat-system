import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Message class - links sender and receiver
public class Message {
    private User sender;
    private User receiver;
    private String content;
    private LocalDateTime timestamp;

    public Message(User sender, User receiver, String content) {
        this.sender   = sender;
        this.receiver = receiver;
        this.content  = content;
        this.timestamp = LocalDateTime.now();
    }

    public User getSender()    { return sender; }
    public User getReceiver()  { return receiver; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[" + timestamp.format(fmt) + "] "
                + sender.getUsername() + " → " + receiver.getUsername()
                + ": " + content;
    }
}
