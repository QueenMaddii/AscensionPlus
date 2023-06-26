package ascensionplus;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.abstracts.CustomSavableRaw;
import basemod.animations.SpineAnimation;
import basemod.interfaces.*;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.*;
import ascensionplus.util.GeneralUtils;
import ascensionplus.util.KeywordInfo;
import ascensionplus.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SpireInitializer
public class AscensionPlusMain implements
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber {
    public static ModInfo info;
    public static String modID;
    private static SpireConfig modConfig = null;
    static { loadModInfo(); }
    public static int currentAscension = 0;

    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.
    private static final String resourcesFolder = "ascensionplus";

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        new AscensionPlusMain();
    }

    public AscensionPlusMain()
    {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");

        BaseMod.addSaveField("CustomAscension", new CustomSavable<Integer>() {
            @Override
            public Integer onSave() {
                return currentAscension;
            }

            @Override
            public void onLoad(Integer integer) {
                currentAscension = integer;
            }
        });

        try
        {
            modConfig = new SpireConfig(modID, "AscensionPlusConfig", new Properties());
        }
        catch (IOException e)
        {
            logger.error("AscensionPlus config initialisation failed:");
            e.printStackTrace();
        }
    }

    public static int getMaxAsc(AbstractPlayer.PlayerClass pc)
    {
        if (modConfig.has("UNLOCKED"))
        {
            return 25 - 1;
        }
        if (modConfig.has(pc.name()))
        {
            return modConfig.getInt(pc.name()) - 1;
        }
        else
        {
            return 20;
        }
    }

    public static void updateMaxAsc(AbstractPlayer.PlayerClass pc, int level)
    {
        modConfig.setInt(pc.name(), level);
        saveConfig();
    }

    public static void decrementAscension(AbstractPlayer.PlayerClass pc)
    {
        if (currentAscension <= 1) return;
        currentAscension--;
        modConfig.setInt(pc.name() + "_CURRENT", currentAscension);
        saveConfig();
    }
    public static void incrementAscension(AbstractPlayer.PlayerClass pc)
    {
        if (currentAscension >= 25) return;
        currentAscension++;
        modConfig.setInt(pc.name() + "_CURRENT", currentAscension);
        saveConfig();
    }

    public static void setCurrentAscension(AbstractPlayer.PlayerClass pc, int level)
    {
        modConfig.setInt(pc.name() + "_CURRENT", level);
        saveConfig();
    }

    public static int getCurrentAscension(AbstractPlayer.PlayerClass pc)
    {
        if (modConfig.has(pc.name() + "_CURRENT"))
        {
            return modConfig.getInt(pc.name() + "_CURRENT");
        }
        return 1;
    }

    @Override
    public void receivePostInitialize() {
        //This loads the image used as an icon in the in-game mods menu.
        Texture badgeTexture = TextureLoader.getTexture(resourcePath("badge.png"));
        //Set up the mod information displayed in the in-game mods menu.
        //The information used is taken from your pom.xml file.
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, null);
    }

    /*----------Localization----------*/

    //This is used to load the appropriate localization files based on language.
    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = "eng";

    @Override
    public void receiveEditStrings() {
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
        loadLocalization(defaultLanguage);
        if (!defaultLanguage.equals(getLangString())) {
            //loadLocalization(getLangString());
        }
    }

    private void loadLocalization(String lang) {
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
//        BaseMod.loadCustomStringsFile(CardStrings.class,
//                localizationPath(lang, "CardStrings.json"));
//        BaseMod.loadCustomStringsFile(CharacterStrings.class,
//                localizationPath(lang, "CharacterStrings.json"));
//        BaseMod.loadCustomStringsFile(EventStrings.class,
//                localizationPath(lang, "EventStrings.json"));
//        BaseMod.loadCustomStringsFile(OrbStrings.class,
//                localizationPath(lang, "OrbStrings.json"));
//        BaseMod.loadCustomStringsFile(PotionStrings.class,
//                localizationPath(lang, "PotionStrings.json"));
//        BaseMod.loadCustomStringsFile(PowerStrings.class,
//                localizationPath(lang, "PowerStrings.json"));
//        BaseMod.loadCustomStringsFile(RelicStrings.class,
//                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    registerKeyword(keyword);
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
    }

    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, info.NAMES, info.DESCRIPTION);
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String resourcePath(String file) {
        return resourcesFolder + "/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/relics/" + file;
    }


    //This determines the mod's ID based on information stored by ModTheSpire.
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(AscensionPlusMain.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    private static void saveConfig() {
        try {
            modConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
