package com.github.lumamaster.genrpgacha;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Calendar;
import java.util.Date;

class Storage {
    private ArrayList<Character> charlist = new ArrayList<>(200);
    private ArrayList<User> userlist = new ArrayList<>(200);
    private ArrayList<Banner> bannerlist = new ArrayList<>(200);
    private int characterid = 1;
    private int bannerid = 1;
    Calendar lastcommand = Calendar.getInstance();

    void madeCommand() {
        Calendar temp = Calendar.getInstance();
        temp.setTime(new Date());
        if (temp.get(Calendar.DAY_OF_YEAR) != lastcommand.get(Calendar.DAY_OF_YEAR)) {
            ResetDaily();
        }
        lastcommand = temp;
    }

    String timeToReset() {
        System.out.println("attempting to get current hour");
        Calendar temp = Calendar.getInstance();
        temp.setTime(new Date());
        System.out.println("date found");
        return Integer.toString(temp.get(Calendar.HOUR_OF_DAY));
    }

    int getnextCharacterID() {
        charlist.sort(Comparator.comparingInt(Character::getID));
        for (int a = 0; a < charlist.size(); a++) {
            if (charlist.get(a).getID() != a + 1) {
                return a + 1;
            }
        }
        return characterid;
    }
    int getnextBannerID() {
        bannerlist.sort(Comparator.comparingInt(Banner::getID));
        for (int a = 0; a < bannerlist.size(); a++) {
            if (bannerlist.get(a).getID() != a + 1) {
                return a + 1;
            }
        }
        return bannerid;
    }
    void ResetDaily() {
        int i;
        for (i = 0; i < userlist.size(); i++) {
            userlist.get(i).resetDaily();
            userlist.get(i).addSummoningCurrency(10);
        }
    }

    void addCharacter(Character c) {
        charlist.add(c);
        characterid = charlist.size() + 1;
    }
    void addUser(User c) {
        userlist.add(c);
    }
    void addBanner(Banner c) {
        bannerlist.add(c);
        bannerid = bannerlist.size() + 1;
    }
    boolean hasCharacter(String s) {
        for (Character character : charlist) {
            if (character.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }
    boolean hasCharacterID(int i) {
        for (Character character : charlist) {
            if (character.getID() == i) {
                return true;
            }
        }
        return false;
    }
    boolean hasUser(String s) {
        for (User user : userlist) {
            if (user.getId().equals(s)) {
                return true;
            }
        }
        return false;
    }
    boolean hasBanner(String s) {
        for (Banner banner : bannerlist) {
            if (banner.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }
    boolean hasBannerID(int i) {
        for (Banner banner : bannerlist) {
            if (banner.getID() == i) {
                return true;
            }
        }
        return false;
    }
    boolean userAlreadyRegistered(String id) {
        int i;
        for (i = 0; i < userlist.size(); i++) {
            if (userlist.get(i).getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    Character getCharacterByID(int i) {
        for (Character character : charlist) {
            if (character.getID() == i) {
                return character;
            }
        }
        return null;
    }
    User getUser(String id) {
        for (User user : userlist) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
    Banner getBanner(String s) {
        for (Banner banner : bannerlist) {
            if (banner.getName().equals(s)) {
                return banner;
            }
        }
        return null;
    }
    Banner getBannerbyID(int i) {
        for (Banner banner : bannerlist) {
            if (banner.getID() == i) {
                return banner;
            }
        }
        return null;
    }
    void removeCharacter(String s) {
        System.out.println("Character " + s + " deleted.");
        for (Character character : charlist) {
            if (character.getName().equals(s)) {
                charlist.remove(character);
                charlist.sort(Comparator.comparingInt(Character::getID));
                for (int a = 0; a < charlist.size(); a++) {
                    if (charlist.get(a).getID() != a + 1) {
                        charlist.get(a).setID(a);
                    }
                }
            }
        }
    }
    void removeUser(String s) {
        System.out.println("User " + s + " deleted.");
        for (int i = 0; i < userlist.size(); i++) {
            if (userlist.get(i).getId().equals(s)) {
                userlist.remove(userlist.get(i));
                saveData();
            }
        }
    }
    void removeBanner(String s) {
        System.out.println("Banner " + s + " deleted.");
        for (int i = 0; i < bannerlist.size(); i++) {
            if (bannerlist.get(i).getName().equals(s)) {
                bannerlist.remove(bannerlist.get(i));
                bannerlist.sort(Comparator.comparingInt(Banner::getID));
                for (int a = 0; a < bannerlist.size(); a++) {
                    if (bannerlist.get(a).getID() != a + 1) {
                        bannerlist.get(a).setID(a);
                    }
                }
                saveData();
            }
        }
    }
    ArrayList<Character> getcharlist() { return charlist; }
    ArrayList<User> getuserlist() { return userlist; }
    ArrayList<Banner> getbannerlist() {
        return bannerlist;
    }
    void saveData() {
        int i;
        try {
            File[] charfiles = new File(System.getProperty("user.dir") + "/characters/").listFiles();
            for (File file : charfiles) {
                file.delete();
            }
            for (i = 0; i < charlist.size(); i++) {
                File f = new File(System.getProperty("user.dir") + "/characters/" + charlist.get(i).getName());
                f.getParentFile().mkdirs();
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileOutputStream fout = new FileOutputStream(System.getProperty("user.dir") + "/characters/" + charlist.get(i).getName());
                ObjectOutputStream oout = new ObjectOutputStream(fout);
                oout.writeObject(charlist.get(i));
                oout.close();
            }
            File[] userfiles = new File(System.getProperty("user.dir") + "/users/").listFiles();
            for (File file : userfiles) {
                file.delete();
            }
            for (i = 0; i < userlist.size(); i++) {
                File f = new File(System.getProperty("user.dir") + "/users/" + userlist.get(i).getId());
                f.getParentFile().mkdirs();
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileOutputStream fout = new FileOutputStream(System.getProperty("user.dir") + "/users/" + userlist.get(i).getId());
                ObjectOutputStream oout = new ObjectOutputStream(fout);
                oout.writeObject(userlist.get(i));
                oout.close();
            }
            File[] bannerfiles = new File(System.getProperty("user.dir") + "/banners/").listFiles();
            for (File file : bannerfiles) {
                file.delete();
            }
            for (i = 0; i < bannerlist.size(); i++) {
                File f = new File(System.getProperty("user.dir") + "/banners/" + bannerlist.get(i).getName());
                f.getParentFile().mkdirs();
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileOutputStream fout = new FileOutputStream(System.getProperty("user.dir") + "/banners/" + bannerlist.get(i).getName());
                ObjectOutputStream oout = new ObjectOutputStream(fout);
                oout.writeObject(bannerlist.get(i));
                oout.close();
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
    void loadData()
    {
        try {
            System.out.println("Loading characters.");
            File[] charfiles = new File(System.getProperty("user.dir") + "/characters/").listFiles();
            charlist = new ArrayList<>(charfiles.length + 200);
            for (File file : charfiles) {
                FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + "/characters/" + file.getName());
                ObjectInputStream oin = new ObjectInputStream(fin);
                charlist.add((Character)oin.readObject());
                oin.close();
            }
            System.out.println("Loading users.");
            File[] userfiles = new File(System.getProperty("user.dir") + "/users/").listFiles();
            userlist = new ArrayList<>(userfiles.length + 200);
            for (File file : userfiles) {
                FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + "/users/" + file.getName());
                ObjectInputStream oin = new ObjectInputStream(fin);
                userlist.add((User)oin.readObject());
                oin.close();
            }
            System.out.println("Loading banners.");
            File[] bannerfiles = new File(System.getProperty("user.dir") + "/banners/").listFiles();
            bannerlist = new ArrayList<>(bannerfiles.length + 200);
            for (File file : bannerfiles) {
                FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + "/banners/" + file.getName());
                ObjectInputStream oin = new ObjectInputStream(fin);
                bannerlist.add((Banner) oin.readObject());
                oin.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        } catch (NullPointerException ignored) {
        }
        charlist.sort(Comparator.comparingInt(Character::getID));
        bannerlist.sort(Comparator.comparingInt(Banner::getID));
        int i;
        System.out.println("Characters loaded: " + charlist.size());
        for (i = 0; i < charlist.size(); i++) {
            System.out.println(charlist.get(i).getName());
        }
        System.out.println("Users loaded: " + userlist.size());
        for (i = 0; i < userlist.size(); i++) {
            System.out.println(userlist.get(i).getId());
        }
        System.out.println("Banners loaded: " + bannerlist.size());
        for (i = 0; i < bannerlist.size(); i++) {
            System.out.println(bannerlist.get(i).getName());
        }
        characterid = charlist.size() + 1;
        bannerid = bannerlist.size() + 1;
    }
}
