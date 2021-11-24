package com.example.autofeed.classes;

public class PetInfo {
    private String name, type, breed, gender, weight, id;

    public PetInfo(String name, String type, String breed, String gender, String weight, String id) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.gender = gender;
        this.weight = weight;
        this.id = id;
    }

    public PetInfo(){
        this.setName("Name");
        this.setType("Type");
        this.setBreed("Breed");
        this.setGender("Gender");
        this.setWeight("kg");
        this.setId("id");

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
