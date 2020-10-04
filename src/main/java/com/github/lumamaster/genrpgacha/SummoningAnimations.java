package com.github.lumamaster.genrpgacha;

import java.util.ArrayList;

class SummoningAnimations {
    private ArrayList<String> threestar = new ArrayList<>();
    private ArrayList<String> fourstar = new ArrayList<>();
    private ArrayList<String> fivestar = new ArrayList<>();
    private ArrayList<String> fivefakeout = new ArrayList<>();
    private ArrayList<String> fivecucked = new ArrayList<>();
    private ArrayList<String> six = new ArrayList<>();
    SummoningAnimations() {
        threestar.add("```<*>```");
        threestar.add("```<**>```");
        threestar.add("```<***>```");

        fourstar.add("```<*>```");
        fourstar.add("```<**>```");
        fourstar.add("```<***>```");
        fourstar.add("```<****>```");

        fivestar.add("```<*>```");
        fivestar.add("```<**>```");
        fivestar.add("```<***>```");
        fivestar.add("```<****>```");
        fivestar.add("```<*****>```");

        fivefakeout.add("```<*>```");
        fivefakeout.add("```<**>```");
        fivefakeout.add("```<***>```");
        fivefakeout.add("```<****>```");
        fivefakeout.add("```<****>```");
        fivefakeout.add("```<****>```");
        fivefakeout.add("```<*****>```");


        fivecucked.add("```<*>```");
        fivecucked.add("```<**>```");
        fivecucked.add("```<***>```");
        fivecucked.add("```<****>```");
        fivecucked.add("```<*****>```");
        fivecucked.add("```<*****>```");
        fivecucked.add("```<****>```");

        six.add("```<*>```");
        six.add("```<**>```");
        six.add("```<***>```");
        six.add("```<****>```");
        six.add("```<****>```");
        six.add("```<****>```");
        six.add("```<*****>```");
        six.add("```<*****>```");
        six.add("```<*****>```");
        six.add("```<*****>```");
        six.add("```<******> (:O)```");
        six.add("```<******> (:O)```");
    }
    ArrayList<String> getThreeStarAnimation() {
        return threestar;
    }
    ArrayList<String> getFourStarAnimation() {
        if ((int)(Math.random() * 50) == 0) {
            return fivecucked;
        } else {
            return fourstar;
        }
    }
    ArrayList<String> getFiveStarAnimation(){
        if ((int)(Math.random() * 50) == 0) {
            return fivefakeout;
        } else {
            return fivestar;
        }
    }
    ArrayList<String> getSixStarAnimation() {
        return six;
    }
}
