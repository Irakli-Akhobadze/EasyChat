package Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sender;
    private String receiver;
    private String message;

    private List<AttachedFile> files;

    public ChatMessage(
            String sender,
            String receiver,
            String message,
            List<AttachedFile> files) {

        this.sender = sender;
        this.receiver = receiver;
        this.message = message;

        this.files = new ArrayList<>(files);
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public List<AttachedFile> getFiles() {
        return files;
    }
}