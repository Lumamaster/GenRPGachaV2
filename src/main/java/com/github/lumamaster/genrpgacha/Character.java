package com.github.lumamaster.genrpgacha;

import java.io.Serializable;

public class Character implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String imagelink;
    private String summonquote;
    private int rarity;
    private int id;

    public Character() { //constructor
        description = "";
        imagelink = "";
        rarity = 3;
        id = 0;
        summonquote = "";
    }


    Character(String name, int id) {
        this();
        this.id = id;
        this.name = name;
    }

    void setID(int i) {
        id = i;
    }

    int getID() { return id; }

    void setImageLink(String s) {
        imagelink = s;
    }

    String getImageLink() {return imagelink; }

    void setRarity(int i) {rarity = i;}

    int getRarity() {return rarity;}

    void setSummonquote(String s) { summonquote = s; }

    public String getSummonquote() { return summonquote; }

    String changeName(String n) { //changes name of character
        name = n;
        return name + " is now named " + n + ".\n";
    }

    public String getName() {
        return name;
    }

    String setDescription(String s) { //changes description of character
        description = s;
        return name + "'s description has been changed.\n";
    }

    String getDescription() {
        return description;
    }


}