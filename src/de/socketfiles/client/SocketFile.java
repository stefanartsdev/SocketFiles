package de.socketfiles.client;

import java.io.Serializable;

/**
 * Wrapper class for a file to implement file information ({@link de.socketfiles.client.SocketFile})
 */
public class SocketFile implements Serializable {
    /**
     * File meta information
     */
    private FileMeta meta;

    /**
     * File content
     */
    private byte[] data;

    /**
     * Constructor for the socket file
     * @param name file name
     * @param author file author
     * @param data file content
     */
    public SocketFile(String name, String author, byte[] data) {
        meta = new FileMeta(name, author, data.length);
        this.data = data;
    }

    /**
     * Alternative constructor the create a SocketFile without content
     * @param name file name
     * @param author file author
     * @param size file size
     */
    public SocketFile(String name, String author, long size) {
        meta = new FileMeta(name, author, size);
    }

    /**
     * Getter for the file meta
     * @return file meta
     */
    public FileMeta getMeta() {
        return meta;
    }

    /**
     * Setter for the file meta
     * @param meta file meta
     */
    public void setMeta(FileMeta meta) {
        this.meta = meta;
    }

    /**
     * Getter for the file content
     * @return file content
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Setter for the file content
     * @param data file content
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
