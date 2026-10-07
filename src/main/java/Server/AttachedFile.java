package Server;

import java.io.Serializable;

public class AttachedFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] data;

    public AttachedFile(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public long getSize() {
        return data.length;
    }
}