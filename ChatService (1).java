// Interface - ChatService
public interface ChatService {
    void sendMessage(User receiver, String content);
    void receiveMessage(Message msg);
}
