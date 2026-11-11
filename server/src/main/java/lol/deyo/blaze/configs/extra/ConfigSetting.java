package lol.deyo.blaze.configs.extra;

public class ConfigSetting<T> {
   private T value;
   private Config config;
   private String name;
   private String path;
   private String comment;
   private String commentAbove;
   private boolean editable;

   public ConfigSetting(T value, String path, String comment, String commentAbove, boolean editable) {
      this.value = value;
      this.config = null;
      this.name = "";
      this.path = path;
      this.comment = comment;
      this.commentAbove = commentAbove;
      this.editable = editable;
   }

   public ConfigSetting(T value, String path, String comment, String commentAbove) {
      this(value, path, comment, commentAbove, true);
   }

   public ConfigSetting(T value, String path, String comment) {
      this(value, path, comment, null);
   }

   public ConfigSetting(T value, String path, String comment, boolean editable) {
      this(value, path, comment, null, editable);
   }

   public ConfigSetting(T value, String path) {
      this(value, path, null, null);
   }

   public ConfigSetting(T value, String path, boolean editable) {
      this(value, path, null, null, editable);
   }

   public String getFullPath() {
      return (!this.path.isEmpty() ? this.path + "." : "") + this.name;
   }

   public T getValue() {
      return this.value;
   }

   public Object parseValue(String string) {
      try {
         if (this.value instanceof String) {
            return string;
         } else if (this.value instanceof Integer) {
            return Integer.parseInt(string);
         } else if (this.value instanceof Double) {
            return Double.parseDouble(string);
         } else if (this.value instanceof Float) {
            return Float.parseFloat(string);
         } else {
            if (this.value instanceof Boolean) {
               boolean isBool = string.equalsIgnoreCase("false") || string.equalsIgnoreCase("true");
               if (isBool) {
                  return Boolean.parseBoolean(string);
               }
            }

            return null;
         }
      } catch (Exception var3) {
         return null;
      }
   }

   public void set(Object value, boolean save) {
      this.set(value, save, true);
   }

   public void set(Object value, boolean save, boolean saveAsync) {
      this.value = (T)value;
      if (save) {
         this.config.save(saveAsync);
      }
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getCommentAbove() {
      return this.commentAbove;
   }

   public void setCommentAbove(String commentAbove) {
      this.commentAbove = commentAbove;
   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setConfig(Config config) {
      this.config = config;
   }

   public boolean isEditable() {
      return this.editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }
}
