package org.github.paperspigot;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class Title {
   public static final int DEFAULT_FADE_IN = 20;
   public static final int DEFAULT_STAY = 200;
   public static final int DEFAULT_FADE_OUT = 20;
   private final BaseComponent[] title;
   private final BaseComponent[] subtitle;
   private final int fadeIn;
   private final int stay;
   private final int fadeOut;

   public Title(BaseComponent title) {
      this(title, null);
   }

   public Title(BaseComponent[] title) {
      this(title, null);
   }

   public Title(String title) {
      this(title, null);
   }

   public Title(BaseComponent title, BaseComponent subtitle) {
      this(title, subtitle, 20, 200, 20);
   }

   public Title(BaseComponent[] title, BaseComponent[] subtitle) {
      this(title, subtitle, 20, 200, 20);
   }

   public Title(String title, String subtitle) {
      this(title, subtitle, 20, 200, 20);
   }

   public Title(BaseComponent title, BaseComponent subtitle, int fadeIn, int stay, int fadeOut) {
      this(
         new BaseComponent[]{(BaseComponent)Preconditions.checkNotNull(title, "title")},
         subtitle == null ? null : new BaseComponent[]{subtitle},
         fadeIn,
         stay,
         fadeOut
      );
   }

   public Title(BaseComponent[] title, BaseComponent[] subtitle, int fadeIn, int stay, int fadeOut) {
      Preconditions.checkArgument(fadeIn >= 0, "Negative fadeIn: %s", new Object[]{fadeIn});
      Preconditions.checkArgument(stay >= 0, "Negative stay: %s", new Object[]{stay});
      Preconditions.checkArgument(fadeOut >= 0, "Negative fadeOut: %s", new Object[]{fadeOut});
      this.title = (BaseComponent[])Preconditions.checkNotNull(title, "title");
      this.subtitle = subtitle;
      this.fadeIn = fadeIn;
      this.stay = stay;
      this.fadeOut = fadeOut;
   }

   public Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
      this(
         TextComponent.fromLegacyText((String)Preconditions.checkNotNull(title, "title")),
         subtitle == null ? null : TextComponent.fromLegacyText(subtitle),
         fadeIn,
         stay,
         fadeOut
      );
   }

   public BaseComponent[] getTitle() {
      return this.title;
   }

   public BaseComponent[] getSubtitle() {
      return this.subtitle;
   }

   public int getFadeIn() {
      return this.fadeIn;
   }

   public int getStay() {
      return this.stay;
   }

   public int getFadeOut() {
      return this.fadeOut;
   }

   public static Title.Builder builder() {
      return new Title.Builder();
   }

   public static final class Builder {
      private BaseComponent[] title;
      private BaseComponent[] subtitle;
      private int fadeIn = 20;
      private int stay = 200;
      private int fadeOut = 20;

      public Title.Builder title(BaseComponent title) {
         return this.title(new BaseComponent[]{(BaseComponent)Preconditions.checkNotNull(title, "title")});
      }

      public Title.Builder title(BaseComponent[] title) {
         this.title = (BaseComponent[])Preconditions.checkNotNull(title, "title");
         return this;
      }

      public Title.Builder title(String title) {
         return this.title(TextComponent.fromLegacyText((String)Preconditions.checkNotNull(title, "title")));
      }

      public Title.Builder subtitle(BaseComponent subtitle) {
         return this.subtitle(subtitle == null ? null : new BaseComponent[]{subtitle});
      }

      public Title.Builder subtitle(BaseComponent[] subtitle) {
         this.subtitle = subtitle;
         return this;
      }

      public Title.Builder subtitle(String subtitle) {
         return this.subtitle(subtitle == null ? null : TextComponent.fromLegacyText(subtitle));
      }

      public Title.Builder fadeIn(int fadeIn) {
         Preconditions.checkArgument(fadeIn >= 0, "Negative fadeIn: %s", new Object[]{fadeIn});
         this.fadeIn = fadeIn;
         return this;
      }

      public Title.Builder stay(int stay) {
         Preconditions.checkArgument(stay >= 0, "Negative stay: %s", new Object[]{stay});
         this.stay = stay;
         return this;
      }

      public Title.Builder fadeOut(int fadeOut) {
         Preconditions.checkArgument(fadeOut >= 0, "Negative fadeOut: %s", new Object[]{fadeOut});
         this.fadeOut = fadeOut;
         return this;
      }

      public Title build() {
         Preconditions.checkState(this.title != null, "Title not specified");
         return new Title(this.title, this.subtitle, this.fadeIn, this.stay, this.fadeOut);
      }
   }
}
