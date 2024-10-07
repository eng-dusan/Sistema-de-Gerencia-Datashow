package com.example.atividade;

public class Teacher {
    private String id;
    private String ra;
    private String username;
    private String subject;

    public Teacher(String id, String ra, String username, String subject) {
        this.id = id;
        this.ra = ra;
        this.username = username;
        this.subject = subject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
