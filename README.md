# Blaze 1.8.8 — Recovered Source

This directory is a Maven reconstruction of the Blaze 1.8.8 server from the
supplied server jar (SHA-256 `37a389ee8221ae59b9633e15ab6535dc9844b8fa4e090b766d2af3f1959b9bc9`).

## Layout

- `api/` — `blaze-api`: the Bukkit 1.8.8 API sources plus the Blaze API
  interfaces (`lol.deyo.blaze.api.*`, `org.github.paperspigot.Title`, and
  the Paper event executor helpers used by the API).
- `server/` — `blaze-server`: the CraftBukkit server, NMS
  (`net.minecraft.server.v1_8_R3`), Spigot/PaperSpigot/TacoSpigot patches, the
  Blaze implementation (`lol.deyo.blaze.*`), and server resources.

Third-party shaded libraries are not vendored as source. They are declared as
normal Maven dependencies in the two module POMs.

## Build

Requirements: JDK 8 or newer to compile (tested with JDK 21) and Maven 3.9+.

```bash
mvn clean package
```

The runnable server jar is written to:

```
server/target/blaze.jar
```

It is a shaded, self-contained server jar with the same main class as the
original (`org.bukkit.craftbukkit.Main`). A Java 8+ runtime works (the smoke
test was run on JDK 21):

```bash
java -jar server/target/blaze.jar
```

## Recovery notes

The source was recovered with REA (artifact inventory and verification) and
Vineflower 1.11.1 / CFR 0.152. Most classes decompiled cleanly. The bundled
legacy `org.bukkit.craftbukkit.libs.org.ibex.nestedvm` classes were omitted:
they are unreferenced by the server and require an `org.ibex.classgen`
dependency that is not shipped in the original jar. `ChatTypeAdapter` was
inlined by the decompiler into `ChatTypeAdapterFactory` with equivalent
behavior.

The remaining decompiler artifacts were repaired manually:

- bridge/override methods that Vineflower omitted (`Predicate.apply`,
  `Callable.call`, Gson `serialize`/`deserialize`, Netty `encode`/`decode`,
  `CacheLoader.load`, `Iterator.next`);
- ambiguous `null` overload calls and raw collection types;
- try/finally blocks that Vineflower could not inline;
- one class whose body Vineflower could not emit (`CraftAsyncTask.run`) and
  `PlayerSelector`, which were restored from upstream CraftBukkit/Spigot 1.8
  sources and adapted to this fork.

The build has been verified with a clean `mvn clean package` on JDK 21, and the
shaded jar was smoke-tested with a generated flat world:

```bash
java -jar server/target/blaze.jar --version
# 1.8.8-R0.1-SNAPSHOT

java -jar server/target/blaze.jar --port 25599 --world world --noconsole
# ...
# [INFO]: Done (2.901s)! For help, type "help" or "?"
```

## Licensing

CraftBukkit/Spigot/Bukkit portions are GPL-licensed upstream code. The Blaze
fork code belongs to its respective author. Keep the upstream license terms
when redistributing.
