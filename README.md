# Blaze Spigot

Blaze Spigot is a 1.8.8 Spigot fork focused on performance and PvP.

## Download

Download the latest jars from the [releases page](https://github.com/deyoyk/blaze/releases/latest):

- [blaze.jar](https://github.com/deyoyk/blaze/releases/latest/download/blaze.jar), the server jar
- [blaze-api.jar](https://github.com/deyoyk/blaze/releases/latest/download/blaze-api.jar), the public API for plugin integrations

## Features

Server:

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

Anticheat (built in, experimental, disabled by default):

- Speed, flight, timer, reach, hitbox, phase, NoSlow, FastPlace/FastBreak and more, each with its own config toggle
- Transaction-verified knockback
- Movement prediction
- Statistical click analysis

## Anticheat

The anticheat is experimental and under active development. It is disabled by default. Enable it with the `enabled` option in `blaze/anticheat/config.yml` (generated on first run).

- **Transaction-verified knockback.** Every knockback is sandwiched between protocol-level confirm transactions. Clients that ignore knockback accumulate unconfirmed packets and get flagged. Delivery and feel stay byte-identical.
- **Movement prediction.** Vanilla physics are simulated server-side to flag impossible deltas such as hover, modified gravity, and fake falls. Flight, vehicles, water, climbables, teleports, and potion effects are exempt.
- **Statistical click analysis.** Click intervals are checked for entropy, equal-delay runs, consistency, and CPS tiers. Each subcheck requires sustained windows before flagging.

Measured on a live 1.8.8 server under sustained PvP bot load:

- 0 false flags over 600 ticks of legit PvP (jump-crits, w-taps, knockback combos)
- 0 flags over 400 ticks on a clean idle bot
- Cheat clients (knockback-ignore, hover, constant-rate clicking) each banned automatically
- 0 missed hits, knockback values unchanged (kbY 0.390, kbXZ 0.675)
- Under 1% of the tick budget (~0.35 ms/tick), verified by CPU-sampling A/B with the anticheat on and off

## Updates

The server checks GitHub on startup and, when a newer release exists, downloads it and restarts once to apply it. Configure it in `blaze/updater.yml`.

## Build

```bash
mvn clean package
```

The server jar is written to `server/target/blaze.jar` and the API jar to `api/target/blaze-api-<version>.jar`.

## License

Original Blaze code is proprietary and requires a commercial license for
business use. The project also contains GPLv3 code from Bukkit, Spigot, and
CraftBukkit. Those parts remain under GPLv3. See `LICENSE.txt`,
`LICENSE-GPL-3.0.txt`, and `NOTICE`.
