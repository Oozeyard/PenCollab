package com.example.pencollab;

public class Picture {

    final long id;
    long cpt = 0;
    String title, other;

    public Picture(String title, String other) {
        this.id = cpt; cpt++;
        this.title = title;
        this.other = other;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getOther() { return other; }

    public void setOther(String other) { this.other = other; }

    public long getId() { return id; }
}
