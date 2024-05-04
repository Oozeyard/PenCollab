package com.example.pencollab;

public class Picture {

    final long id;
    long cpt = 0;
    String title;

    public Picture(String title) {
        this.id = cpt; cpt++;
        this.title = title;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public long getId() { return id; }
}
