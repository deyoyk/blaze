package org.bukkit.craftbukkit.v1_8_R3.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;

public final class CraftChatMessage {
   private static final Pattern LINK_PATTERN = Pattern.compile("((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[§ \\n]|$))))");

   public static IChatBaseComponent[] fromString(String message) {
      return fromString(message, false);
   }

   public static IChatBaseComponent[] fromString(String message, boolean keepNewlines) {
      return new CraftChatMessage.StringMessage(message, keepNewlines).getOutput();
   }

   public static String fromComponent(IChatBaseComponent component) {
      return fromComponent(component, EnumChatFormat.BLACK);
   }

   public static String fromComponent(IChatBaseComponent component, EnumChatFormat defaultColor) {
      if (component == null) {
         return "";
      } else {
         StringBuilder out = new StringBuilder();
         long start = System.currentTimeMillis();

         for (IChatBaseComponent c : component) {
            ChatModifier modi = c.getChatModifier();
            out.append(modi.getColor() == null ? defaultColor : modi.getColor());
            if (modi.isBold()) {
               out.append(EnumChatFormat.BOLD);
            }

            if (modi.isItalic()) {
               out.append(EnumChatFormat.ITALIC);
            }

            if (modi.isUnderlined()) {
               out.append(EnumChatFormat.UNDERLINE);
            }

            if (modi.isStrikethrough()) {
               out.append(EnumChatFormat.STRIKETHROUGH);
            }

            if (modi.isRandom()) {
               out.append(EnumChatFormat.OBFUSCATED);
            }

            out.append(c.getText());
            if (System.currentTimeMillis() - start > 10L) {
               System.out.println("The player below has tried to crash the server with long lines book exploit");
            }
         }

         return out.toString().replaceFirst("^(" + defaultColor + ")*", "");
      }
   }

   public static IChatBaseComponent fixComponent(IChatBaseComponent component) {
      Matcher matcher = LINK_PATTERN.matcher("");
      return fixComponent(component, matcher);
   }

   private static IChatBaseComponent fixComponent(IChatBaseComponent component, Matcher matcher) {
      if (component instanceof ChatComponentText) {
         ChatComponentText text = (ChatComponentText)component;
         String msg = text.g();
         if (matcher.reset(msg).find()) {
            matcher.reset();
            ChatModifier modifier = text.getChatModifier() != null ? text.getChatModifier() : new ChatModifier();
            List<IChatBaseComponent> extras = new ArrayList<>();
            List<IChatBaseComponent> extrasOld = new ArrayList<>(text.a());
            component = text = new ChatComponentText("");

            int pos;
            for (pos = 0; matcher.find(); pos = matcher.end()) {
               String match = matcher.group();
               if (!match.startsWith("http://") && !match.startsWith("https://")) {
                  match = "http://" + match;
               }

               ChatComponentText prev = new ChatComponentText(msg.substring(pos, matcher.start()));
               prev.setChatModifier(modifier);
               extras.add(prev);
               ChatComponentText link = new ChatComponentText(matcher.group());
               ChatModifier linkModi = modifier.clone();
               linkModi.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, match));
               link.setChatModifier(linkModi);
               extras.add(link);
            }

            ChatComponentText prev = new ChatComponentText(msg.substring(pos));
            prev.setChatModifier(modifier);
            extras.add(prev);
            extras.addAll(extrasOld);

            for (IChatBaseComponent c : extras) {
               text.addSibling(c);
            }
         }
      }

      List<IChatBaseComponent> extras = component.a();

      for (int i = 0; i < extras.size(); i++) {
         IChatBaseComponent comp = extras.get(i);
         if (comp.getChatModifier() != null && comp.getChatModifier().h() == null) {
            extras.set(i, fixComponent(comp, matcher));
         }
      }

      if (component instanceof ChatMessage) {
         Object[] subs = ((ChatMessage)component).j();

         for (int ix = 0; ix < subs.length; ix++) {
            Object comp = subs[ix];
            if (comp instanceof IChatBaseComponent) {
               IChatBaseComponent c = (IChatBaseComponent)comp;
               if (c.getChatModifier() != null && c.getChatModifier().h() == null) {
                  subs[ix] = fixComponent(c, matcher);
               }
            } else if (comp instanceof String && matcher.reset((String)comp).find()) {
               subs[ix] = fixComponent(new ChatComponentText((String)comp), matcher);
            }
         }
      }

      return component;
   }

   private CraftChatMessage() {
   }

   private static class StringMessage {
      private static final Map<Character, EnumChatFormat> formatMap;
      private static final Pattern INCREMENTAL_PATTERN = Pattern.compile(
         "(§[0-9a-fk-or])|(\\n)|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[§ \\n]|$))))", 2
      );
      private final List<IChatBaseComponent> list = new ArrayList<>();
      private IChatBaseComponent currentChatComponent = new ChatComponentText("");
      private ChatModifier modifier = new ChatModifier();
      private final IChatBaseComponent[] output;
      private int currentIndex;
      private final String message;

      static {
         Builder<Character, EnumChatFormat> builder = ImmutableMap.builder();

         EnumChatFormat[] var4;
         for (EnumChatFormat format : var4 = EnumChatFormat.values()) {
            builder.put(Character.toLowerCase(format.toString().charAt(1)), format);
         }

         formatMap = builder.build();
      }

      private StringMessage(String message, boolean keepNewlines) {
         this.message = message;
         if (message == null) {
            this.output = new IChatBaseComponent[]{this.currentChatComponent};
         } else {
            this.list.add(this.currentChatComponent);
            Matcher matcher = INCREMENTAL_PATTERN.matcher(message);

            while (matcher.find()) {
               int groupId = 0;

               String match;
               while ((match = matcher.group(++groupId)) == null) {
               }

               this.appendNewComponent(matcher.start(groupId));
               label45:
               switch (groupId) {
                  case 1:
                     EnumChatFormat format = formatMap.get(match.toLowerCase().charAt(1));
                     if (format == EnumChatFormat.RESET) {
                        this.modifier = new ChatModifier();
                     } else if (format.isFormat()) {
                        switch (format) {
                           case OBFUSCATED:
                              this.modifier.setRandom(Boolean.TRUE);
                              break label45;
                           case BOLD:
                              this.modifier.setBold(Boolean.TRUE);
                              break label45;
                           case STRIKETHROUGH:
                              this.modifier.setStrikethrough(Boolean.TRUE);
                              break label45;
                           case UNDERLINE:
                              this.modifier.setUnderline(Boolean.TRUE);
                              break label45;
                           case ITALIC:
                              this.modifier.setItalic(Boolean.TRUE);
                              break label45;
                           default:
                              throw new AssertionError("Unexpected message format");
                        }
                     } else {
                        this.modifier = new ChatModifier().setColor(format);
                     }
                     break;
                  case 2:
                     if (keepNewlines) {
                        this.currentChatComponent.addSibling(new ChatComponentText("\n"));
                     } else {
                        this.currentChatComponent = null;
                     }
                     break;
                  case 3:
                     if (!match.startsWith("http://") && !match.startsWith("https://")) {
                        match = "http://" + match;
                     }

                     this.modifier.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, match));
                     this.appendNewComponent(matcher.end(groupId));
                     this.modifier.setChatClickable(null);
               }

               this.currentIndex = matcher.end(groupId);
            }

            if (this.currentIndex < message.length()) {
               this.appendNewComponent(message.length());
            }

            this.output = this.list.toArray(new IChatBaseComponent[0]);
         }
      }

      private void appendNewComponent(int index) {
         if (index > this.currentIndex) {
            IChatBaseComponent addition = new ChatComponentText(this.message.substring(this.currentIndex, index)).setChatModifier(this.modifier);
            this.currentIndex = index;
            this.modifier = this.modifier.clone();
            if (this.currentChatComponent == null) {
               this.currentChatComponent = new ChatComponentText("");
               this.list.add(this.currentChatComponent);
            }

            this.currentChatComponent.addSibling(addition);
         }
      }

      private IChatBaseComponent[] getOutput() {
         return this.output;
      }
   }
}
