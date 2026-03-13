package com.holybuckets.travelercompat.externalapi;
import com.holybuckets.traveler.core.ManagedTravelerTickEvent;

public interface TemperatureAPI {

    void onManagedTravelerTick(ManagedTravelerTickEvent event);

    void onCoolBreezeBlessing(ManagedTravelerTickEvent event);

    void onWarmWindsBlessing(ManagedTravelerTickEvent event);
}
