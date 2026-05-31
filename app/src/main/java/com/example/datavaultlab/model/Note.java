package com.example.datavaultlab.model;

public class Note {
    public final int id;
    public final String title;
    public final String content;

    public Note(int id, String title, String content) {
        this.id      = id;
        this.title   = title;
        this.content = content;
    }
}