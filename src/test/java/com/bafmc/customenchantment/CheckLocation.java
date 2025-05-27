package com.bafmc.customenchantment;

import org.junit.jupiter.api.Test;

public class CheckLocation {
    // world 7371 102 -6706
    // world 7283 102 -6777
    // teleportenabled: true
    //teleportauto: false
    //godmode: false
    //muted: false
    //jailed: false
    //ip-address: 27.64.223.139
    //afk: false
    //socialspy: false
    //npc: false
    //powertoolsenabled: true
    //accepting-pay: true
    //baltop-exempt: false
    //timestamps:
    //  lastteleport: 1737820846915
    //  lastheal: 1737820859199
    //  mute: 0
    //  jail: 0
    //  onlinejail: 0
    //  logout: 1737832868924
    //  login: 1737836809806
    //money: '2051328.3859470212826288'
    //last-account-name: Niccolas
    //lastlocation:
    //  world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //  world-name: world
    //  x: 6750.699999988079
    //  y: 125.85510010707323
    //  z: -2254.699999988079
    //  yaw: 53.82896041870117
    //  pitch: 64.39840698242188
    //homes:
    //  farm:
    //    world: 808b9e7f-ceff-414e-b347-5bd818203b18
    //    world-name: world_nether
    //    x: -2931.241102762435
    //    y: 111.0
    //    z: -397.2971590192614
    //    yaw: -96.24179077148438
    //    pitch: 10.39775276184082
    //  farm2:
    //    world: 808b9e7f-ceff-414e-b347-5bd818203b18
    //    world-name: world_nether
    //    x: -2653.43514348325
    //    y: 123.0
    //    z: -433.31695488138536
    //    yaw: 145.88058471679688
    //    pitch: 18.945106506347656
    //  zombie:
    //    world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //    world-name: world
    //    x: 6770.666585072064
    //    y: 287.0
    //    z: -898.2373275252277
    //    yaw: -140.22286987304688
    //    pitch: 6.421797275543213
    //  farm3:
    //    world: 808b9e7f-ceff-414e-b347-5bd818203b18
    //    world-name: world_nether
    //    x: -2574.556136847595
    //    y: 123.0
    //    z: -433.1663562344308
    //    yaw: 73.300048828125
    //    pitch: 44.099857330322266
    //  blaze:
    //    world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //    world-name: world
    //    x: 5773.5
    //    y: 303.0
    //    z: -909.5
    //    yaw: -92.347900390625
    //    pitch: 13.94139289855957
    //  mia:
    //    world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //    world-name: world
    //    x: 6564.464104443442
    //    y: 98.1
    //    z: -2168.2038130923756
    //    yaw: 12.08726978302002
    //    pitch: 25.752986907958984
    //  fuck:
    //    world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //    world-name: world
    //    x: 6773.451755329317
    //    y: 307.0
    //    z: -894.5067044202816
    //    yaw: -166.69337463378906
    //    pitch: 10.146628379821777
    //  nongsan:
    //    world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //    world-name: world
    //    x: 6750.699999988079
    //    y: 184.36781472997748
    //    z: -2254.699999988079
    //    yaw: 53.528961181640625
    //    pitch: 64.39840698242188
    //logoutlocation:
    //  world: ea877e5d-6b4f-4a1d-93da-109f673cbf4a
    //  world-name: world
    //  x: 6704.972774720747
    //  y: 173.0
    //  z: -2180.546745515395
    //  yaw: 0.0
    //  pitch: -30.815017700195312
    //ignore:
    //- 4a211646-8cb2-3e8a-98d4-9127acdc6917
    @Test
    public void onTest() {
//        File folder = new File("src/test/resources/userdata");
//
//        Set<String> keys = new HashSet<>();
//
//        for (File file : folder.listFiles()) {
//            if (!file.getName().endsWith(".yml")) {
//                continue;
//            }
//
//            AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);
//
//            List<TestLocation> list = new ArrayList<>();
//
//            TestLocation lastLocation = new TestLocation();
//            lastLocation.world = config.getString("lastlocation.world-name");
//            lastLocation.x = config.getDouble("lastlocation.x");
//            lastLocation.y = config.getDouble("lastlocation.y");
//            lastLocation.z = config.getDouble("lastlocation.z");
//            list.add(lastLocation);
//
//            TestLocation logoutLocation = new TestLocation();
//            logoutLocation.world = config.getString("logoutlocation.world-name");
//            logoutLocation.x = config.getDouble("logoutlocation.x");
//            logoutLocation.y = config.getDouble("logoutlocation.y");
//            logoutLocation.z = config.getDouble("logoutlocation.z");
//            list.add(logoutLocation);
//
//            for (String home : config.getKeySection("homes", false)) {
//                TestLocation homeLocation = new TestLocation();
//                homeLocation.world = config.getString("homes." + home + ".world-name");
//                homeLocation.x = config.getDouble("homes." + home + ".x");
//                homeLocation.y = config.getDouble("homes." + home + ".y");
//                homeLocation.z = config.getDouble("homes." + home + ".z");
//                list.add(homeLocation);
//            }
//
//            for (TestLocation location : list) {
//                // Between // world 7371 102 -6706
//                //    // world 7283 102 -6777
//
//                if (location.world == null) {
//                    continue;
//                }
//
//                if (location.world.equals("world")) {
//                    if (location.x >= 7283 && location.x <= 7371) {
//                        if (location.z >= -6777 && location.z <= -6706) {
//                            String playerName = config.getString("last-account-name");
//                            keys.add(playerName);
//                        }
//                    }
//                }
//            }
//        }
//
//        for (String key : keys) {
//            System.out.println(key);
//        }
    }

    public static class TestLocation {
        public String world;
        public double x;
        public double y;
        public double z;
    }
}
