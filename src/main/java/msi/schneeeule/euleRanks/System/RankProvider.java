package msi.schneeeule.euleRanks.System;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class RankProvider {
    public enum Ranks {
        ADMINISTRATION("owl.rank.administration", "Administration", "Admin", "#CC1818", 1000),
        MANAGEMENT("owl.rank.management", "Management", "Manage", "#FF7F7F", 950),
        MODERATION("owl.rank.moderation", "Moderation", "Mod", "#FF5D35", 900),
        DEVELOPMENT_PLUS("owl.rank.development+", "Development+", "Dev+", "#448e00", 850),
        DEVELOPMENT("owl.rank.development", "Development", "Dev", "#448e00", 800),
        SUPPORT_PLUS("owl.rank.support+", "Support+", "Sup+", "#E8DD4C", 750),
        SUPPORT("owl.rank.support", "Support", "Sup", "#E8DD4C", 700),
        DESIGN_PLUS("owl.rank.design+", "Design+", "Design+", "#7A9FFF", 650),
        DESIGN("owl.rank.design", "Design", "Design", "#7A9FFF", 600),
        PARTNER("owl.rank.partner", "Partner", "Partner", "#B200FF", 550),
        VIP("owl.rank.vip", "VIP", "VIP", "#FF4F96", 500),
        MVP("owl.rank.mvp", "MVP", "MVP", "#6AF0FF", 400),
        PREMIUM("owl.rank.premium", "Premium", "Premium", "#47FF4A", 300),
        USER("owl.rank.user", "User", null, "#C0C0C0", 100),
        FALLBACK(null, "User", null, "#C0C0C0", 0);


        private final String permission;
        private final String name;
        private final Component colouredName;
        private final Component prefix;
        private final TextColor colour;
        private final String colourCode;
        private final int priority;

        Ranks(String permission, String name, String prefix, String colourCode, int priority) {
            this.permission = permission;
            this.name = name;
            this.colouredName = Component.text(name, TextColor.fromHexString(colourCode));
            this.prefix = (prefix == null ? null : Component.text(prefix, TextColor.fromHexString(colourCode)));
            this.colour = TextColor.fromHexString(colourCode);
            this.colourCode = colourCode;
            this.priority = priority;
        }

        public String getPermission() {return permission;}
        public String getName() {return name;}
        public Component getColouredName() {return colouredName;}
        public Component getPrefix() {return prefix;}
        public TextColor getColour() {return colour;}
        public String getColourCode() {return colourCode;}
        public int getPriority() {return priority;}

        public static Ranks getRank(Player p) {
            for (Ranks rank : Ranks.values()) {
                if (rank.permission == null) continue;
                if (p.hasPermission(rank.permission)) {
                    return rank;
                }
            }
            return FALLBACK;
        }
    }

    public static Component spacer = Component.text(" ¦ ", NamedTextColor.WHITE);
    public static int plus_priority_boost = 10;

    public static Component getPlusOption(Player p) {
        if (p.hasPermission("owl.rank.plus")
                && Ranks.getRank(p).getPriority() < 500 && Ranks.getRank(p).getPriority() > 100)
            return Component.text("+", Ranks.getRank(p).getColour());
        return Component.text("");
    }


}
