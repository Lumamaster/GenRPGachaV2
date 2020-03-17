package com.github.lumamaster.genrpgacha;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Banner implements Serializable {
    private static final long serialVersionUID = 14L;
    private String name;
    private int id;
    private ArrayList<Integer> threestar = new ArrayList<>(1);
    private ArrayList<Integer> fourstar = new ArrayList<>(1);
    private ArrayList<Integer> fivestar = new ArrayList<>(1);
    private ArrayList<Integer> focus = new ArrayList<>(1);
    private int threerate = 52;
    private int fourrate = 40;
    private int fiverate = 3;
    private int focusrate = 5;
    private boolean enable;
    Banner()
    {
    }

    Banner(String n, int i) {
        name = n;
        id = i;
        enable = false;
    }

    public String getName() {
        return name;
    }

    void changeName(String a) {name = a;}

    void setID(int i) {
        id = i;
    }

    int getID() { return id; }

    int hasUnit(int s) {
        int i;
        for (i = 0; i < threestar.size(); i++) {
            if (threestar.get(i) == s) {
                return 3;
            }
        }
        for (i = 0; i < fourstar.size(); i++) {
            if (fourstar.get(i) == s) {
                return 4;
            }
        }
        for (i = 0; i < fivestar.size(); i++) {
            if (fivestar.get(i) == s) {
                return 5;
            }
        }
        for (i = 0; i < focus.size(); i++) {
            if (focus.get(i) == s) {
                return 6;
            }
        }
        return 0;
    }

    boolean addtothreestar(Character c) {
        try {
            threestar.add(c.getID());
            threestar.sort((o1, o2) -> {
                if (o1 > o2) {
                    return 1;
                }
                else if (o1.equals(o2)) {
                    return 0;
                }
                else {
                    return -1;
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean removefromthreestar(int i) {
        if (hasUnit(i) == 3) {
            int a;
            for (a = 0; a < threestar.size(); a++) {
                if (threestar.get(a) == i) {
                    threestar.remove(a);
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    boolean addtofourstar(Character c) {
        try {
            fourstar.add(c.getID());
            fourstar.sort((o1, o2) -> {
                if (o1 > o2) {
                    return 1;
                }
                else if (o1.equals(o2)) {
                    return 0;
                }
                else {
                    return -1;
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean removefromfourstar(int i) {
        if (hasUnit(i) == 4) {
            int a;
            for (a = 0; a < fourstar.size(); a++) {
                if (fourstar.get(a) == i) {
                    fourstar.remove(a);
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    boolean addtofivestar(Character c) {
        try {
            fivestar.add(c.getID());
            fivestar.sort((o1, o2) -> {
                if (o1 > o2) {
                    return 1;
                }
                else if (o1.equals(o2)) {
                    return 0;
                }
                else {
                    return -1;
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean removefromfivestar(int i) {
        if (hasUnit(i) == 5) {
            int a;
            for (a = 0; a < fivestar.size(); a++) {
                if (fivestar.get(a) == i) {
                    fivestar.remove(a);
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    boolean addtofocus(Character c) {
        try {
            focus.add(c.getID());
            focus.sort((o1, o2) -> {
                if (o1 > o2) {
                    return 1;
                }
                else if (o1.equals(o2)) {
                    return 0;
                }
                else {
                    return -1;
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean removefromfocus(int i) {
        if (hasUnit(i) == 6) {
            int a;
            for (a = 0; a < focus.size(); a++) {
                if (focus.get(a) == i) {
                    focus.remove(a);
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    int[] getRates() {
        int[] rates = new int[4];
        rates[0] = threerate;
        rates[1] = fourrate;
        rates[2] = fiverate;
        rates[3] = focusrate;
        return rates;
    }

    void setthreestarrate(int r) {
        threerate = r;
    }

    void setfourstarrate(int r) {
        fourrate = r;
    }

    void setfivestarrate(int r) {
        fiverate = r;
    }

    void setfocusrate(int r) {
        focusrate = r;
    }

    ArrayList<Integer> getThreeStars() { return threestar; }

    ArrayList<Integer> getFourStars() { return fourstar; }

    ArrayList<Integer> getFiveStars() { return fivestar; }

    ArrayList<Integer> getFocus() { return focus; }

    void enable(boolean b) { enable = b; }

    boolean isEnabled() { return enable; }

    Integer singlepull() {
        int starcount = (int)(Math.random() * 100);
        int choice;
        if (starcount > threerate) {
            if (starcount > fourrate + threerate) {
                if (starcount > fiverate + fourrate + threerate) {
                    choice = (int)(Math.random() * focus.size());
                    return focus.get(choice);
                } else {
                    choice = (int)(Math.random() * fivestar.size());
                    return fivestar.get(choice);
                }
            } else {
                choice = (int)(Math.random() * fourstar.size());
                return fourstar.get(choice);
            }
        } else {
            choice = (int)(Math.random() * threestar.size());
            return threestar.get(choice);
        }
    }

    ArrayList<Integer> tenpull() {
        int starcount;
        int choice;
        ArrayList<Integer> result = new ArrayList<>();
        int i;
        for (i = 0; i < 10; i++) {
            starcount = (int)(Math.random() * 100);
            if (starcount > threerate) {
                if (starcount > fourrate + threerate) {
                    if (starcount > fiverate + fourrate + threerate) {
                        choice = (int) (Math.random() * focus.size());
                        result.add(focus.get(choice));
                    } else {
                        choice = (int) (Math.random() * fivestar.size());
                        result.add(fivestar.get(choice));
                    }
                } else {
                    choice = (int) (Math.random() * fourstar.size());
                    result.add(fourstar.get(choice));
                }
            } else {
                choice = (int) (Math.random() * threestar.size());
                result.add(threestar.get(choice));
            }
        }
        return result;
    }

    Integer rarepull() {
        int starcount = (int) (Math.random() * (fourrate + fiverate + focusrate));
        int choice;
        if (starcount > fourrate) {
            if (starcount > fiverate + fourrate) {
                choice = (int) (Math.random() * focus.size());
                return focus.get(choice);
            } else {
                choice = (int) (Math.random() * fivestar.size());
                return fivestar.get(choice);
            }
        } else {
            choice = (int) (Math.random() * fourstar.size());
            return fourstar.get(choice);
        }
    }

    Integer guaranteepull() {
        int choice = (int) (Math.random() * focus.size());
        return focus.get(choice);
    }
}
