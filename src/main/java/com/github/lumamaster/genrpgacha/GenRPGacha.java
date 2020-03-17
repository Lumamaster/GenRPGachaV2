package com.github.lumamaster.genrpgacha;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GenRPGacha {
    static Storage store = new Storage();
    static SummoningAnimations summon = new SummoningAnimations();

    interface Command {
        void execute(MessageCreateEvent event);
    }

    private static final Map<String, Command> commands = new HashMap<>();

    public static String[] parseCommand(String command) {
        String truncated = command.substring(10, command.length() - 1);
        String[] m = truncated.split("\\\"?( |$)(?=(([^\\\"]*\\\"){2})*[^\\\"]*$)\\\"?");
        for (int a = 0; a < m.length; a++) {
            System.out.print(m[a] + " | ");
        }
        int i;
        for (i = 0; i < m.length; i++) {
            if (m[i].charAt(0) == '"' && m[i].charAt(m[i].length() - 1) == '"') {
                m[i] = m[i].substring(1, m[i].length() - 1);
            }
        }
        System.out.println("Command size: " + m.length);
        return m;
    }

    static {
        commands.put("ping", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    event.getMessage().getChannel().block().createMessage("Pong!").block();
                }
            }
        });

        commands.put("mobiletest", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    ArrayList<Member> temp = new ArrayList<>();
                    event.getGuild().block().getMembers().subscribe(i -> temp.add(i));
                    for (int i = 0; i < temp.size(); i++) {
                        System.out.println(temp.get(i).getUsername() + "#" + temp.get(i).getDiscriminator());
                    }
                }
            }
        });

        commands.put("timetest", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    event.getMessage().getChannel().block().createMessage("Pong!").block();
                }
            }
        });

        commands.put("timetoreset", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    try {
                        event.getMessage().getChannel().block().createMessage("Current hour: " + store.timeToReset()).block();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Dev not found! (expected: 136668026404732928) (found: " + senderID + ").");
                }
            } else {
                System.out.println("null member");
            }
        });

        commands.put("register", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                if (store.hasUser(member.getId().asString())) {
                    event.getMessage().getChannel().block().createMessage("You already have a registered account.").block();
                } else {
                    User temp = new User(member.getId().asString());
                    temp.addSummoningCurrency(100);
                    event.getMessage().getChannel().block().createMessage("<@" + member.getId().asString() + ">, you have been registered!").block();
                    store.addUser(temp);
                    store.saveData();
                }
            }
        });

        commands.put("dailybonus", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length != 1) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %dailybonus").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (store.getUser(senderID).DailyReady()) {
                            store.getUser(senderID).getDailyBonus();
                            event.getMessage().getChannel().block().createMessage("You have received your daily bonus of 20 Recall Shards!").block();
                            store.saveData();
                        } else {
                            event.getMessage().getChannel().block().createMessage("You've already claimed your daily bonus for today.").block();
                        }
                    }
                }
            }
        });

        commands.put("singleroll", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                String[] m = parseCommand(event.getMessage().getContent().toString());
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %singleroll (ID)").block();
                } else {
                    if (!store.hasUser(member.getId().asString())) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasBannerID(id)) {
                                if (store.getUser(senderID).canMakeCommand() || senderID.equals("136668026404732928")) {
                                    store.getUser(senderID).madecommand();
                                    if (store.getUser(senderID).getSummoningCurrency() >= 10) {
                                        store.getUser(senderID).addSummoningCurrency(-10);
                                        Integer tempchar = store.getBannerbyID(id).singlepull();
                                        Character ref = store.getCharacterByID(tempchar);
                                        String mess = "You have summoned: " + ref.getName() + ".\n";
                                        Thread summoning = new Thread(() -> {
                                            ArrayList<String> summontemp;
                                            if (ref.getRarity() == 3) {
                                                summontemp = summon.getThreeStarAnimation();
                                            } else if (ref.getRarity() == 4) {
                                                summontemp = summon.getFourStarAnimation();
                                            } else {
                                                summontemp = summon.getFiveStarAnimation();
                                            }
                                            Message messagetest = event.getMessage().getChannel().block().createMessage(summontemp.get(0)).block();
                                            for (int a1 = 1; a1 < summontemp.size(); a1++) {
                                                try {
                                                    Thread.sleep(1000);
                                                    final int asdf = a1;
                                                    messagetest.edit(messageEditSpec -> messageEditSpec.setContent(summontemp.get(asdf))).block();
                                                } catch (InterruptedException ignored) {
                                                }
                                            }
                                            messagetest.edit(messageEditSpec -> messageEditSpec.setContent(mess)).block();
                                            try {
                                                Thread.sleep(3000);
                                            } catch (InterruptedException ignored) {
                                            }
                                            String blah = summontemp.get(summontemp.size() - 1);
                                            messagetest.edit(messageEditSpec -> messageEditSpec.setContent(blah + "\n" + mess + "\n```" + ref.getSummonquote() + "```")).block();
                                            String anotherone = blah + "\n" + mess + "\n```" + ref.getSummonquote() + "```";
                                            if (store.getUser(senderID).hasCharacter(ref.getID())) {
                                                int quint;
                                                if (ref.getRarity() == 3) {
                                                    quint = 1;
                                                } else if (ref.getRarity() == 4) {
                                                    quint = 3;
                                                } else {
                                                    quint = 5;
                                                }
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(anotherone + "\n" + "Since you already possess " + ref.getName() + ", you have been given " + quint + " Quintessence Fragments.")).block();
                                                store.getUser(senderID).addGuaranteedCurrency(quint);
                                            } else {
                                                store.getUser(senderID).addCharacter(tempchar);
                                            }
                                            //event.getMessage().getChannel().block().createEmbed(embedCreateSpec -> embedCreateSpec.setImage(ref.getImageLink())).block();
                                            messagetest.edit(messageEditSpec -> messageEditSpec.setEmbed(embedCreateSpec -> embedCreateSpec.setImage(ref.getImageLink()))).block();
                                            store.saveData();
                                            return;
                                        });
                                        summoning.start();

                                    } else {
                                        event.getMessage().getChannel().block().createMessage("Insufficient Recall Shards! You have: " + store.getUser(senderID).getSummoningCurrency() + " Recall Shards. Required: 10 Recall Shards").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Slow down! Please wait " + store.getUser(senderID).timetonextCommand() + " seconds before rolling again.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner ID does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("tenroll", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                String[] m = parseCommand(event.getMessage().getContent().toString());
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %tenroll (ID)").block();
                } else {
                    if (!store.hasUser(member.getId().asString())) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasBannerID(id)) {
                                if (store.getUser(senderID).canMakeCommand() || senderID.equals("136668026404732928")) {
                                    store.getUser(senderID).madecommand();
                                    if (store.getUser(senderID).getSummoningCurrency() >= 100) {
                                        store.getUser(senderID).addSummoningCurrency(-100);
                                        ArrayList<Integer> tempchar = store.getBannerbyID(id).tenpull();
                                        ArrayList<String> result = new ArrayList<>();
                                        result.add("You have summoned:");
                                        int recall = 0;
                                        for (Integer character : tempchar) {
                                            Character ref = store.getCharacterByID(character);
                                            String tempname = "(" + ref.getRarity() + "\\*) " + ref.getName();

                                            if (store.getUser(senderID).hasCharacter(character)) {
                                                if (ref.getRarity() == 3) {
                                                    recall += 1;
                                                } else if (ref.getRarity() == 4) {
                                                    recall += 3;
                                                } else {
                                                    recall += 5;
                                                }
                                                tempname += " (DUPLICATE)";
                                            } else {
                                                store.getUser(senderID).addCharacter(character);
                                                tempname += " (NEW)";
                                            }
                                            result.add(result.get(result.size() - 1) + "\n" + tempname);
                                        }
                                        if (recall > 0) {
                                            result.add(result.get(result.size() - 1) + "\n" + "You have been given " + recall + " Quintessence Fragments for your duplicate units.");
                                        }
                                        store.getUser(senderID).addGuaranteedCurrency(recall);
                                        Thread summoning = new Thread(() -> {
                                            int highestrarity = 3;
                                            for (Integer character : tempchar) {
                                                if (store.getCharacterByID(character).getRarity() > highestrarity) {
                                                    highestrarity = store.getCharacterByID(character).getRarity();
                                                }
                                            }
                                            ArrayList<String> summontemp;
                                            if (highestrarity == 3) {
                                                summontemp = summon.getThreeStarAnimation();
                                            } else if (highestrarity == 4) {
                                                summontemp = summon.getFourStarAnimation();
                                            } else {
                                                summontemp = summon.getFiveStarAnimation();
                                            }
                                            Message messagetest = event.getMessage().getChannel().block().createMessage(summontemp.get(0)).block();
                                            for (int a1 = 1; a1 < summontemp.size(); a1++) {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException ignored) {
                                                }
                                                int asdf = a1;
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(summontemp.get(asdf))).block();
                                            }

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException ignored) {
                                            }
                                            for (int a1 = 0; a1 < result.size(); a1++) {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException ignored) {
                                                }
                                                int asdf = a1;
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(result.get(asdf))).block();
                                            }
                                        });
                                        summoning.start();
                                        store.saveData();
                                    } else {
                                        event.getMessage().getChannel().block().createMessage("Insufficient Recall Shards! You have: " + store.getUser(senderID).getSummoningCurrency() + " Recall Shards. Required: 100 Recall Shards").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Slow down! Please wait " + store.getUser(senderID).timetonextCommand() + " seconds before rolling again.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("That banner ID does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("rareroll", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                String[] m = parseCommand(event.getMessage().getContent().toString());
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %rareroll (ID)").block();
                } else {
                    if (!store.hasUser(member.getId().asString())) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasBannerID(id)) {
                                if (store.getUser(senderID).canMakeCommand() || senderID.equals("136668026404732928")) {
                                    store.getUser(senderID).madecommand();
                                    if (store.getUser(senderID).getSummoningCurrency() >= 100) {
                                        store.getUser(senderID).addSummoningCurrency(-100);
                                        Integer tempchar = store.getBannerbyID(id).rarepull();
                                        Character ref = store.getCharacterByID(tempchar);
                                        String mess = "You have summoned: " + ref.getName() + ".\n";
                                        Thread summoning = new Thread(() -> {
                                            ArrayList<String> summontemp;
                                            if (ref.getRarity() == 4) {
                                                summontemp = summon.getFourStarAnimation();
                                            } else {
                                                summontemp = summon.getFiveStarAnimation();
                                            }
                                            Message messagetest = event.getMessage().getChannel().block().createMessage(summontemp.get(0)).block();
                                            for (int a1 = 1; a1 < summontemp.size(); a1++) {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException ignored) {
                                                }
                                                final int asdf = a1;
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(summontemp.get(asdf))).block();
                                            }
                                            messagetest.edit(messageEditSpec -> messageEditSpec.setContent(mess)).block();
                                            try {
                                                Thread.sleep(3000);
                                            } catch (InterruptedException ignored) {
                                            }
                                            String blah = summontemp.get(summontemp.size() - 1);
                                            messagetest.edit(messageEditSpec -> messageEditSpec.setContent(blah + "\n" + mess + "\n```" + ref.getSummonquote() + "```")).block();
                                            String anotherone = blah + "\n" + mess + "\n```" + ref.getSummonquote() + "```";
                                            if (store.getUser(senderID).hasCharacter(ref.getID())) {
                                                int quint;
                                                if (ref.getRarity() == 3) {
                                                    quint = 1;
                                                } else if (ref.getRarity() == 4) {
                                                    quint = 3;
                                                } else {
                                                    quint = 5;
                                                }
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(anotherone + "\n" + "Since you already possess " + ref.getName() + ", you have been given " + quint + " Quintessence Fragments.")).block();
                                                store.getUser(senderID).addGuaranteedCurrency(quint);
                                            } else {
                                                store.getUser(senderID).addCharacter(tempchar);
                                            }
                                            event.getMessage().getChannel().block().createEmbed(embedCreateSpec -> {
                                                embedCreateSpec.setImage(ref.getImageLink());
                                            }).block();
                                            store.saveData();
                                        });
                                        summoning.start();
                                    } else {
                                        event.getMessage().getChannel().block().createMessage("Insufficient Recall Shards! You have: " + store.getUser(senderID).getSummoningCurrency() + " Recall Shards. Required: 100 Recall Shards").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Slow down! Please wait " + store.getUser(senderID).timetonextCommand() + " seconds before rolling again.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner ID does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("guaranteeroll", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String senderID = member.getId().asString();
                String[] m = parseCommand(event.getMessage().getContent().toString());
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %guaranteeroll (ID)").block();
                } else {
                    if (!store.hasUser(member.getId().asString())) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasBannerID(id)) {
                                if (store.getBannerbyID(id).getFocus().size() > 0) {
                                    if (store.getUser(senderID).canMakeCommand() || senderID.equals("136668026404732928")) {
                                        store.getUser(senderID).madecommand();
                                        if (store.getUser(senderID).getSummoningCurrency() >= 100) {
                                            store.getUser(senderID).addSummoningCurrency(-100);
                                            Integer tempchar = store.getBannerbyID(id).guaranteepull();
                                            Character ref = store.getCharacterByID(tempchar);
                                            String mess = "You have summoned: " + ref.getName() + ".\n";
                                            Thread summoning = new Thread(() -> {
                                                ArrayList<String> summontemp = summon.getFiveStarAnimation();
                                                Message messagetest = event.getMessage().getChannel().block().createMessage(summontemp.get(0)).block();
                                                for (int a1 = 1; a1 < summontemp.size(); a1++) {
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (InterruptedException ignored) {
                                                    }
                                                    final int asdf = a1;
                                                    messagetest.edit(messageEditSpec -> messageEditSpec.setContent(summontemp.get(asdf))).block();
                                                }
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(mess)).block();
                                                try {
                                                    Thread.sleep(3000);
                                                } catch (InterruptedException ignored) {
                                                }
                                                String blah = summontemp.get(summontemp.size() - 1);
                                                messagetest.edit(messageEditSpec -> messageEditSpec.setContent(blah + "\n" + mess + "\n```" + ref.getSummonquote() + "```")).block();
                                                String anotherone = blah + "\n" + mess + "\n```" + ref.getSummonquote() + "```";
                                                if (store.getUser(senderID).hasCharacter(ref.getID())) {
                                                    messagetest.edit(messageEditSpec -> messageEditSpec.setContent(anotherone + "\n" + "Since you already possess " + ref.getName() + ", you have been given 10 Quintessence Fragments.")).block();
                                                    store.getUser(senderID).addGuaranteedCurrency(10);
                                                } else {
                                                    store.getUser(senderID).addCharacter(tempchar);
                                                }
                                                event.getMessage().getChannel().block().createEmbed(embedCreateSpec -> embedCreateSpec.setImage(ref.getImageLink())).block();
                                                store.saveData();
                                            });
                                            summoning.start();
                                        } else {
                                            event.getMessage().getChannel().block().createMessage("Insufficient Quintessence Fragments! You have: " + store.getUser(senderID).getGuaranteedCurrency() + " Quintessence Fragments. Required: 100 Recall Shards").block();
                                        }
                                    } else {
                                        event.getMessage().getChannel().block().createMessage("Slow down! Please wait " + store.getUser(senderID).timetonextCommand() + " seconds before rolling again.").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("This banner does not have any focus units.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner ID not found.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("profile", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length > 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %profile (mention)").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (m.length == 1) {
                            String mess = "**" + member.getDisplayName() + "'s Profile**\n";
                            mess += "Number of characters: " + store.getUser(senderID).getCharacters().size() + "\n";
                            mess += "Favorite Character: ";
                            if (store.getUser(senderID).hasFavoriteCharacter()) {
                                mess += store.getCharacterByID(store.getUser(senderID).getFavorite()).getName() + "\n";
                            } else {
                                mess += "None\n";
                            }
                            mess += "Recall Shards: " + store.getUser(senderID).getSummoningCurrency() + "\n";
                            mess += "Quintessence Fragments: " + store.getUser(senderID).getGuaranteedCurrency() + "\n";
                            event.getMessage().getChannel().block().createMessage(mess).block();
                        }
                        else {
                            if (m[1].matches("<@!\\S+>")) {
                                String targetID = parseMention(m[1]);
                                if (!store.hasUser(targetID)) {
                                    event.getMessage().getChannel().block().createMessage("User " + m[1] + " does not exist.").block();
                                } else {
                                    ArrayList<Member> temp = new ArrayList<>();
                                    event.getGuild().block().getMembers().subscribe(i -> temp.add(i));
                                    String sendername = "USERNAME NOT FOUND";
                                    int i;
                                    for (i = 0; i < temp.size(); i++) {
                                        if (temp.get(i).getId().asString().equals(targetID)) {
                                            sendername = temp.get(i).getDisplayName();
                                            break;
                                        }
                                    }
                                    String mess = "**" + sendername + "'s Profile**\n";
                                    mess += "Number of characters: " + store.getUser(targetID).getCharacters().size() + "\n";
                                    mess += "Favorite Character: ";
                                    if (store.getUser(targetID).hasFavoriteCharacter()) {
                                        mess += store.getCharacterByID(store.getUser(targetID).getFavorite()).getName() + "\n";
                                    } else {
                                        mess += "None\n";
                                    }
                                    mess += "Recall Shards: " + store.getUser(targetID).getSummoningCurrency() + "\n";
                                    mess += "Quintessence Fragments: " + store.getUser(targetID).getGuaranteedCurrency() + "\n";
                                    event.getMessage().getChannel().block().createMessage(mess).block();
                                }
                            } else if (m[1].matches("<@\\S+>")) {
                                String targetID = parseMentionMobile(m[1]);
                                if (!store.hasUser(targetID)) {
                                    event.getMessage().getChannel().block().createMessage("User " + m[1] + " does not exist.").block();
                                } else {
                                    ArrayList<Member> temp = new ArrayList<>();
                                    event.getGuild().block().getMembers().subscribe(i -> temp.add(i));
                                    String sendername = "USERNAME NOT FOUND";
                                    int i;
                                    for (i = 0; i < temp.size(); i++) {
                                        if (temp.get(i).getId().asString().equals(targetID)) {
                                            sendername = temp.get(i).getDisplayName();
                                            break;
                                        }
                                    }
                                    String mess = "**" + sendername + "'s Profile**\n";
                                    mess += "Number of characters: " + store.getUser(targetID).getCharacters().size() + "\n";
                                    mess += "Favorite Character: ";
                                    if (store.getUser(targetID).hasFavoriteCharacter()) {
                                        mess += store.getCharacterByID(store.getUser(targetID).getFavorite()).getName() + "\n";
                                    } else {
                                        mess += "None\n";
                                    }
                                    mess += "Recall Shards: " + store.getUser(targetID).getSummoningCurrency() + "\n";
                                    mess += "Quintessence Fragments: " + store.getUser(targetID).getGuaranteedCurrency() + "\n";
                                    event.getMessage().getChannel().block().createMessage(mess).block();
                                }
                            }
                            else {
                                event.getMessage().getChannel().block().createMessage("That is not a valid mentioned user.").block();
                                System.out.println(m[1]);
                            }
                        }
                    }
                }
            }
        });

        commands.put("characterlist", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length != 1) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %characterlist").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        int a;
                        StringBuilder mess = new StringBuilder(member.getDisplayName() + "'s characters:\n");
                        User asdf = store.getUser(senderID);
                        asdf.sortCharacters();
                        if (asdf.getCharacters().size() == 0) {
                            mess.append("None");
                        }
                        for (a = 0; a < asdf.getCharacters().size(); a++) {
                            Character temp = store.getCharacterByID(asdf.getCharacters().get(a));
                            mess.append("(").append(temp.getID()).append(") ").append(temp.getName()).append("\n");
                        }
                        event.getMessage().getChannel().block().createMessage(mess.toString()).block();
                    }
                }
            }
        });

        commands.put("characterinfo", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %characterinfo (ID)").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            Character temp = store.getCharacterByID(id);
                            StringBuilder infomessage = new StringBuilder("**Name: ** ");
                            infomessage.append(temp.getName());
                            infomessage.append("\n**Description: ** ");
                            infomessage.append(temp.getDescription());
                            infomessage.append("\n**Summoning Quote: ** ");
                            infomessage.append(temp.getSummonquote());
                            infomessage.append("\n**Faceclaim/Character Art:** ");
                            event.getMessage().getChannel().block().createMessage(infomessage.toString()).block();
                            event.getMessage().getChannel().block().createEmbed(embedCreateSpec -> {
                                embedCreateSpec.setImage(temp.getImageLink());
                            }).block();
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("setfavorite", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setfavorite (ID)").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.getUser(senderID).hasCharacter(id)) {
                                store.getUser(senderID).setFavorite(id);
                                store.saveData();
                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(store.getUser(senderID).getFavorite()).getName() + " is now your favorite character.").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("You either do not own that character, or that character of ID " + id + " does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("bannerinfo", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length != 2) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %bannerinfo (ID)").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasBannerID(id)) {
                                if (store.getBannerbyID(id).isEnabled()) {
                                    StringBuilder temp = new StringBuilder();
                                    temp.append("**Name:** ").append(store.getBannerbyID(id).getName()).append("\n");
                                    temp.append("**Banner ID:** ").append(store.getBannerbyID(id).getID()).append("\n");
                                    temp.append("**Focus Characters: **\n");
                                    ArrayList<Integer> tempchars = store.getBannerbyID(id).getFocus();
                                    for (Integer tempchar : tempchars) {
                                        temp.append(store.getCharacterByID(tempchar).getName()).append("\n");
                                    }
                                    temp.append("**Summoning Rates: **\n");
                                    temp.append("Focus: ").append(store.getBannerbyID(id).getRates()[3]).append("%\n");
                                    temp.append("Five Star: ").append(store.getBannerbyID(id).getRates()[2]).append("%\n");
                                    temp.append("Four Star: ").append(store.getBannerbyID(id).getRates()[1]).append("%\n");
                                    temp.append("Three Star: ").append(store.getBannerbyID(id).getRates()[0]).append("%\n");
                                    event.getMessage().getChannel().block().createMessage(temp.toString()).block();
                                } else {
                                    event.getMessage().getChannel().block().createMessage("That banner is not active.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("That banner does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("listbanners", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (m.length != 1) {
                    event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %listbanners").block();
                } else {
                    if (!store.hasUser(senderID)) {
                        event.getMessage().getChannel().block().createMessage("Please register an account with the %register command, <@" + senderID + ">").block();
                    } else {
                        ArrayList<Banner> bannerlist = store.getbannerlist();
                        StringBuilder temp = new StringBuilder();
                        for (Banner ban : bannerlist) {
                            if (ban.isEnabled()) {
                                temp.append("(").append(ban.getID()).append(") ").append(ban.getName()).append("\n");
                            }
                        }
                        event.getMessage().getChannel().block().createMessage(temp.toString()).block();
                    }
                }
            }
        });

        commands.put("reload", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 1) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %reload").block();
                    } else {
                        Message t = event.getMessage().getChannel().block().createMessage("Reloading database.").block();
                        store = new Storage();
                        store.loadData();
                        t.edit(messageEditSpec -> messageEditSpec.setContent("Database reloaded.")).block();
                    }
                }
            }
        });

        commands.put("createchar", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %createchar name").block();
                    } else {
                        if (store.hasCharacter(m[1])) {
                            event.getMessage().getChannel().block().createMessage("Character already exists.").block();
                        } else {
                            store.addCharacter(new Character(m[1], store.getnextCharacterID()));
                            store.saveData();
                            event.getMessage().getChannel().block().createMessage("New character named " + m[1] + " created!").block();
                        }
                    }
                }
            }
        });

        commands.put("deletechar", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %deletechar ID").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasCharacterID(id)) {
                                ArrayList<User> temp = store.getuserlist();
                                int a;
                                for (a = 0; a < temp.size(); a++) {
                                    if (temp.get(a).hasCharacter(id)) {
                                        temp.get(a).removeCharacter(id);
                                    }
                                }
                                for (a = 0; a < store.getbannerlist().size(); a++) {
                                    if (store.getbannerlist().get(a).hasUnit(id) != 0) {
                                        if (store.getbannerlist().get(a).hasUnit(id) == 3) {
                                            store.getbannerlist().get(a).removefromthreestar(id);
                                        }
                                        if (store.getbannerlist().get(a).hasUnit(id) == 4) {
                                            store.getbannerlist().get(a).removefromfourstar(id);
                                        }
                                        if (store.getbannerlist().get(a).hasUnit(id) == 5) {
                                            store.getbannerlist().get(a).removefromfivestar(id);
                                        }
                                        if (store.getbannerlist().get(a).hasUnit(id) == 6) {
                                            store.getbannerlist().get(a).removefromfocus(id);
                                        }
                                    }
                                }
                                store.removeCharacter(m[1]);
                                store.saveData();
                                event.getMessage().getChannel().block().createMessage("Character removed.").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("setchardescription", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setchardescription id \"description\"").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasCharacterID(id)) {
                                store.getCharacterByID(id).setDescription(m[2]);
                                store.saveData();
                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(id).getName() + "'s character description has been successfully changed.").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("changecharname", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %changecharname id \"name\"").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasCharacterID(id)) {
                                String temp = store.getCharacterByID(id).getName();
                                store.getCharacterByID(id).changeName(m[2]);
                                store.saveData();
                                event.getMessage().getChannel().block().createMessage(temp + "'s name has been changed to " + m[2] + ".").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("setcharimage", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setcharimage id \"URL\"").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasCharacterID(id)) {
                                store.getCharacterByID(id).setImageLink(m[2]);
                                store.saveData();
                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(id).getName() + "'s image link has been successfully changed.").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("setcharrarity", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setcharrarity id rarity").block();
                    } else {
                        String regex = "\\d+";
                        if (m[2].matches(regex)) {
                            int level = Integer.parseInt(m[2]);
                            if (IDregexconfirm(m[1])) {
                                int id = Integer.parseInt(m[1]);
                                if (store.hasCharacterID(id)) {
                                    if (level < 3 || level > 5) {
                                        event.getMessage().getChannel().block().createMessage("Invalid rarity. Ensure it is between 3 and 5.").block();
                                    } else {
                                        store.getCharacterByID(id).setRarity(level);
                                        store.saveData();
                                        event.getMessage().getChannel().block().createMessage(store.getCharacterByID(id).getName() + "'s rarity has successfully been changed to " + m[2] + "* rarity.").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid level input. Ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("setsummonquote", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setsummonquote id \"URL\"").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasCharacterID(id)) {
                                store.getCharacterByID(id).setSummonquote(m[2]);
                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(id).getName() + " summoning quote has been updated.").block();
                                store.saveData();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("listchars", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 1) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %listchars").block();
                    } else {
                        ArrayList<Character> temp = store.getcharlist();
                        temp.sort(Comparator.comparingInt(Character::getID));
                        int a;
                        StringBuilder t = new StringBuilder("List of characters: \n");
                        for (a = 0; a < temp.size(); a++) {
                            t.append("(").append(temp.get(a).getID()).append(") (").append(temp.get(a).getRarity()).append("\\*) ").append(temp.get(a).getName()).append("\n");
                            if (t.length() > 1900) {
                                event.getMessage().getChannel().block().createMessage(t.toString()).block();
                                t = new StringBuilder();
                            }
                        }
                        event.getMessage().getChannel().block().createMessage(t.toString()).block();
                    }
                }
            }
        });

        commands.put("renamebanner", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %renamebanner ID \"name\"").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (store.hasBannerID(bannerid)) {
                            String temp = store.getBannerbyID(bannerid).getName();
                            store.getBannerbyID(bannerid).changeName(m[2]);
                            store.saveData();
                            event.getMessage().getChannel().block().createMessage("Banner " + temp + "'s name has been updated to " + store.getBannerbyID(bannerid).getName() + ".").block();
                        } else {
                            event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                        }
                    }
                }
            }
        });

        commands.put("deletebanner", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %deletebanner ID").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (store.hasBannerID(bannerid)) {
                            store.removeBanner(store.getBannerbyID(bannerid).getName());
                            store.saveData();
                            event.getMessage().getChannel().block().createMessage("Banner removed.").block();
                        } else {
                            event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                        }
                    }
                }
            }
        });

        commands.put("createbanner", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %createbanner \"name\"").block();
                    } else {
                        if (store.hasBanner(m[1])) {
                            event.getMessage().getChannel().block().createMessage("Banner " + m[1] + " already exists.").block();
                        } else {
                            store.addBanner(new Banner(m[1], store.getnextBannerID()));
                            store.saveData();
                            event.getMessage().getChannel().block().createMessage("New banner named " + m[1] + " with ID " + store.getBanner(m[1]).getID() + " created!").block();
                        }
                    }
                }
            }
        });

        commands.put("setbannerstatus", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setbannerstatus id, true/false").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (store.hasBannerID(bannerid)) {
                            boolean tf;
                            switch (m[2]) {
                                case "true":
                                    tf = true;
                                    break;
                                case "false":
                                    tf = false;
                                    break;
                                default:
                                    event.getMessage().getChannel().block().createMessage("Please use true/false.").block();
                                    return;
                            }
                            store.getBannerbyID(bannerid).enable(tf);
                            store.saveData();
                            if (tf) {
                                event.getMessage().getChannel().block().createMessage("Banner " + store.getBannerbyID(bannerid).getName() + " has been enabled.").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner " + store.getBannerbyID(bannerid).getName() + " has been disabled.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                        }
                    }
                }
            }
        });

        commands.put("addunittobanner", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 4) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %addunittobanner bannerid, unitid, rate").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (IDregexconfirm(m[1])) {
                            if (IDregexconfirm(m[2])) {
                                int unitid = Integer.parseInt(m[2]);
                                if (store.hasBannerID(bannerid)) {
                                    if (store.hasCharacterID(unitid)) {
                                        int rate;
                                        switch (m[3]) {
                                            case "three":
                                                rate = 3;
                                                break;
                                            case "four":
                                                rate = 4;
                                                break;
                                            case "five":
                                                rate = 5;
                                                break;
                                            case "focus":
                                                rate = 6;
                                                break;
                                            default:
                                                event.getMessage().getChannel().block().createMessage("Invalid banner rate. Ensure it is one of the following: \"three, four, five, focus\".").block();
                                                return;
                                        }
                                        switch (rate) {
                                            case 3:
                                                store.getBannerbyID(bannerid).addtothreestar(store.getCharacterByID(unitid));
                                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(unitid).getName() + " has been added to the three star pool of " + store.getBannerbyID(bannerid).getName() + ".").block();
                                                break;
                                            case 4:
                                                store.getBannerbyID(bannerid).addtofourstar(store.getCharacterByID(unitid));
                                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(unitid).getName() + " has been added to the four star pool of " + store.getBannerbyID(bannerid).getName() + ".").block();
                                                break;
                                            case 5:
                                                store.getBannerbyID(bannerid).addtofivestar(store.getCharacterByID(unitid));
                                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(unitid).getName() + " has been added to the five star pool of " + store.getBannerbyID(bannerid).getName() + ".").block();
                                                break;
                                            case 6:
                                                store.getBannerbyID(bannerid).addtofocus(store.getCharacterByID(unitid));
                                                event.getMessage().getChannel().block().createMessage(store.getCharacterByID(unitid).getName() + " has been added to the focus star pool of " + store.getBannerbyID(bannerid).getName() + ".").block();
                                                break;
                                            default:
                                                event.getMessage().getChannel().block().createMessage("An error occured during processing.").block();
                                                return;
                                        }
                                        store.saveData();
                                    } else {
                                        event.getMessage().getChannel().block().createMessage("Character does not exist.").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Invalid character ID inputted, ensure it is a number.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid banner ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("removeunitfrombanner", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 3) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %removeunitfrombanner bannerid, unitid").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (IDregexconfirm(m[1])) {
                            if (IDregexconfirm(m[2])) {
                                int unitid = Integer.parseInt(m[2]);
                                if (store.hasBannerID(bannerid)) {
                                    if (store.hasCharacterID(unitid)) {
                                        if (store.getBannerbyID(bannerid).hasUnit(unitid) == 3) {
                                            store.getBannerbyID(bannerid).removefromthreestar(unitid);
                                        }
                                        if (store.getBannerbyID(bannerid).hasUnit(unitid) == 4) {
                                            store.getBannerbyID(bannerid).removefromfourstar(unitid);
                                        }
                                        if (store.getBannerbyID(bannerid).hasUnit(unitid) == 5) {
                                            store.getBannerbyID(bannerid).removefromfivestar(unitid);
                                        }
                                        if (store.getBannerbyID(bannerid).hasUnit(unitid) == 6) {
                                            store.getBannerbyID(bannerid).removefromfocus(unitid);
                                        }
                                        event.getMessage().getChannel().block().createMessage("Character has been removed.").block();
                                        store.saveData();
                                    } else {
                                        event.getMessage().getChannel().block().createMessage("Character is not in this banner.").block();
                                    }
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Invalid character ID inputted, ensure it is a number.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid banner ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("clearbannerfocus", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %clerbannerfocus bannerid").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (IDregexconfirm(m[1])) {
                            if (store.hasBannerID(bannerid)) {
                                for (int b = 0; b < store.getBannerbyID(bannerid).getFocus().size(); b++) {
                                    store.getBannerbyID(bannerid).removefromfocus(store.getBannerbyID(bannerid).getFocus().get(b));
                                }
                                event.getMessage().getChannel().block().createMessage("Focus cleared.").block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid banner ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("setbannerrate", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 4) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setbannerrate bannerid \"rate type\" rate").block();
                    } else {
                        int bannerid = Integer.parseInt(m[1]);
                        if (IDregexconfirm(m[1])) {
                            if (store.hasBannerID(bannerid)) {
                                String regex = "\\d+";
                                if (m[3].matches(regex)) {
                                    int ratetype;
                                    switch (m[2]) {
                                        case "three":
                                            ratetype = 3;
                                            break;
                                        case "four":
                                            ratetype = 4;
                                            break;
                                        case "five":
                                            ratetype = 5;
                                            break;
                                        case "focus":
                                            ratetype = 6;
                                            break;
                                        default:
                                            event.getMessage().getChannel().block().createMessage("Invalid banner rate. Ensure it is one of the following: \"three, four, five, focus\".").block();
                                            return;
                                    }
                                    int newrate = Integer.parseInt(m[3]);
                                    switch (ratetype) {
                                        case 3:
                                            store.getBannerbyID(bannerid).setthreestarrate(newrate);
                                            event.getMessage().getChannel().block().createMessage(store.getBannerbyID(bannerid).getName() + "'s three star rate has been set to " + newrate + "%.").block();
                                            break;
                                        case 4:
                                            store.getBannerbyID(bannerid).setfourstarrate(newrate);
                                            event.getMessage().getChannel().block().createMessage(store.getBannerbyID(bannerid).getName() + "'s four star rate has been set to " + newrate + "%.").block();
                                            break;
                                        case 5:
                                            store.getBannerbyID(bannerid).setfivestarrate(newrate);
                                            event.getMessage().getChannel().block().createMessage(store.getBannerbyID(bannerid).getName() + "'s five star rate has been set to " + newrate + "%.").block();
                                            break;
                                        case 6:
                                            store.getBannerbyID(bannerid).setfocusrate(newrate);
                                            event.getMessage().getChannel().block().createMessage(store.getBannerbyID(bannerid).getName() + "'s focus rate has been set to " + newrate + "%.").block();
                                            break;
                                        default:
                                            event.getMessage().getChannel().block().createMessage("An error occured during processing.").block();
                                            return;
                                    }
                                    store.saveData();
                                } else {
                                    event.getMessage().getChannel().block().createMessage("Invalid rate number. Ensure it is a number.").block();
                                }
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid banner ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("devbannerinfo", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %setbannerrate id").block();
                    } else {
                        if (IDregexconfirm(m[1])) {
                            int id = Integer.parseInt(m[1]);
                            if (store.hasBannerID(id)) {
                                StringBuilder temp = new StringBuilder();
                                temp.append("**Name:** ").append(store.getBannerbyID(id).getName()).append("\n");
                                temp.append("**Banner ID:** ").append(store.getBannerbyID(id).getID()).append("\n");
                                temp.append("**Focus Characters: **\n");
                                ArrayList<Integer> tempfocus = store.getBannerbyID(id).getFocus();
                                for (Integer tempchar : tempfocus) {
                                    temp.append(store.getCharacterByID(tempchar).getName()).append("\n");
                                }
                                temp.append("**Five Star Characters: **\n");
                                ArrayList<Integer> tempfive = store.getBannerbyID(id).getFiveStars();
                                for (Integer tempchar : tempfive) {
                                    temp.append(store.getCharacterByID(tempchar).getName()).append("\n");
                                }
                                temp.append("**Four Star Characters: **\n");
                                ArrayList<Integer> tempfour = store.getBannerbyID(id).getFourStars();
                                for (Integer tempchar : tempfour) {
                                    temp.append(store.getCharacterByID(tempchar).getName()).append("\n");
                                }
                                temp.append("**Three Star Characters: **\n");
                                ArrayList<Integer> tempthree = store.getBannerbyID(id).getThreeStars();
                                for (Integer tempchar : tempthree) {
                                    temp.append(store.getCharacterByID(tempchar).getName()).append("\n");
                                }
                                temp.append("**Summoning Rates: **\n");
                                temp.append("Focus: ").append(store.getBannerbyID(id).getRates()[3]).append("%\n");
                                temp.append("Five Star: ").append(store.getBannerbyID(id).getRates()[2]).append("%\n");
                                temp.append("Four Star: ").append(store.getBannerbyID(id).getRates()[1]).append("%\n");
                                temp.append("Three Star: ").append(store.getBannerbyID(id).getRates()[0]).append("%\n");
                                event.getMessage().getChannel().block().createMessage(temp.toString()).block();
                            } else {
                                event.getMessage().getChannel().block().createMessage("Banner does not exist.").block();
                            }
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid banner ID inputted, ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("devbannerlist", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 1) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %devbannerlist").block();
                    } else {
                        ArrayList<Banner> bannerlist = store.getbannerlist();
                        bannerlist.sort(Comparator.comparingInt(Banner::getID));
                        StringBuilder temp = new StringBuilder();
                        for (Banner ban : bannerlist) {
                            temp.append(ban.getID()).append(": ");
                            temp.append(ban.getName()).append("\n");
                        }
                        event.getMessage().getChannel().block().createMessage(temp.toString()).block();
                    }
                }
            }
        });

        commands.put("listusers", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 1) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %listusers").block();
                    } else {
                        ArrayList<Member> memberlist = new ArrayList<>();
                        event.getGuild().block().getMembers().subscribe(i -> memberlist.add(i));
                        ArrayList<User> userlist = store.getuserlist();
                        StringBuilder temp = new StringBuilder();
                        for (User user : userlist) {
                            for (int i = 0; i < memberlist.size(); i++) {
                                if (memberlist.get(i).getId().asString().equals(user.getId())) {
                                    temp.append(memberlist.get(i).getDisplayName());
                                }
                            }
                            temp.append(" (");
                            temp.append(user.getId()).append(")\n");
                        }
                        event.getMessage().getChannel().block().createMessage(temp.toString()).block();
                    }
                }
            }
        });

        commands.put("givealluserssummoningcurr", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %givealluserssummoningcurr amount").block();
                    } else {
                        String regex = "\\d+";
                        if (m[1].matches(regex)) {
                            int currency = Integer.parseInt(m[1]);
                            ArrayList<User> userlist = store.getuserlist();
                            for (User use : userlist) {
                                store.getUser(use.getId()).addSummoningCurrency(currency);
                            }
                            event.getMessage().getChannel().block().createMessage(m[1] + " Recall Shard(s) added to all users.").block();
                            store.saveData();
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid amount. Ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("giveallusersguaranteedcurr", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %giveallusersguaranteedcurr amount").block();
                    } else {
                        String regex = "\\d+";
                        if (m[1].matches(regex)) {
                            int currency = Integer.parseInt(m[1]);
                            ArrayList<User> userlist = store.getuserlist();
                            for (User use : userlist) {
                                store.getUser(use.getId()).addGuaranteedCurrency(currency);
                            }
                            event.getMessage().getChannel().block().createMessage(m[1] + " Quintessence Fragment(s) added to all users.").block();
                            store.saveData();
                        } else {
                            event.getMessage().getChannel().block().createMessage("Invalid amount. Ensure it is a number.").block();
                        }
                    }
                }
            }
        });

        commands.put("devuserinfo", event -> {
            store.madeCommand();
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                String[] m = parseCommand(event.getMessage().getContent().toString());
                String senderID = member.getId().asString();
                if (senderID.equals("136668026404732928")) {
                    if (m.length != 2) {
                        event.getMessage().getChannel().block().createMessage("Invalid argument amount. Usage: %devuserinfo \"nickname\"").block();
                    } else {
                        ArrayList<Member> temp = new ArrayList<>();
                        event.getGuild().block().getMembers().subscribe(i -> temp.add(i));
                        String asdf = "USER NOT FOUND";
                        for (int i = 0; i < temp.size(); i++) {
                            System.out.println(temp.get(i).getDisplayName());
                            if (temp.get(i).getDisplayName().equals(m[1])) {
                                String tempid = temp.get(i).getId().toString();
                                User tempuser = store.getUser(tempid);
                                String mess = "**" + temp.get(i).getDisplayName() + "'s User Information**\n";
                                mess += "User's String ID: " + tempid + "\n";
                                mess += "Number of characters: " + store.getUser(tempid).getCharacters().size() + "\n";
                                if (tempuser.hasFavoriteCharacter()) {
                                    mess += "Favorite Character: " + store.getCharacterByID(tempuser.getFavorite()).getName() + "\n";
                                } else {
                                    mess += "Favorite Character: NONE\n";
                                }
                                mess += "Recall Shards: " + tempuser.getSummoningCurrency() + "\n";
                                mess += "Quintessence Fragments: " + tempuser.getGuaranteedCurrency() + "\n";
                                mess += "Daily Bonus Ready: ";
                                if (tempuser.DailyReady()) {
                                    mess += "Yes\n";
                                } else {
                                    mess += "No\n";
                                }
                                asdf = mess;
                                break;
                            }
                        }
                        event.getMessage().getChannel().block().createMessage(asdf).block();
                    }
                }
            }
        });


    }

    private static boolean IDregexconfirm(String m) {
        String regex = "\\d+";
        return m.matches(regex);
    }

    private static String parseMention(String m) {
        return m.substring(3, m.length() - 1);
    }

    private static String parseMentionMobile(String m) {
        return m.substring(2, m.length() - 1);
    }

    public static void main(String[] args) {
        store.loadData();
        final DiscordClient client = new DiscordClientBuilder(args[0]).build();
        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    final String content = event.getMessage().getContent().orElse("");
                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (content.startsWith('%' + entry.getKey())) { //SET PREFIX
                            entry.getValue().execute(event);
                            break;
                        }
                    }
                });
        client.login().block();

    }

}