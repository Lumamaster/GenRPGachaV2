package com.github.lumamaster.genrpgacha;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class User implements Serializable {
    private static final long serialVersionUID = 11L;
    private String id;
    private int summoningcurrency;
    private int guaranteedcurrency;
    private boolean daily;
    private boolean hasfavorite;
    private ArrayList<Integer> characterlist;
    private Integer favorite;
    private long lastcommand;

    private User() { //constructor
        summoningcurrency = 0;
        guaranteedcurrency = 0;
        favorite = 0;
        lastcommand = 0;
        hasfavorite = false;
        characterlist = new ArrayList<>(1);
        daily = true;
        id = "";
    }

    User(String id) {
        this();
        this.id = id;
    }

    public void madecommand() {
        lastcommand = System.currentTimeMillis();
    }

    public void sortCharacters() {
        characterlist.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public boolean canMakeCommand() {
        return System.currentTimeMillis() - lastcommand > 10000;
    }

    public int timetonextCommand() {
        return (int)((System.currentTimeMillis() - lastcommand)/1000);
    }

    ArrayList<Integer> getCharacters() {
        return characterlist;
    }

    boolean hasFavoriteCharacter() { return hasfavorite; }

    Integer getFavorite() {
        return favorite;
    }

    void addCharacter(Integer i) {
        characterlist.add(i);
    }

    void removeCharacter(int i) {
        characterlist.remove(i-1);
    }

    boolean hasCharacter(int i) {
        int a;
        for (a = 0; a < characterlist.size(); a++) {
            if (characterlist.get(a) == i) {
                return true;
            }
        }
        return false;
    }

    void setFavorite(int id) {
        if (hasCharacter(id)) {
            favorite = id;
        }
        hasfavorite = true;
    }

    void addSummoningCurrency(int amount) {
        summoningcurrency += amount;
    }

    int getSummoningCurrency() {
        return summoningcurrency;
    }

    void addGuaranteedCurrency(int amount) {
        guaranteedcurrency += amount;
    }

    int getGuaranteedCurrency() {
        return guaranteedcurrency;
    }

    void getDailyBonus() {
        summoningcurrency += 20;
        daily = false;
    }

    boolean DailyReady() {
        return daily;
    }

    void resetDaily() {
        daily = true;
    }

    String getId() {
        return id;
    }
}