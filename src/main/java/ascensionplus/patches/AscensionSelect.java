package ascensionplus.patches;

import ascensionplus.AscensionPlusMain;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CNCardTextColors;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.patches.RetainMonsterBlockPatches;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import javax.smartcardio.Card;
import java.util.ArrayList;

public class AscensionSelect
{
    public static final String[] uiStrings = CardCrawlGame.languagePack.getUIString(AscensionPlusMain.makeID("CustomAscension")).TEXT;

    @SpirePatch(clz=CharacterSelectScreen.class,method="updateAscensionToggle")
    public static class Decrement
    {
        @SpireInsertPatch(locator= Locator.class,localvars={"o"})
        public static void Insert(CharacterSelectScreen __instance, CharacterOption o)
        {
            if (AscensionPlusMain.currentAscension > 20)
            {
                __instance.ascensionLevel++;
            }
            AscensionPlusMain.decrementAscension(o.c.chosenClass);
            AscensionPlusMain.logger.info(AscensionPlusMain.currentAscension);
        }
        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CharacterOption.class, "decrementAscensionLevel");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch(clz=CharacterSelectScreen.class,method="updateAscensionToggle")
    public static class Increment
    {
        @SpireInsertPatch(locator= Locator.class,localvars={"o"})
        public static void Insert(CharacterSelectScreen __instance, CharacterOption o)
        {
            if (AscensionPlusMain.currentAscension > 20)
            {
                __instance.ascensionLevel--;
            }
            AscensionPlusMain.incrementAscension(o.c.chosenClass);
            AscensionPlusMain.logger.info(AscensionPlusMain.currentAscension);
        }
        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CharacterOption.class, "incrementAscensionLevel");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch(clz=CharacterOption.class,method="incrementAscensionLevel")
    public static class IncrementUI
    {
        @SpirePostfixPatch
        public static void Postfix(CharacterOption __instance, int level)
        {
            if (AscensionPlusMain.currentAscension > 20)
            {
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = uiStrings[ascensionplus.AscensionPlusMain.currentAscension - 21];
            }
        }
    }
    @SpirePatch(clz=CharacterOption.class,method="decrementAscensionLevel")
    public static class DecrementUI
    {
        @SpirePostfixPatch
        public static void Postfix(CharacterOption __instance, int level)
        {
            if (AscensionPlusMain.currentAscension > 20)
            {
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = uiStrings[ascensionplus.AscensionPlusMain.currentAscension - 21];
            }
        }
    }

    @SpirePatch(clz=CharacterOption.class,method="updateHitbox")
    public static class PleaseWork
    {
        @SpireInsertPatch(locator=Locator.class)
        public static void PleaseIAmBeggingYou(CharacterOption __instance)
        {
            AscensionPlusMain.currentAscension = AscensionPlusMain.getCurrentAscension(__instance.c.chosenClass);
            if (AscensionPlusMain.currentAscension > 20)
            {
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel = 20;
            }
            else
            {
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel = AscensionPlusMain.currentAscension;
            }
        }
        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CharacterSelectScreen.class, "ascensionLevel");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[3]};
                //return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)}[4];
            }
        }

        @SpireInsertPatch(locator=Locator2.class)
        public static SpireReturn<Void> Insert2(CharacterOption __instance)
        {
            if (AscensionPlusMain.currentAscension > 20)
            {
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = uiStrings[ascensionplus.AscensionPlusMain.currentAscension - 21];
                return SpireReturn.Return();
            }
            else
            {
                return SpireReturn.Continue();
            }
        }

        private static class Locator2 extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CharacterSelectScreen.class, "A_TEXT");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
                //return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[3]};
            }
        }
//        static int i = 0;
//        @SpireInstrumentPatch
//        public static ExprEditor Instrument()
//        {
//
//            return new ExprEditor()
//            {
//                public void edit(FieldAccess f) throws CannotCompileException
//                {
//                    if (f.getFieldName().equals("ascLevelInfoString") && i ==0)
//                    {
//                        i++;
//                        f.replace("if (ascensionplus.AscensionPlusMain.currentAscension > 20) {" +
//                                "$_ = ascensionplus.patches.AscensionSelect.uiStrings[ascensionplus.AscensionPlusMain.currentAscension - 21];" +
//                                "} else {" +
//                                "$_ = $proceed($$);" +
//                                "};");
////                        f.replace("$_ = ascensionplus.AscensionPlusMain.currentAscension > 20 ?" +
////                                "ascensionplus.patches.AscensionSelect.uiStrings[ascensionplus.AscensionPlusMain.currentAscension - 21] :" +
////                                "com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen.A_TEXT[ascensionLevel - 1];");
//                    }
//                }
//            };
//        }
    }


    @SpirePatch(clz=CharacterSelectScreen.class, method="renderAscensionMode")
    public static class RenderAscNum
    {
        @SpireInstrumentPatch
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(FieldAccess f) throws CannotCompileException
                {
                    if (f.getFieldName().equals("ascensionLevel"))
                    {
                        f.replace("$_ = Math.max(ascensionplus.AscensionPlusMain.currentAscension, com.megacrit.cardcrawl.dungeons.AbstractDungeon.ascensionLevel);");
                    }
                }
            };
        }
    }
}
