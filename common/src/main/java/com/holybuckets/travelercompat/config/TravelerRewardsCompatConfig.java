package com.holybuckets.travelercompat.config;

import com.holybuckets.travelercompat.Constants;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.NestedType;

import java.util.HashSet;
import java.util.Set;


@Config(Constants.MOD_ID)
public class TravelerRewardsCompatConfig {

    public static class WaystoneCompatConfig {

        @Comment("While true, teleporting using a Waystone will remove any items NOT Soulbound or in a Soulbound slot; items a player is wearing or holding are also safe")
        public boolean takeItemsOnWaystoneTeleport = true;

        @NestedType(String.class)
        @Comment("ResourceLocation of modded enchantments that will make sure a certain item or stack is not removed from a players inventory on Waystone teleport")
        public Set<String> waystoneTeleportSafeEnchantments = new HashSet<>(Set.of(
            "some_mod_id:soulbound"
        ));

    }

    public WaystoneCompatConfig waystoneCompatConfig = new WaystoneCompatConfig();
}