# Blaze Spigot

Blaze Spigot is a 1.8.8 Spigot fork focused on performance and PvP.

## Features

- Custom knockback profiles
- Latency-compensated combat with rewound hitboxes and timestamped hits
- Optimized chunk, block, and entity ticking with palette-compressed sections and LZ4 regions
- Async player, world, and NBT saving
- Eigencraft and PandaWire redstone
- Configurable world generator
- AntiXray and activation range tuning
- Ender pearl controls
- Bundled Spark profiler and SlimeWorldManager bridge
- Colorized console with a branded startup banner
- Config GUI and utility commands (ping, TPS, clear lag, chunk unload, max players, `/gmc`, `/gms`)

## Download

Grab the latest jar from the [releases page](https://github.com/deyoyk/blaze/releases/latest).

## Build

```bash
mvn clean package
```

The server jar is written to `server/target/blaze.jar`.

## License

Original Blaze code is proprietary and requires a commercial license for
business use. The project also contains GPLv3 code from Bukkit, Spigot, and
CraftBukkit. Those parts remain under GPLv3. See `LICENSE.txt`,
`LICENSE-GPL-3.0.txt`, and `NOTICE`.
