package net.minecraft.server.v1_8_R3;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import lol.deyo.blaze.Blaze;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

public class JsonList<K, V extends JsonListEntry<K>> {
   protected static final Logger a = LogManager.getLogger();
   protected final Gson b;
   private final File c;
   private final Map<String, V> d = Maps.newConcurrentMap();
   private boolean e = true;
   private static final ParameterizedType f = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{JsonListEntry.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };

   public JsonList(File file) {
      this.c = file;
      GsonBuilder gsonbuilder = new GsonBuilder().setPrettyPrinting();
      gsonbuilder.registerTypeHierarchyAdapter(JsonListEntry.class, new JsonList.JsonListEntrySerializer(null));
      this.b = gsonbuilder.create();
   }

   public boolean isEnabled() {
      return this.e;
   }

   public void a(boolean flag) {
      this.e = flag;
   }

   public File c() {
      return this.c;
   }

   public void add(V v0) {
      this.d.put(this.a(v0.getKey()), v0);

      try {
         this.save();
      } catch (IOException var3) {
         a.warn("Could not save the list after adding a user.", var3);
      }
   }

   public V get(K k0) {
      return this.d.computeIfPresent(this.a(k0), (k, v) -> (V)(v.hasExpired() ? null : v));
   }

   public void remove(K k0) {
      this.d.remove(this.a(k0));

      try {
         this.save();
      } catch (IOException var3) {
         a.warn("Could not save the list after removing a user.", var3);
      }
   }

   public String[] getEntries() {
      return this.d.keySet().toArray(new String[0]);
   }

   public Collection<V> getValues() {
      return this.d.values();
   }

   public boolean isEmpty() {
      return this.d.isEmpty();
   }

   protected String a(K k0) {
      return k0.toString();
   }

   protected boolean d(K k0) {
      return this.d.containsKey(this.a(k0));
   }

   private void h() {
      this.d.values().removeIf(JsonListEntry::hasExpired);
   }

   protected JsonListEntry<K> a(JsonObject jsonobject) {
      return new JsonListEntry<>(null, jsonobject);
   }

   protected Map<String, V> e() {
      return this.d;
   }

   public void save() throws IOException {
      this.h();
      Blaze.getInstance().getThreadingManager().getAsyncExecutor().execute(() -> {
         Collection<?> collection = this.d.values();
         String s = this.b.toJson(collection);
         BufferedWriter bufferedwriter = null;

         try {
            bufferedwriter = Files.newWriter(this.c, Charsets.UTF_8);
            bufferedwriter.write(s);
         } catch (IOException var8) {
            a.warn("Failed to save {}", new Object[]{this.c, var8});
         } finally {
            IOUtils.closeQuietly(bufferedwriter);
         }
      });
   }

   public void load() throws FileNotFoundException {
      Collection<JsonListEntry<K>> collection = null;
      BufferedReader bufferedreader = null;

      try {
         bufferedreader = Files.newReader(this.c, Charsets.UTF_8);
         collection = (Collection<JsonListEntry<K>>)this.b.fromJson(bufferedreader, f);
      } catch (FileNotFoundException var9) {
         Bukkit.getLogger().log(Level.INFO, "Unable to find file {0}, creating it.", this.c);
      } catch (JsonSyntaxException var10) {
         Bukkit.getLogger().log(Level.WARNING, "Unable to read file {0}, backing it up to {0}.backup and creating new copy.", this.c);
         File backup = new File(this.c + ".backup");
         this.c.renameTo(backup);
         this.c.delete();
      } finally {
         IOUtils.closeQuietly(bufferedreader);
      }

      if (collection != null) {
         this.d.clear();

         for (JsonListEntry<K> jsonlistentry : collection) {
            if (jsonlistentry.getKey() != null) {
               this.d.put(this.a(jsonlistentry.getKey()), (V)jsonlistentry);
            }
         }
      }
   }

   class JsonListEntrySerializer implements JsonDeserializer<JsonListEntry<K>>, JsonSerializer<JsonListEntry<K>> {
      private JsonListEntrySerializer() {
      }

      public JsonElement a(JsonListEntry<K> jsonlistentry, Type type, JsonSerializationContext jsonserializationcontext) {
         JsonObject jsonobject = new JsonObject();
         jsonlistentry.a(jsonobject);
         return jsonobject;
      }

      public JsonListEntry<K> a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
         return jsonelement.isJsonObject() ? JsonList.this.a(jsonelement.getAsJsonObject()) : null;
      }

      public JsonElement serialize(JsonListEntry<K> object, Type type, JsonSerializationContext jsonserializationcontext) {
         return this.a(object, type, jsonserializationcontext);
      }

      public JsonListEntry<K> deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
         return this.a(jsonelement, type, jsondeserializationcontext);
      }

      JsonListEntrySerializer(Object object) {
         this();
      }
   }
}
