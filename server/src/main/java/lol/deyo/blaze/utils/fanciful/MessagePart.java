package lol.deyo.blaze.utils.fanciful;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class MessagePart implements JsonRepresentedObject, ConfigurationSerializable, Cloneable {
   public ChatColor color = ChatColor.WHITE;
   public ArrayList<ChatColor> styles = new ArrayList<>();
   public String clickActionName = null;
   public String clickActionData = null;
   public String hoverActionName = null;
   public JsonRepresentedObject hoverActionData = null;
   public TextualComponent text;
   public String insertionData = null;
   public ArrayList<JsonRepresentedObject> translationReplacements = new ArrayList<>();
   static final BiMap<ChatColor, String> stylesToNames;

   static {
      Builder<ChatColor, String> builder = ImmutableBiMap.builder();

      ChatColor[] var4;
      for (ChatColor style : var4 = ChatColor.values()) {
         if (style.isFormat()) {
            String styleName;
            switch (style) {
               case MAGIC:
                  styleName = "obfuscated";
                  break;
               case BOLD:
               case STRIKETHROUGH:
               default:
                  styleName = style.name().toLowerCase();
                  break;
               case UNDERLINE:
                  styleName = "underlined";
            }

            builder.put(style, styleName);
         }
      }

      stylesToNames = builder.build();
      ConfigurationSerialization.registerClass(MessagePart.class);
   }

   public MessagePart(TextualComponent text) {
      this.text = text;
   }

   public MessagePart() {
      this.text = null;
   }

   public boolean hasText() {
      return this.text != null;
   }

   public MessagePart clone() throws CloneNotSupportedException {
      MessagePart obj = (MessagePart)super.clone();
      obj.styles = (ArrayList<ChatColor>)this.styles.clone();
      if (this.hoverActionData instanceof JsonString) {
         obj.hoverActionData = new JsonString(((JsonString)this.hoverActionData).getValue());
      } else if (this.hoverActionData instanceof FancyMessage) {
         obj.hoverActionData = ((FancyMessage)this.hoverActionData).clone();
      }

      obj.translationReplacements = (ArrayList<JsonRepresentedObject>)this.translationReplacements.clone();
      return obj;
   }

   public void writeJson(JsonWriter json) {
      this.writeJson(json, UnaryOperator.identity());
   }

   @Override
   public void writeJson(JsonWriter json, UnaryOperator<String> replacer) {
      try {
         json.beginObject();
         this.text.writeJson(json, replacer);
         json.name("color").value(this.color.name().toLowerCase());

         for (ChatColor style : this.styles) {
            json.name((String)stylesToNames.get(style)).value(true);
         }

         if (this.clickActionName != null && this.clickActionData != null) {
            json.name("clickEvent")
               .beginObject()
               .name("action")
               .value(this.clickActionName)
               .name("value")
               .value(replacer.apply(this.clickActionData))
               .endObject();
         }

         if (this.hoverActionName != null && this.hoverActionData != null) {
            json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value");
            this.hoverActionData.writeJson(json, replacer);
            json.endObject();
         }

         if (this.insertionData != null) {
            json.name("insertion").value(this.insertionData);
         }

         if (!this.translationReplacements.isEmpty() && TextualComponent.isTranslatableText(this.text)) {
            json.name("with").beginArray();

            for (JsonRepresentedObject obj : this.translationReplacements) {
               obj.writeJson(json, replacer);
            }

            json.endArray();
         }

         json.endObject();
      } catch (IOException var5) {
         Bukkit.getLogger().log(Level.WARNING, "A problem occured during writing of JSON string", (Throwable)var5);
      }
   }

   public Map<String, Object> serialize() {
      HashMap<String, Object> map = new HashMap<>();
      map.put("text", this.text);
      map.put("styles", this.styles);
      map.put("color", this.color.getChar());
      map.put("hoverActionName", this.hoverActionName);
      map.put("hoverActionData", this.hoverActionData);
      map.put("clickActionName", this.clickActionName);
      map.put("clickActionData", this.clickActionData);
      map.put("insertion", this.insertionData);
      map.put("translationReplacements", this.translationReplacements);
      return map;
   }

   public static MessagePart deserialize(Map<String, Object> serialized) {
      MessagePart part = new MessagePart((TextualComponent)serialized.get("text"));
      part.styles = (ArrayList<ChatColor>)serialized.get("styles");
      part.color = ChatColor.getByChar(serialized.get("color").toString());
      part.hoverActionName = (String)serialized.get("hoverActionName");
      part.hoverActionData = (JsonRepresentedObject)serialized.get("hoverActionData");
      part.clickActionName = (String)serialized.get("clickActionName");
      part.clickActionData = (String)serialized.get("clickActionData");
      part.insertionData = (String)serialized.get("insertion");
      part.translationReplacements = (ArrayList<JsonRepresentedObject>)serialized.get("translationReplacements");
      return part;
   }
}
