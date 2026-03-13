package com.holybuckets.travelercompat.externalapi;

public interface TemperatureAPI {

    /**
     * Called each tick while the Cool Breeze blessing is active on the traveler.
     * Implementations should apply cooling temperature effects via the TAN API.
     */
    void onCoolBreezeBlessingTick(ManagedTravelerTickEvent event);

    /**
     * Called each tick while the Warm Winds blessing is active on the traveler.
     * Implementations should apply warming temperature effects via the TAN API.
     */
    void onWarmWindsBlessingTick(ManagedTravelerTickEvent event);
}
