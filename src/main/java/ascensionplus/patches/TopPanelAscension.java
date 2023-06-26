package ascensionplus.patches;

import ascensionplus.AscensionPlusMain;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class TopPanelAscension
{
    @SpirePatch(clz= TopPanel.class, method="renderDungeonInfo")
    public static class AscensionNumber
    {
        private static int i = 0;
        @SpireInstrumentPatch
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(FieldAccess f) throws CannotCompileException
                {
                    if (f.getFieldName().equals("ascensionLevel") && i != 0)
                    {
                        f.replace("$_ = Math.max(ascensionplus.AscensionPlusMain.currentAscension, com.megacrit.cardcrawl.dungeons.AbstractDungeon.ascensionLevel);");
                    }
                    i++;
                }
            };
        }
    }
    static final String[] uiStrings = CardCrawlGame.languagePack.getUIString(AscensionPlusMain.makeID("CustomAscension")).TEXT;
    @SpirePatch(clz=TopPanel.class, method="setupAscensionMode")
    public static class AscensionTooltip
    {
        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance)
        {
            if (AscensionPlusMain.currentAscension > 20)
            {
                String text = ReflectionHacks.getPrivate(__instance, TopPanel.class, "ascensionString");
                StringBuilder builder = new StringBuilder();
                builder.append(text);
                builder.append(" NL ");
                for (int i = 0; i < AscensionPlusMain.currentAscension - 20; i++)
                {
                    builder.append(uiStrings[i]);
                    if (i != AscensionPlusMain.currentAscension - 20 - 1)
                    {
                        builder.append(" NL ");
                    }
                }
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "ascensionString", builder.toString());
            }
        }
    }
}
