package ascensionplus.patches;

import ascensionplus.AscensionPlusMain;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.util.ArrayList;
import java.util.List;

public class AscensionEffects
{

    @SpirePatch(clz=AbstractRoom.class,method="update")
    public static class RemoveReward
    {
        @SpireInsertPatch(locator=Locator.class)
        public static void Insert(AbstractRoom __instance)
        {
            if (__instance.rewardAllowed)
            {
                __instance.rewardAllowed = !skip();
            }
        }
        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractRoom.class, "rewardAllowed");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    //Exodium, City, Beyond
    public static boolean skip()
    {
        List<String> bossList = AbstractDungeon.bossList;
        if (CardCrawlGame.dungeon instanceof com.megacrit.cardcrawl.dungeons.Exordium)
        {
            if (AscensionPlusMain.currentAscension >= 22)
            {
                if (bossList.size() == 2)
                {
                    return true;
                }
                else if (AscensionPlusMain.currentAscension >= 25)
                {
                    if (bossList.size() == 1)
                    {
                        return true;
                    }
                }
            }
        }
        else if (CardCrawlGame.dungeon instanceof com.megacrit.cardcrawl.dungeons.TheCity)
        {
            if (AscensionPlusMain.currentAscension >= 21)
            {
                if (bossList.size() == 2)
                {
                    return true;
                }
                else if (AscensionPlusMain.currentAscension >= 24)
                {
                    if (bossList.size() == 1)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SpirePatch(clz=ProceedButton.class, method="update")
    public static class Challenges
    {
        @SpireInsertPatch(locator=Locator.class)
        public static void Insert(ProceedButton __instance) {
            AbstractRoom currentRoom = AbstractDungeon.getCurrRoom();
            if (currentRoom instanceof MonsterRoomBoss)
            {
                if (CardCrawlGame.dungeon instanceof com.megacrit.cardcrawl.dungeons.Exordium)
                {
                    if (AbstractDungeon.ascensionLevel >= 20 && AbstractDungeon.bossList.size() == 2 && AscensionPlusMain.currentAscension >= 22)
                    {
                        ReflectionHacks.privateMethod(ProceedButton.class, "goToDoubleBoss").invoke(__instance);
                    }
                    if (AbstractDungeon.ascensionLevel >= 20 && AbstractDungeon.bossList.size() == 1 && AscensionPlusMain.currentAscension >= 25)
                    {
                        ReflectionHacks.privateMethod(ProceedButton.class, "goToDoubleBoss").invoke(__instance);
                    }
                }
                else if (CardCrawlGame.dungeon instanceof com.megacrit.cardcrawl.dungeons.TheCity)
                {
                    if (AbstractDungeon.ascensionLevel >= 20 && AbstractDungeon.bossList.size() == 2 && AscensionPlusMain.currentAscension >= 21)
                    {
                        ReflectionHacks.privateMethod(ProceedButton.class, "goToDoubleBoss").invoke(__instance);
                    }
                    if (AbstractDungeon.ascensionLevel >= 20 && AbstractDungeon.bossList.size() == 1 && AscensionPlusMain.currentAscension >= 24)
                    {
                        ReflectionHacks.privateMethod(ProceedButton.class, "goToDoubleBoss").invoke(__instance);
                    }
                }
                else if (CardCrawlGame.dungeon instanceof com.megacrit.cardcrawl.dungeons.TheBeyond)
                {
                    if (AbstractDungeon.ascensionLevel >= 20 && AbstractDungeon.bossList.size() == 1 && AscensionPlusMain.currentAscension >= 23)
                    {
                        ReflectionHacks.privateMethod(ProceedButton.class, "goToDoubleBoss").invoke(__instance);
                    }
                }
            }
        }
        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.InstanceOfMatcher(MonsterRoomBoss.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

        private static int i = 0;
        @SpireInstrumentPatch
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(FieldAccess f) throws CannotCompileException
                {
                    if (f.getFieldName().equals("isEndless") && i == 0)
                    {
                        f.replace("{$_ = ($proceed($$) || (ascensionplus.AscensionPlusMain.currentAscension >= 23 && com.megacrit.cardcrawl.dungeons.AbstractDungeon.bossList.size() == 1) );}");
                        i++;
                    }
                }
            };
        }
    }
}