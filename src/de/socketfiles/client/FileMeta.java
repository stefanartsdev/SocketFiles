package de.socketfiles.client;

import java.io.Serializable;

public class FileMeta implements Serializable {

    private String name;
    private String author;
    private double size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

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
