package org.bukkit.craftbukkit.v1_8_R3.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.v1_8_R3.WorldMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public final class CraftMapView implements MapView {
   private final Map<CraftPlayer, RenderData> renderCache = new HashMap<>();
   private final List<MapRenderer> renderers = new ArrayList<>();
   private final Map<MapRenderer, Map<CraftPlayer, CraftMapCanvas>> canvases = new HashMap<>();
   final WorldMap worldMap;

   public CraftMapView(WorldMap worldMap) {
      this.worldMap = worldMap;
      this.addRenderer(new CraftMapRenderer(this, worldMap));
   }

   @Override
   public short getId() {
      String text = this.worldMap.id;
      if (text.startsWith("map_")) {
         try {
            return Short.parseShort(text.substring("map_".length()));
         } catch (NumberFormatException var3) {
            throw new IllegalStateException("Map has non-numeric ID");
         }
      } else {
         throw new IllegalStateException("Map has invalid ID");
      }
   }

   @Override
   public boolean isVirtual() {
      return this.renderers.size() > 0 && !(this.renderers.get(0) instanceof CraftMapRenderer);
   }

   @Override
   public MapView.Scale getScale() {
      return MapView.Scale.valueOf(this.worldMap.scale);
   }

   @Override
   public void setScale(MapView.Scale scale) {
      this.worldMap.scale = scale.getValue();
   }

   @Override
   public org.bukkit.World getWorld() {
      byte dimension = this.worldMap.map;

      for (org.bukkit.World world : org.bukkit.Bukkit.getServer().getWorlds()) {
         if (((CraftWorld)world).getHandle().dimension == dimension) {
            return world;
         }
      }

      return null;
   }

   @Override
   public void setWorld(org.bukkit.World world) {
      this.worldMap.map = (byte)((CraftWorld)world).getHandle().dimension;
   }

   @Override
   public int getCenterX() {
      return this.worldMap.centerX;
   }

   @Override
   public int getCenterZ() {
      return this.worldMap.centerZ;
   }

   @Override
   public void setCenterX(int x) {
      this.worldMap.centerX = x;
   }

   @Override
   public void setCenterZ(int z) {
      this.worldMap.centerZ = z;
   }

   @Override
   public List<MapRenderer> getRenderers() {
      return new ArrayList<>(this.renderers);
   }

   @Override
   public void addRenderer(MapRenderer renderer) {
      if (!this.renderers.contains(renderer)) {
         this.renderers.add(renderer);
         this.canvases.put(renderer, new HashMap<>());
         renderer.initialize(this);
      }
   }

   @Override
   public boolean removeRenderer(MapRenderer renderer) {
      if (!this.renderers.contains(renderer)) {
         return false;
      } else {
         this.renderers.remove(renderer);

         for (Entry<CraftPlayer, CraftMapCanvas> entry : this.canvases.get(renderer).entrySet()) {
            for (int x = 0; x < 128; x++) {
               for (int y = 0; y < 128; y++) {
                  entry.getValue().setPixel(x, y, (byte)-1);
               }
            }
         }

         this.canvases.remove(renderer);
         return true;
      }
   }

   private boolean isContextual() {
      for (MapRenderer renderer : this.renderers) {
         if (renderer.isContextual()) {
            return true;
         }
      }

      return false;
   }

   public RenderData render(CraftPlayer player) {
      boolean context = this.isContextual();
      RenderData render = this.renderCache.get(context ? player : null);
      if (render == null) {
         render = new RenderData();
         this.renderCache.put(context ? player : null, render);
      }

      if (context) {
         this.renderCache.remove(null);
      }

      Arrays.fill(render.buffer, (byte)0);
      render.cursors.clear();

      for (MapRenderer renderer : this.renderers) {
         CraftMapCanvas canvas = this.canvases.get(renderer).get(renderer.isContextual() ? player : null);
         if (canvas == null) {
            canvas = new CraftMapCanvas(this);
            this.canvases.get(renderer).put(renderer.isContextual() ? player : null, canvas);
         }

         canvas.setBase(render.buffer);
         renderer.render(this, canvas, player);
         byte[] buf = canvas.getBuffer();

         for (int i = 0; i < buf.length; i++) {
            byte color = buf[i];
            if (color >= 0 || color <= -113) {
               render.buffer[i] = color;
            }
         }

         for (int ix = 0; ix < canvas.getCursors().size(); ix++) {
            render.cursors.add(canvas.getCursors().getCursor(ix));
         }
      }

      return render;
   }
}
