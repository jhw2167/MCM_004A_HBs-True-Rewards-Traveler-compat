package com.holybuckets.travelercompat.waystones;

import com.holybuckets.foundation.CommonClass;
import com.holybuckets.traveler.core.ManagedTraveler;
import com.holybuckets.travelercompat.TravelerRewardsCompatMain;
import com.holybuckets.travelercompat.externalapi.INonRequiredModCompat;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.*;

public class WaystonesSoulboundTeleport implements INonRequiredModCompat {

    private Map<IWaystoneTeleportContext, BlockPos> preTeleportPos = new HashMap<>();

    public WaystonesSoulboundTeleport() {
        BalmEvents reg = Balm.getEvents();
        reg.onEvent(WaystoneTeleportEvent.Pre.class, this::waystonePreTeleport);
        reg.onEvent(WaystoneTeleportEvent.Post.class, this::waystonePostTeleport);
    }

    private void waystonePreTeleport(WaystoneTeleportEvent.Pre event) {
        BlockPos chestPost = event.getContext().getEntity().blockPosition();
        IWaystone ws = event.getContext().getFromWaystone();
        if(ws != null) chestPost = ws.getPos().south().below();
        preTeleportPos.put(event.getContext(), chestPost);
    }

    private void waystonePostTeleport(WaystoneTeleportEvent.Post event)
    {
        if(!TravelerRewardsCompatMain.CONFIG.waystoneCompatConfig.takeItemsOnWaystoneTeleport) return;
        List<Player> playersTeleported = event.getTeleportedEntities().stream()
            .filter(entity -> entity instanceof Player)
            .map(entity -> (Player) entity)
            .toList();
        BlockPos prePos = preTeleportPos.remove(event.getContext());
        if (playersTeleported.isEmpty()) return;
        if (prePos == null) return;

        for (Player player : playersTeleported)
        {
            ManagedTraveler traveler = ManagedTraveler.getManagedTraveler(player);
            if (traveler == null) continue;
            if(traveler.hasTravelersBlessing()) continue;

            Set<Integer> soulSlots = traveler.getSoulboundSlots();
            Level level = player.level();

            Set<ItemStack> droppedItems = new HashSet<>();
            Inventory inv = player.getInventory();
            for (int slot = 0; slot < inv.getContainerSize(); slot++)
            {
                if (slot >= 36) break; // Skip armor and offhand
                if (soulSlots.contains(slot)) continue;

                ItemStack stack = inv.getItem(slot);
                if(stack.isEmpty()) continue;
                if(stack == player.getMainHandItem()) continue; // skip main hand item

                //check if item has soulbound enchant
                if (stack.isEnchanted()) {
                    if(EnchantmentHelper.hasBindingCurse(stack))
                        continue;
                    Map<Enchantment, Integer> otherEnchants = EnchantmentHelper.getEnchantments(stack);
                    for(Enchantment ench : otherEnchants.keySet()) {
                        //to do
                    }
                }

                // Get or create chest at prePos
                if (!(level.getBlockState(prePos).getBlock() instanceof ChestBlock)) {
                    level.setBlockAndUpdate(prePos, Blocks.CHEST.defaultBlockState());
                }
                BlockEntity be = level.getBlockEntity(prePos);
                if (!(be instanceof ChestBlockEntity chest)) continue;

                inv.removeItem(stack);
                droppedItems.add(stack);
                ItemStack remainder = addToContainer(chest, stack);
                if(remainder.isEmpty()) continue;
                level.addFreshEntity(new ItemEntity(level,
                prePos.getX() + 0.5, prePos.getY() + 1, prePos.getZ() + 0.5,
                 remainder));
            }

            if(droppedItems.isEmpty()) continue;
            String message = Component.translatable("action_hint.hbs_traveler_rewards_compat.waystones.lost_items_warn",
                droppedItems.size()).getString();
            CommonClass.MESSAGER.sendBottomActionHint(player, message);

        }
    }

    /** Attempts to insert a stack into a container, returns whatever couldn't fit. */
    private ItemStack addToContainer(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++)
        {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty()) {
                container.setItem(i, stack);
                stack = ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameTags(existing, stack)
                && existing.getCount() < existing.getMaxStackSize()) {
                int space = existing.getMaxStackSize() - existing.getCount();
                int transfer = Math.min(space, stack.getCount());
                existing.grow(transfer);
                stack.shrink(transfer);
            }
        }
        return stack;
    }



}
