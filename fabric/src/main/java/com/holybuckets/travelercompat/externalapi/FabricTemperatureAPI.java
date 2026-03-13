package com.holybuckets.travelercompat.externalapi;

import com.holybuckets.traveler.core.ManagedTravelerTickEvent;
import com.holybuckets.traveler.core.ManagedTraveler;
import net.blay09.mods.balm.api.Balm;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.effect.MobEffectInstance;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

public class FabricTemperatureAPI implements TemperatureAPI
{

    public FabricTemperatureAPI() {
        Balm.getEvents().onEvent(ManagedTravelerTickEvent.class, this::onManagedTravelerTick);
    }

    private static final int EFFECT_DURATION_TICKS = 60;

    @Override
    public void onManagedTravelerTick(ManagedTravelerTickEvent event) {
        if(event.getManagedTraveler()==null) return;
        if(event.getManagedTraveler().hasCoolBreezeBlessing())
            onCoolBreezeBlessing(event);
        if(event.getManagedTraveler().hasWarmWindsBlessing())
            onWarmWindsBlessing(event);
    }

    @Override
    public void onCoolBreezeBlessing(ManagedTravelerTickEvent event) {
        ServerPlayer player = getServerPlayer(event);
        if (player == null) return;

        TemperatureLevel envLevel = TemperatureHelper.getTemperatureAtPos(player.level(), player.blockPosition());
        if (envLevel == TemperatureLevel.HOT || envLevel == TemperatureLevel.WARM) {
            applyClimateClemency(player);
        }
    }

    @Override
    public void onWarmWindsBlessing(ManagedTravelerTickEvent event) {
        ServerPlayer player = getServerPlayer(event);
        if (player == null) return;

        TemperatureLevel envLevel = TemperatureHelper.getTemperatureAtPos(player.level(), player.blockPosition());
        if (envLevel == TemperatureLevel.ICY || envLevel == TemperatureLevel.COLD) {
            applyClimateClemency(player);
        }
    }

    private void applyClimateClemency(ServerPlayer player) {
        if (!player.hasEffect(TANEffects.CLIMATE_CLEMENCY)) {
            // ambient=false, visible=false keeps it hidden from HUD
            player.addEffect(new MobEffectInstance(TANEffects.CLIMATE_CLEMENCY, EFFECT_DURATION_TICKS, 0, false, false));
        }
    }

    private ServerPlayer getServerPlayer(ManagedTravelerTickEvent event) {
        ManagedTraveler traveler = event.getManagedTraveler();
        if (traveler == null) return null;
        if (!(traveler.getPlayer() instanceof ServerPlayer sp)) return null;
        return sp;
    }

}