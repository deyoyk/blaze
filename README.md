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
- Public API jar for plugin integrations (`blaze-api`)
- Colorized console with a branded startup banner
- Config GUI and utility commands (ping, TPS, clear lag, chunk unload, max players, `/gmc`, `/gms`)

## Download

Download the latest jars from the [releases page](https://github.com/deyoyk/blaze/releases/latest) or directly: [blaze.jar](https://github.com/deyoyk/blaze/releases/latest/download/blaze.jar) and [blaze-api.jar](https://github.com/deyoyk/blaze/releases/latest/download/blaze-api.jar).

## Updates

The server checks GitHub on startup and, when a newer release exists, downloads it and restarts once to apply it. Configure it in `blaze/updater.yml`.

## Build

```bash
mvn clean package
```

The server jar is written to `server/target/blaze.jar` and the API jar to `api/target/blaze-api-<version>.jar`.

## Anticheat

Blaze ships with a built-in anticheat, enabled by default and configurable in `blaze/anticheat/config.yml`.

- **Transaction-verified knockback** — every knockback is sandwiched between protocol-level confirm transactions; clients that ignore knockback accumulate unconfirmed packets and are flagged, while delivery and feel stay byte-identical
- **Movement prediction** — server-side simulation of vanilla physics flags impossible deltas (hover, modified gravity, fake falls) with a full exemption set (flight, vehicles, water, climbables, teleport, potion effects)
- **Statistical click analysis** — click-interval entropy, equal-delay runs, consistency (coefficient of variation) and CPS tiers, each requiring sustained windows before flagging
- Plus speed, flight, timer, reach, hitbox, phase, NoSlow, FastPlace/FastBreak and more, all with per-check config toggles

Measured on a live 1.8.8 server under sustained PvP bot load: legit PvP movement (jump-crits, w-taps, knockback combos) produced **0 false flags over 600 ticks**, a clean idle bot **0 flags over 400 ticks**, while cheat clients (knockback-ignore, hover, constant-rate clicking) were each **banned automatically**. Hit registration stayed at **0 missed hits** with knockback values unchanged (kbY 0.390, kbXZ 0.675), and the full pipeline costs **under 1% of the tick budget (~0.35 ms/tick)**, verified by CPU-sampling A/B with the anticheat on and off.

## License

Original Blaze code is proprietary and requires a commercial license for
business use. The project also contains GPLv3 code from Bukkit, Spigot, and
CraftBukkit. Those parts remain under GPLv3. See `LICENSE.txt`,
`LICENSE-GPL-3.0.txt`, and `NOTICE`.
