package lol.deyo.blaze.utils.fanciful;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.function.UnaryOperator;

public interface JsonRepresentedObject {
   void writeJson(JsonWriter var1, UnaryOperator<String> var2) throws IOException;
}
