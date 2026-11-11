package org.bukkit.util;

import java.util.LinkedList;
import java.util.List;

public class ChatPaginator {
   public static final int GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH = 55;
   public static final int AVERAGE_CHAT_PAGE_WIDTH = 65;
   public static final int UNBOUNDED_PAGE_WIDTH = Integer.MAX_VALUE;
   public static final int OPEN_CHAT_PAGE_HEIGHT = 20;
   public static final int CLOSED_CHAT_PAGE_HEIGHT = 10;
   public static final int UNBOUNDED_PAGE_HEIGHT = Integer.MAX_VALUE;

   public static ChatPaginator.ChatPage paginate(String unpaginatedString, int pageNumber) {
      return paginate(unpaginatedString, pageNumber, 55, 10);
   }

   public static ChatPaginator.ChatPage paginate(String unpaginatedString, int pageNumber, int lineLength, int pageHeight) {
      String[] lines = wordWrap(unpaginatedString, lineLength);
      int totalPages = lines.length / pageHeight + (lines.length % pageHeight == 0 ? 0 : 1);
      int actualPageNumber = pageNumber <= totalPages ? pageNumber : totalPages;
      int from = (actualPageNumber - 1) * pageHeight;
      int to = from + pageHeight <= lines.length ? from + pageHeight : lines.length;
      String[] selectedLines = Java15Compat.Arrays_copyOfRange(lines, from, to);
      return new ChatPaginator.ChatPage(selectedLines, actualPageNumber, totalPages);
   }

   public static String[] wordWrap(String rawString, int lineLength) {
      if (rawString == null) {
         return new String[]{""};
      } else if (rawString.length() <= lineLength && !rawString.contains("\n")) {
         return new String[]{rawString};
      } else {
         char[] rawChars = (rawString + ' ').toCharArray();
         StringBuilder word = new StringBuilder();
         StringBuilder line = new StringBuilder();
         List<String> lines = new LinkedList<>();
         int lineColorChars = 0;

         for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];
            if (c == 167) {
               word.append(org.bukkit.ChatColor.getByChar(rawChars[i + 1]));
               lineColorChars += 2;
               i++;
            } else if (c != ' ' && c != '\n') {
               word.append(c);
            } else {
               if (line.length() == 0 && word.length() > lineLength) {
                  for (String partialWord : word.toString().split("(?<=\\G.{" + lineLength + "})")) {
                     lines.add(partialWord);
                  }
               } else if (line.length() + word.length() - lineColorChars == lineLength) {
                  line.append((CharSequence)word);
                  lines.add(line.toString());
                  line = new StringBuilder();
                  lineColorChars = 0;
               } else if (line.length() + 1 + word.length() - lineColorChars > lineLength) {
                  for (String partialWord : word.toString().split("(?<=\\G.{" + lineLength + "})")) {
                     lines.add(line.toString());
                     line = new StringBuilder(partialWord);
                  }

                  lineColorChars = 0;
               } else {
                  if (line.length() > 0) {
                     line.append(' ');
                  }

                  line.append((CharSequence)word);
               }

               word = new StringBuilder();
               if (c == '\n') {
                  lines.add(line.toString());
                  line = new StringBuilder();
               }
            }
         }

         if (line.length() > 0) {
            lines.add(line.toString());
         }

         if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != 167) {
            lines.set(0, org.bukkit.ChatColor.WHITE + lines.get(0));
         }

         for (int ix = 1; ix < lines.size(); ix++) {
            String pLine = lines.get(ix - 1);
            String subLine = lines.get(ix);
            char color = pLine.charAt(pLine.lastIndexOf(167) + 1);
            if (subLine.length() == 0 || subLine.charAt(0) != 167) {
               lines.set(ix, org.bukkit.ChatColor.getByChar(color) + subLine);
            }
         }

         return lines.toArray(new String[lines.size()]);
      }
   }

   public static class ChatPage {
      private String[] lines;
      private int pageNumber;
      private int totalPages;

      public ChatPage(String[] lines, int pageNumber, int totalPages) {
         this.lines = lines;
         this.pageNumber = pageNumber;
         this.totalPages = totalPages;
      }

      public int getPageNumber() {
         return this.pageNumber;
      }

      public int getTotalPages() {
         return this.totalPages;
      }

      public String[] getLines() {
         return this.lines;
      }
   }
}
