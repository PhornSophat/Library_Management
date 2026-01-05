package com.library.library_system.model;

public class Member {

    private Long id;

    private String name;
    private String email;
    private String phone;

    private Status status;

    // Default constructor (required by JPA)
    public Member() {}

    // Constructor with fields
    public Member(String name, String email, String phone, Status status) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Enum for member status
    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
