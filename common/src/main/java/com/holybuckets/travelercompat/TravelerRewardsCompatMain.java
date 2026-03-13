package com.holybuckets.travelercompat;


import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.travelercompat.config.TravelerRewardsCompatConfig;
import com.holybuckets.travelercompat.externalapi.TemperatureAPI;
import com.holybuckets.travelercompat.platform.Services;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;

/**
 * Main instance of the mod, initialize this class statically via commonClass
 * This class will init all major Manager instances and events for the mod
 */
public class TravelerRewardsCompatMain {
    private static boolean DEV_MODE = false;;
    public static TravelerRewardsCompatConfig CONFIG;
    public static TravelerRewardsCompatMain INSTANCE;

    public TravelerRewardsCompatMain()
    {
        super();
        INSTANCE = this;
        init();
        // LoggerProject.logInit( "001000", this.getClass().getName() ); // Uncomment if you have a logging system in place
    }

    private void init()
    {

        if (Services.PLATFORM.isModLoaded("toughasnails")) {
            Balm.platformProxy()
                .withForge("com.holybuckets.travelercompat.externalapi.ForgeTemperatureAPI")
                .withFabric("com.holybuckets.travelercompat.externalapi.FabricTemperatureAPI")
                .build();
        }

        Balm.modProxy()
            .with("waystones", "com.holybuckets.travelercompat.waystones.WaystonesSoulboundTeleport")
            .withFallback(null)
            .build();

            //Configs
        Balm.getConfig().registerConfig(TravelerRewardsCompatConfig.class);

        //Events
        EventRegistrar registrar = EventRegistrar.getInstance();
        //ChallengeBlockBehavior.init(registrar);


        //register local events
        registrar.registerOnBeforeServerStarted(this::onServerStarting);

    }

    private void onServerStarting(ServerStartingEvent e) {
        CONFIG = Balm.getConfig().getActiveConfig(TravelerRewardsCompatConfig.class);
        //this.DEV_MODE = CONFIG.devMode;
        this.DEV_MODE = false;
    }


}
