package lol.deyo.blaze.updater;

public final class UpdateInfo {
   private final String tag;
   private final int version;
   private final String assetName;
   private final String downloadUrl;
   private final String pageUrl;

   public UpdateInfo(String tag, int version, String assetName, String downloadUrl, String pageUrl) {
      this.tag = tag;
      this.version = version;
      this.assetName = assetName;
      this.downloadUrl = downloadUrl;
      this.pageUrl = pageUrl;
   }

   public String getTag() {
      return this.tag;
   }

   public int getVersion() {
      return this.version;
   }

   public String getAssetName() {
      return this.assetName;
   }

   public String getDownloadUrl() {
      return this.downloadUrl;
   }

   public String getPageUrl() {
      return this.pageUrl;
   }
}
