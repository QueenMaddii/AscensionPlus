//package ascensionplus.patches.scenes;
//
//import ascensionplus.AscensionPlusMain;
//import com.badlogic.gdx.files.FileHandle;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
//import com.megacrit.cardcrawl.characters.AbstractPlayer;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
//import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
//
//public class CurrentAscensionSave
//{
//    @SpirePatch(clz= CardCrawlGame.class, method="loadPlayerSave")
//    public static class LoadAscPlayer
//    {
//        public static void Postfix(CardCrawlGame __instance, AbstractPlayer p)
//        {
//            thePatch();
//        }
//    }
//    @SpirePatch(clz= CardCrawlGame.class, method="loadPostCombat")
//    public static class LoadAscCombat
//    {
//        public static void Postfix(CardCrawlGame __instance, SaveFile saveFile)
//        {
//            thePatch();
//        }
//    }
//    private static void thePatch()
//    {
//        FileHandle curAscFile = new FileHandle(AscensionPlusMain.APPDATA + AbstractDungeon.player.chosenClass.name() + ".curasc");
//        if (!curAscFile.exists())
//        {
//            AscensionPlusMain.currentAscension = AbstractDungeon.ascensionLevel;
//        }
//        else
//        {
//            String str = curAscFile.readString();
//            if (str.matches("2[1-5]"))
//            {
//                AscensionPlusMain.currentAscension = Integer.parseInt(str);
//            }
//            else
//            {
//                AscensionPlusMain.currentAscension = 0;
//                AscensionPlusMain.logger.error("Something went wrong with loading ascension level. Contact dev");
//            }
//        }
//    }
//
//    @SpirePatch(clz= SaveAndContinue.class, method="save")
//    public static class SaveAsc
//    {
//        @SpirePrefixPatch
//        public static void Prefix(SaveFile save)
//        {
//            FileHandle curAscFile = new FileHandle(AscensionPlusMain.APPDATA + AbstractDungeon.player.chosenClass.name() + ".curasc");
//            curAscFile.writeString(String.valueOf(AscensionPlusMain.currentAscension),false);
//        }
//    }
//}