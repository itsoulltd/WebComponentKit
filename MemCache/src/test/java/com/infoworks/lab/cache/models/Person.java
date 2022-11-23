package com.infoworks.lab.cache.models;

import com.it.soul.lab.sql.entity.Entity;

public class Person extends Entity {
    private String name;
    private int age;
    private String gender;
    private String email;

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Person setAge(int age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Person setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Person setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return getName() + ":" + getEmail();
    }
}
