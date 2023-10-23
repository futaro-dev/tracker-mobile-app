package com.example.trackerapp.data.model;

public class Item {
    String id;
    String image;
    String name;
    String notes;
    String status;
    String type;
    int progress;
    int score;


    public Item() {}

    public Item(String id, String name, String notes, String status, String type, int progress, int score) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.status = status;
        this.type = type;
        this.progress = progress;
        this.score = score;
    }

    public Item(String id, String name, String status, int progress, int score) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.progress = progress;
        this.score = score;
    }

    public Item(String name, String status, int progress, int score) {
        this.name = name;
        this.status = status;
        this.progress = progress;
        this.score = score;
    }

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
