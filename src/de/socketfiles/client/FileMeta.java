package de.socketfiles.client;

import java.io.Serializable;

/**
 * File meta class
 * Contains information about a file
 */
public class FileMeta implements Serializable {

    private String name;
    private String author;
    private double size;

    /**
     * Getter for the file name
     * @return file name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the file name
     * @param name file name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the file author
     * @return file author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Setter for the file author
     * @param author file author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Getter for the file size
     * @return file size
     */
    public double getSize() {
        return size;
    }

    /**
     * Setter for the file size
     * @param size file size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Constructor for the file meta
     * @param name file name
     * @param author file author
     * @param size file size
     */
    public FileMeta(String name, String author, double size) {
        this.name = name;
        this.author = author;
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", size=" + size +
                '}';
    }
}
