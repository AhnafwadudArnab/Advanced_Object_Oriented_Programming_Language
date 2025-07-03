package com.alerts.system;

import java.time.LocalDate;

public class MissingPerson {
    private int personId; // Unique identifier for DB updates
    private String name;
    private int age;
    private String gender;
    private String lastSeenLocation;
    private LocalDate lastSeenDate;
    private String contactPerson;
    private String contactNumber;
    private String description;
    private String photoPath; // Path to the image file
    private String status; // e.g., "Missing", "Found", "Deceased"
    private LocalDate dateCreated; // Date when the alert was created

    public MissingPerson(int personId, String name, int age, String gender, String lastSeenLocation,
                         LocalDate lastSeenDate, String contactPerson, String contactNumber,
                         String description, String photoPath, String status) {
        this.personId = personId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.lastSeenLocation = lastSeenLocation;
        this.lastSeenDate = lastSeenDate;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.description = description;
        this.photoPath = photoPath;
        this.status = status;
        this.dateCreated = LocalDate.now(); // Set current date as creation date
    }

    public MissingPerson(String name, int age, String gender, String lastSeenLocation,
                         LocalDate lastSeenDate, String contactPerson, String contactNumber,
                         String description, String photoPath, String status) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.lastSeenLocation = lastSeenLocation;
        this.lastSeenDate = lastSeenDate;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.description = description;
        this.photoPath = photoPath;
        this.status = status;
        this.dateCreated = LocalDate.now(); // Set current date as creation date
    }

    public MissingPerson(String name, int age, String gender, String lastSeenLocation,
                         LocalDate lastSeenDate, String contactPerson, String contactNumber,
                         String description, String photoPath, String status, LocalDate dateCreated) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.lastSeenLocation = lastSeenLocation;
        this.lastSeenDate = lastSeenDate;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.description = description;
        this.photoPath = photoPath;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    // Getters
    public int getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getLastSeenLocation() {
        return lastSeenLocation;
    }

    public LocalDate getLastSeenDate() {
        return lastSeenDate;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    // Setters (if mutable, depends on application requirements)
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLastSeenLocation(String lastSeenLocation) {
        this.lastSeenLocation = lastSeenLocation;
    }

    public void setLastSeenDate(LocalDate lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return String.format("MissingPerson{personId=%d, name='%s', age=%d, gender='%s', lastSeenLocation='%s', lastSeenDate=%s, contactPerson='%s', contactNumber='%s', description='%s', photoPath='%s', status='%s', dateCreated=%s}", 
            personId, name, age, gender, lastSeenLocation, lastSeenDate, contactPerson, contactNumber, description, photoPath, status, dateCreated);
    }
}