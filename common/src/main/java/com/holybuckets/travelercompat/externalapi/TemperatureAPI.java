package com.holybuckets.travelercompat.externalapi;
import com.holybuckets.traveler.core.ManagedTravelerTickEvent;

public interface TemperatureAPI {

    static final int EFFECT_DURATION_TICKS = 240;

    void onManagedTravelerTick(ManagedTravelerTickEvent event);

    void onCoolBreezeBlessing(ManagedTravelerTickEvent event);

    void onWarmWindsBlessing(ManagedTravelerTickEvent event);
}
