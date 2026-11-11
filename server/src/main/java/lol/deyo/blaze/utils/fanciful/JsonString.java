package lol.deyo.blaze.utils.fanciful;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

final class JsonString implements JsonRepresentedObject, ConfigurationSerializable {
   private final String _value;

   public JsonString(CharSequence value) {
      this._value = value == null ? null : value.toString();
   }

   @Override
   public void writeJson(JsonWriter writer, UnaryOperator<String> replacer) throws IOException {
      writer.value(replacer.apply(this.getValue()));
   }

   public String getValue() {
      return this._value;
   }

   public Map<String, Object> serialize() {
      HashMap<String, Object> theSingleValue = new HashMap<>();
      theSingleValue.put("stringValue", this._value);
      return theSingleValue;
   }

   public static JsonString deserialize(Map<String, Object> map) {
      return new JsonString(map.get("stringValue").toString());
   }

   @Override
   public String toString() {
      return this._value;
   }
}
