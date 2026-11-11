package org.bukkit;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang.Validate;

public class Note {
   private final byte note;

   public Note(int note) {
      Validate.isTrue(note >= 0 && note <= 24, "The note value has to be between 0 and 24.");
      this.note = (byte)note;
   }

   public Note(int octave, org.bukkit.Note.Tone tone, boolean sharped) {
      if (sharped && !tone.isSharpable()) {
         tone = org.bukkit.Note.Tone.values()[tone.ordinal() + 1];
         sharped = false;
      }

      if (octave >= 0 && octave <= 2 && (octave != 2 || tone == org.bukkit.Note.Tone.F && sharped)) {
         this.note = (byte)(octave * 12 + tone.getId(sharped));
      } else {
         throw new IllegalArgumentException("Tone and octave have to be between F#0 and F#2");
      }
   }

   public static org.bukkit.Note flat(int octave, org.bukkit.Note.Tone tone) {
      Validate.isTrue(octave != 2, "Octave cannot be 2 for flats");
      tone = tone == org.bukkit.Note.Tone.G ? org.bukkit.Note.Tone.F : org.bukkit.Note.Tone.values()[tone.ordinal() - 1];
      return new org.bukkit.Note(octave, tone, tone.isSharpable());
   }

   public static org.bukkit.Note sharp(int octave, org.bukkit.Note.Tone tone) {
      return new org.bukkit.Note(octave, tone, true);
   }

   public static org.bukkit.Note natural(int octave, org.bukkit.Note.Tone tone) {
      Validate.isTrue(octave != 2, "Octave cannot be 2 for naturals");
      return new org.bukkit.Note(octave, tone, false);
   }

   public org.bukkit.Note sharped() {
      Validate.isTrue(this.note < 24, "This note cannot be sharped because it is the highest known note!");
      return new org.bukkit.Note(this.note + 1);
   }

   public org.bukkit.Note flattened() {
      Validate.isTrue(this.note > 0, "This note cannot be flattened because it is the lowest known note!");
      return new org.bukkit.Note(this.note - 1);
   }

   @Deprecated
   public byte getId() {
      return this.note;
   }

   public int getOctave() {
      return this.note / 12;
   }

   private byte getToneByte() {
      return (byte)(this.note % 12);
   }

   public org.bukkit.Note.Tone getTone() {
      return org.bukkit.Note.Tone.getById(this.getToneByte());
   }

   public boolean isSharped() {
      byte note = this.getToneByte();
      return org.bukkit.Note.Tone.getById(note).isSharped(note);
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + this.note;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         org.bukkit.Note other = (org.bukkit.Note)obj;
         return this.note == other.note;
      }
   }

   @Override
   public String toString() {
      return "Note{" + this.getTone().toString() + (this.isSharped() ? "#" : "") + "}";
   }

   public static enum Tone {
      G(1, true),
      A(3, true),
      B(5, false),
      C(6, true),
      D(8, true),
      E(10, false),
      F(11, true);

      private final boolean sharpable;
      private final byte id;
      private static final Map<Byte, org.bukkit.Note.Tone> BY_DATA = Maps.newHashMap();
      public static final byte TONES_COUNT = 12;

      private Tone(int id, boolean sharpable) {
         this.id = (byte)(id % 12);
         this.sharpable = sharpable;
      }

      @Deprecated
      public byte getId() {
         return this.getId(false);
      }

      @Deprecated
      public byte getId(boolean sharped) {
         byte id = (byte)(sharped && this.sharpable ? this.id + 1 : this.id);
         return (byte)(id % 12);
      }

      public boolean isSharpable() {
         return this.sharpable;
      }

      @Deprecated
      public boolean isSharped(byte id) {
         if (id == this.getId(false)) {
            return false;
         } else if (id == this.getId(true)) {
            return true;
         } else {
            throw new IllegalArgumentException("The id isn't matching to the tone.");
         }
      }

      @Deprecated
      public static org.bukkit.Note.Tone getById(byte id) {
         return BY_DATA.get(id);
      }

      static {
         for (org.bukkit.Note.Tone tone : values()) {
            int id = tone.id % 12;
            BY_DATA.put((byte)id, tone);
            if (tone.isSharpable()) {
               id = (id + 1) % 12;
               BY_DATA.put((byte)id, tone);
            }
         }
      }
   }
}
