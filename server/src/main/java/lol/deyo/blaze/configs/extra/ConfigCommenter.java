package lol.deyo.blaze.configs.extra;

import lol.deyo.blaze.utils.JavaUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigCommenter {
   private final Map<String, String> comments = new HashMap<>();
   private final Map<String, String> commentsNextTo = new HashMap<>();
   private String header = null;

   public void addCommentNextTo(String path, String comment) {
      this.commentsNextTo.put(path, comment);
   }

   public void addComment(String path, String comment) {
      this.comments.put(path, comment);
   }

   public void setHeader(String header) {
      this.header = header;
   }

   public void saveComments(File file) throws IOException {
      ArrayList<String> lines = (ArrayList<String>)Files.readAllLines(file.toPath());
      lines.removeIf(s -> s.trim().startsWith("#") || s.trim().length() <= 4);
      if (this.header != null && !this.header.isEmpty()) {
         lines.add(0, "# " + this.header.replace("\n", "\n# ") + "\n");
      }

      for (Entry<String, String> comment : this.comments.entrySet()) {
         int line = JavaUtils.findKey(lines, comment.getKey());
         if (line == -1) {
            throw new IllegalStateException(String.format("You are trying to add a comment to key \"%s\" which does not exist!", comment.getKey()));
         }

         if (line >= 0) {
            String prefix = JavaUtils.repeat(" ", JavaUtils.getIndentation(lines.get(line))) + "# ";
            boolean noNewline = JavaUtils.getIndentation(lines.get(line)) > JavaUtils.getIndentation(lines.get(line - 1));
            lines.add(line, (noNewline ? "" : "\n") + prefix + comment.getValue().replace("\n", "\n" + prefix));
         } else {
            System.out.printf("Failed to find key %s in %s!", comment.getKey(), file);
         }
      }

      for (Entry<String, String> comment : this.commentsNextTo.entrySet()) {
         int linex = JavaUtils.findKey(lines, comment.getKey());
         if (linex == -1) {
            throw new IllegalStateException(String.format("You are trying to add a comment to key \"%s\" which does not exist!", comment.getKey()));
         }

         if (linex >= 0) {
            lines.set(linex, lines.get(linex) + " # " + comment.getValue());
         } else {
            System.out.printf("Failed to find key %s in %s!", comment.getKey(), file);
         }
      }

      String text = String.join("\n", lines);
      FileWriter fw = new FileWriter(file);
      fw.write(text);
      fw.close();
   }
}
