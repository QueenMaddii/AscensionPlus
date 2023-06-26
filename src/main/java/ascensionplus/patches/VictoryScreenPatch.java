package ascensionplus.patches;

import ascensionplus.AscensionPlusMain;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;

public class VictoryScreenPatch
{
    @SpirePatch(clz= VictoryScreen.class,method="updateAscensionAndBetaArtProgress")
    public static class Increment
    {
        @SpirePostfixPatch
        public static void Postfix(VictoryScreen __instance)
        {
            if (AbstractDungeon.ascensionLevel == 20 && AscensionPlusMain.currentAscension < 25)
            {
                AscensionPlusMain.updateMaxAsc(AbstractDungeon.player.chosenClass, AscensionPlusMain.currentAscension + 1);
            }
        }
    }
}