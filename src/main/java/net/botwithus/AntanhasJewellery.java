package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.events.impl.SkillUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AntanhasJewellery extends LoopingScript {

    private BotState botState = BotState.STOPPED;
    private JewelToSmelt jewelToSmelt = JewelToSmelt.UNSTRUNGSYMBOL;
    private Random random = new Random();

    LinkedList<String> logNames = new LinkedList<>();
    LinkedList<Integer> logAmounts = new LinkedList<>();
    int experienceGained = 0;
    long startingTime = System.currentTimeMillis();
    long timeScriptWasLastActive = System.currentTimeMillis();

    enum BotState {
        //define your own states here
        STOPPED,
        SETUP,
        SMELTING,
        BANKING,
        STOPPED_LACKOFMATERIALS,
        //...
    }

    enum JewelToSmelt {
        UNSTRUNGSYMBOL("Silver bar", "Unstrung symbol", 1, 1),
        UNSTRUNGEMBLEM("Silver bar", "Unstrung emblem", 1, 3),
        SILVERSICKLE("Silver bar", "Silver sickle", 1, 5),
        SILVERBOLTSUNF("Silver bar", "Silver bolts (unf)", 1, 7),
        CONDUCTOR("Silver bar", "Conductor", 1, 9),
        TIARA("Silver bar", "Tiara", 1, 11),
        SILVTHRILROD("Silver bar", "Silvthril rod", 1, 13),
        SILVTHRILCHAIN("Silver bar", "Silvthril chain", 1, 15),
        SILVTHRILLIMBS("Silver bar", "Silvthril limbs", 1, 17),
        OPALRING("Silver bar", "Opal ring", 1, 19),
        OPALBRACELET("Silver bar", "Opal bracelet", 1, 21),
        OPALNECKLACE("Silver bar", "Opal nacklace", 1, 23),
        OPALAMULET("Silver bar", "Opal amulet", 1, 25),
        JADERING("Silver bar", "Jade ring", 1, 27),
        JADEBRACELET("Silver bar", "Jade bracelet", 1, 29),
        JADENECKLACE("Silver bar", "Jade necklace", 1, 31),
        JADEAMULET("Silver bar", "Jade amulet", 1, 33),
        TOPAZRING("Silver bar", "Topaz ring", 1, 35),
        TOPAZBRACELET("Silver bar", "Topaz bracelet", 1, 37),
        TOPAZNECKLACE("Silver bar", "Topaz necklace", 1, 39),
        TOPAZAMULET("Silver bar", "Topaz amulet", 1, 41),
        LAPISLAZULIRING("Silver bar", "Lapis lazuli ring", 1, 43),
        GLORIOUSSILVTHRILCHAIN("Silver bar", "Glorious silvthril chain", 1, 45),
        GOLDRING("Gold bar", "Gold ring", 3, 1),
        GOLDNECKLACE("Gold bar", "Gold necklace", 3, 3),
        GOLDBRACELET("Gold bar", "Gold bracelet", 3, 5),
        GOLDAMULET("Gold bar", "Gold amulet", 3, 7),
        SAPPHIRERING("Gold bar", "Sapphire ring", 3, 9),
        SAPPHIRENECKLACE("Gold bar", "Sapphire necklace", 3, 11),
        SAPPHIREBRACELET("Gold bar", "Sapphire bracelet", 3, 13),
        SAPPHIREAMULET("Gold bar", "Sapphire amulet", 3, 15),
        EMERALDRING("Gold bar", "Emerald ring", 3, 17),
        EMERALDNECKLACE("Gold bar", "Emerald necklace", 3, 19),
        EMERALDBRACELET("Gold bar", "Emerald bracelet", 3, 21),
        EMERALDAMULET("Gold bar", "Emerald amulet", 3, 23),
        RUBYRING("Gold bar", "Ruby ring", 3, 25),
        RUBYNECKLACE("Gold bar", "Ruby necklace", 3, 27),
        RUBYBRACELET("Gold bar", "Ruby bracelet", 3, 29),
        RUBYAMULET("Gold bar", "Ruby amulet", 3, 31),
        DIAMONDRING("Gold bar", "Diamond ring", 3, 33),
        DIAMONDNECKLACE("Gold bar", "Diamond necklace", 3, 35),
        DIAMONDBRACELET("Gold bar", "Diamond bracelet", 3, 37),
        DIAMONDAMULET("Gold bar", "Diamond amulet", 3, 39),
        DRAGONSTONERING("Gold bar", "Dragonstone ring", 3, 41),
        DRAGONSTONENECKLACE("Gold bar", "Dragonstone necklace", 3, 43),
        DRAGONSTONEBRACELET("Gold bar", "Dragonstone bracelet", 3, 45),
        DRAGONSTONEAMULET("Gold bar", "Dragonstone amulet", 3, 47),
        ONYXRING("Gold bar", "Onyx ring", 3, 49),
        ONYXNECKLACE("Gold bar", "Onyx necklace", 3, 51),
        ONYXBRACELET("Gold bar", "Onyx bracelet", 3, 53),
        ONYXAMULET("Gold bar", "Onyx amulet", 3, 55),
        HYDRIXRING("Gold bar", "Hydrix ring", 3, 57),
        HYDRIXNECKLACE("Gold bar", "Hydrix necklace", 3, 59),
        HYDRIXBRACELET("Gold bar", "Hydrix bracelet", 3, 61),
        HYDRIXAMULET("Gold bar", "Hydrix amulet", 3, 63),
        ALCHEMICALONYXRING("Gold bar", "Alchemical onyx ring", 3, 65),
        ALCHEMICALONYXNECKLACE("Gold bar", "Alchemical onyx necklace", 3, 67),
        RINGOSLAYING("Gold bar", "Ring of slaying (8)", 3, 69),
        PLATINUMRING("Platinum bar", "Platinum ring", 5, 1),
        PLATINUMNECKLACE("Platinum bar", "Platinum necklace", 5, 3),
        PLATINUMBRACELET("Platinum bar", "Platinum bracelet", 5, 5),
        PLATINUMAMULET("Platinum bar", "Platinum amulet", 5, 7),
        PLATINUMANKLET("Platinum bar", "Platinum anklet", 5, 9);
        //...

        private final String barName;
        private final String jewelName;
        private final int barAddress;
        private final int jewelAddress;

        JewelToSmelt(String barName, String jewelName, int barAddress, int jewelAddress) {
            this.barName = barName;
            this.jewelName = jewelName;
            this.barAddress = barAddress;
            this.jewelAddress = jewelAddress;
        }

        public String getBarName() {
            return barName;
        }

        public String getJewelName() {
            return jewelName;
        }

        public int getBarAddress() {
            return barAddress;
        }

        public int getJewelAddress() {
            return jewelAddress;
        }

    }

    public AntanhasJewellery(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new AntanhasJewelleryGraphicsContext(getConsole(), this);
    }

    @Override
    public boolean initialize() {
        super.initialize();

        //this subscription updates the item log
        subscribe(InventoryUpdateEvent.class, inventoryUpdateEvent -> {
            //only update log if: a new item appears in the inv
            if(inventoryUpdateEvent.getNewItem().getName() != null && inventoryUpdateEvent.getInventoryId() == 93 && botState == BotState.SMELTING) {
                int increment;
                if (inventoryUpdateEvent.getNewItem().getName().equals(inventoryUpdateEvent.getOldItem().getName()) && inventoryUpdateEvent.getNewItem().getStackSize() > inventoryUpdateEvent.getOldItem().getStackSize()) {
                    increment = inventoryUpdateEvent.getNewItem().getStackSize() - inventoryUpdateEvent.getOldItem().getStackSize();
                } else {
                    increment = inventoryUpdateEvent.getNewItem().getStackSize();
                }
                if (logNames.contains(inventoryUpdateEvent.getNewItem().getName())) {
                    logAmounts.set(logNames.indexOf(inventoryUpdateEvent.getNewItem().getName()), logAmounts.get(logNames.indexOf(inventoryUpdateEvent.getNewItem().getName())) + increment);
                } else {
                    logNames.push(inventoryUpdateEvent.getNewItem().getName());
                    logAmounts.push(increment);
                }
            }
        });

        //subscription to keep track of xp gained
        subscribe(SkillUpdateEvent.class, skillUpdateEvent -> {
            if(Skills.byId(skillUpdateEvent.getId()) == Skills.CRAFTING && botState != BotState.STOPPED && botState != BotState.STOPPED_LACKOFMATERIALS) {
                experienceGained += skillUpdateEvent.getExperience() - skillUpdateEvent.getOldExperience();
            }
        });

        //subscription to stop the script if there aren't enough naterials in inv to make the jewel at the furnace
        subscribe(ChatMessageEvent.class, chatMessageEvent -> {
            if(chatMessageEvent.getMessage().replace("\u00A0", " ").equals("<col=EB2F2F>You lack the materials or inventory space to create this.")) {
                botState = BotState.STOPPED_LACKOFMATERIALS;
            }
        });

        loadConfiguration();

        return true;
    }

    @Override
    public void onLoop() {
        println("onLoop()");
        //Loops every 100ms by default, to change:
        this.loopDelay = 500;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000,7000));
            return;
        }

        //Coordinate coord = Client.getLocalPlayer().getCoordinate();
        //println("TESTING: %s", coord.getRegionId());

        switch (botState) {
            case STOPPED:
            case STOPPED_LACKOFMATERIALS:
                //do nothing
                println("We're idle!");
                Execution.delay(random.nextLong(1000,3000));
                break;
            //this only gets called when the start button is clicked, it can't clog onLoop() up
            case SETUP:
                println("Going to banking state");
                botState = BotState.BANKING;
                break;
            //notice that, if the script is running fine, this doesn't clog onLoop() up besides the brief moments between rocks and brief pauses to emulate human-like behavior
            //this is the only case where the breakpoint (botState = BotState.MOVINGTOBANK) isn't here but in the function itself, it's part of why I wrote handleMining() to be non-clogging
            case SMELTING:
                handleJewellery(player);
                break;
            //this clogs up the onLoop
            case BANKING:
                Execution.delay(handleBanking());
                if(botState != BotState.STOPPED && botState != BotState.STOPPED_LACKOFMATERIALS) botState = BotState.SMELTING;
                break;
        }

    }

    private long handleJewellery(LocalPlayer player) {
        println("handleJewellery()");

        //if the progress bar window isn't open
        if (!Interfaces.isOpen(1251)) {
            //and we don't have the desired jewel in inv yet
            if(!Backpack.contains(jewelToSmelt.getJewelName())) {
                SceneObject furnace = SceneObjectQuery.newQuery().name("Furnace").hidden(false).option("Smelt").results().nearest();
                if(furnace != null) {
                    furnace.interact("Smelt");
                    Execution.delayUntil(20000, () -> {
                        return Interfaces.isOpen(37);
                    });
                    Execution.delay(random.nextLong(500, 1000));
                    //select bar in the left side menu
                    Component bar = ComponentQuery.newQuery(37).componentIndex(62).itemName(jewelToSmelt.getBarName()).option("Select " + jewelToSmelt.getBarName()).results().first();
                    if (bar != null) {
                        bar.interact(bar.getOptions().get(0));
                        Execution.delay(random.nextLong(500, 1000));
                        //select jewel in the middle menu
                        Component jewel = ComponentQuery.newQuery(37).componentIndex(103).itemName(jewelToSmelt.getJewelName()).option("Select " + jewelToSmelt.getJewelName()).results().first();
                        if (jewel != null) {
                            jewel.interact(jewel.getOptions().get(0));
                            Execution.delay(random.nextLong(500, 1000));
                            //click "begin project"
                            Component beginButton = ComponentQuery.newQuery(37).componentIndex(163).results().first();
                            if (beginButton != null) {
                                beginButton.interact(beginButton.getOptions().get(0));
                                Execution.delay(random.nextLong(500, 1000));
                            }
                        }
                    }
                }
            //if we do have the jewel in inv, time to bank
            } else {
                Execution.delay(random.nextLong(500,1000));
                if (botState != BotState.STOPPED && botState != BotState.STOPPED_LACKOFMATERIALS) botState = BotState.BANKING;
            }
        }
        return random.nextLong(500,1000);
    }

    private long handleBanking()
    {
        ResultSet<Item> oldInventory = InventoryItemQuery.newQuery(93).results();
        Execution.delayUntil(20000, () -> Bank.loadLastPreset());
        //Execution.delay(random.nextLong(1000, 2000));
        Execution.delayUntil(5000, () -> !areTheseTwoInventoriesTheSame(oldInventory, InventoryItemQuery.newQuery(93).results()));
        return random.nextLong(500,1000);
    }

    boolean areTheseTwoInventoriesTheSame (ResultSet<Item> oldInv, ResultSet<Item> newInv) {
        var oldInvArray = oldInv.stream().toArray();
        var newInvArray = newInv.stream().toArray();
        for(int i = 0; i < 28; i++) {
            if(!oldInvArray[i].equals(newInvArray[i])) return false;
        }
        return true;
    }

    //the big String containing all text in the stats
    public String logString() {
        String bigString = "Time elapsed: " + timeElapsed() + "\n";
        bigString = bigString + "Experience gained: " + experienceGained + " (" + calculatePerHour(experienceGained) + " / hr)\n";
        for(var i = logNames.size() - 1; i >= 0; i--) {
            bigString = bigString + logNames.get(i) + " x " + logAmounts.get(i) + " (" + calculatePerHour(logAmounts.get(i)) + " / hr)\n";
        }
        return bigString;
    }

    //used by logString()
    private String calculatePerHour(int toBeCalculated) {
        long timeToConsider = botState != BotState.STOPPED && botState != BotState.STOPPED_LACKOFMATERIALS ? System.currentTimeMillis() : timeScriptWasLastActive;

        long timeElapsedMillis = timeToConsider - startingTime;

        long xpPerHour = (long) (toBeCalculated / (timeElapsedMillis / 3600000.0));

        NumberFormat numberFormat = NumberFormat.getInstance();
        String formattedPerHour = numberFormat.format(xpPerHour);

        return formattedPerHour;
    }

    //used by logString()
    private String timeElapsed() {
        long endingTime = botState != BotState.STOPPED && botState != BotState.STOPPED_LACKOFMATERIALS ? System.currentTimeMillis() : timeScriptWasLastActive;;
        long elapsedTime = endingTime - startingTime;

        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    void saveConfiguration() {
        try {
            configuration.addProperty("jewelToSmelt", String.valueOf(jewelToSmelt.ordinal()));
            configuration.save();
        } catch (Exception e) {
            println("Error saving configuration");
            println("This is a non-fatal error, you can ignore it.");
        }
    }

    void loadConfiguration() {
        try {
            jewelToSmelt = JewelToSmelt.values()[Integer.parseInt(configuration.getProperty("jewelToSmelt"))];
        } catch (Exception e) {
            println("Error loading configuration");
            println("This is a non-fatal error, you can ignore it.");
        }
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public JewelToSmelt getJewelToSmelt() {
        return jewelToSmelt;
    }

    public void setJewelToSmelt(JewelToSmelt jewelToSmelt) {
        this.jewelToSmelt = jewelToSmelt;
    }

}
