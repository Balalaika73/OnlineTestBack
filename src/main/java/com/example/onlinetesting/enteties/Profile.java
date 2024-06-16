package com.example.onlinetesting.enteties;

public class Profile {
    private Person person;
    private int useDays;

    public Profile() {
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getUseDays() {
        return useDays;
    }

    public void setUseDays(int useDays) {
        this.useDays = useDays;
    }
}
