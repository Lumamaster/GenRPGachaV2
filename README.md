# GenRPGachaV2

A bot using Discord4J that acts as a "gacha game", wherein users accumulate currency over time that they can spend in a lottery-like system where they are given a random item from a pool of preset items, with the odds of getting items differing depending on rarity.

Command prefix is %.

Currently uses Java Serialization for data storage, though it can be adapted to use databases for storage.

Known issues to fix include lowering the amount of runtime memory required by retrieving data directly from files rather than retaining it in memory.

Names for currencies, etc. are currently preset, but can be easily edited through modifying the code.

## Command list

### Developer commands
ping - Pings the bot.
reload - Reloads the database
listusers - Lists all users.
givealluserssummoningcurr amount - Gives all users the specified amount of Recall Shards.
giveallusersguaranteedcurr amount - Gives all users the specified amount of Quintessence Fragments.
devuserinfo "username" - Displays verbose information about the specified user.
createchar "name" - Creates a new character with the specified name.
deletechar id - Deletes the specified character from all users.
listchars - Lists all characters.
characterinfo id - Displays information about the specified character.
changecharname id "name" - Changes the specified character's name.
setchardescription id "description" - Changes the specified character's description.
setcharimage id "image url" - Sets the specified character's image link.
setcharrarity id rarity - Changes the rarity of the specified character.
setsummonquote id "quote" - Sets the specified character's summoning quote.
createbanner "name" - Creates a new banner with the specified name.
renamebanner id "name" - Renames the specified banner.
devbannerinfo id - Displays verbose information about the specified banner.
devbannerlist - Lists all banners.
setbannerstatus id, true/false - Sets if the specified banner is visible to players or not.
addunittobanner bannerid, unitid, three/four/five/focus - Adds the specified unit to the specified banner in the specified pool.
removeunitfrombanner bannerid, unitid - Removes the specified unit from the specified banner's rarity pool.
setbannerrate bannerid, rate type, rate - Sets the % likelihood of summoning a unit from this rarity.
clearbannerfocus bannerid - Removes all units from the focus group of this banner.

### User commands
register - Registers your Discord account onto the bot. Also gives a one time gift of 100 Recall Shards.
dailybonus - Claims your daily bonus of 20 Recall Shards.
singleroll (ID) - Does a single summon on the corresponding banner. (costs 10 Recall Shards)
tenroll (ID) - Does ten summons on the corresponding banner. (costs 100 Recall Shards)
rareroll (ID) - Does a rare summon on the corresponding banner. (pulls only from 4 and 5 star pool, higher 5* chance) (costs 100 Recall Shards)
guaranteeroll (ID) - Guarantees one focus character from the corresponding banner. (Costs 100 Quintessence Fragments)
profile user (mention them) - Displays profile information about the specified user, including number of owned characters and currency held.
characterlist - Lists all the characters on your account.
characterinfo (ID) - Lists information about the character you have in your account with the corresponding ID. 
setfavorite (ID) - Sets your favorite character to the one with the corresponding ID.
listbanners - Shows a list of all available and pullable banners.
bannerinfo (ID) - Displays information about the banner with the corresponding ID.
