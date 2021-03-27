package de.socketfiles.client;

import java.io.Serializable;

public class SocketFile implements Serializable {
    private FileMeta meta;
    private byte[] data;

    public SocketFile(String name, String author, byte[] data) {
        meta = new FileMeta(name, author, data.length);
        this.data = data;
    }

    public SocketFile(String name, String author, long size) {
        meta = new FileMeta(name, author, size);
    }

    public FileMeta getMeta() {
        return meta;
    }

    public void setMeta(FileMeta meta) {
        this.meta = meta;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
