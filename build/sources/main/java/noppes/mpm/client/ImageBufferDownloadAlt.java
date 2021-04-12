package noppes.mpm.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ImageDownloadAlt extends SimpleTexture {
  private static final Logger logger = LogManager.getLogger();

  private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);

  private final File cacheFile;

  private final String imageUrl;

  private final IImageBuffer imageBuffer;

  private BufferedImage bufferedImage;

  private Thread imageThread;

  private boolean textureUploaded;

  public ImageDownloadAlt(File file, String url, ResourceLocation resource, IImageBuffer buffer) {
    super(resource);
    this.cacheFile = file;
    this.imageUrl = url;
    this.imageBuffer = buffer;
  }

  private void checkTextureUploaded() {
    if (!this.textureUploaded)
      if (this.bufferedImage != null) {
        this.textureUploaded = true;
        if (this.field_110568_b != null)
          func_147631_c();
        TextureUtil.func_110987_a(super.func_110552_b(), this.bufferedImage);
      }
  }

  public int func_110552_b() {
    checkTextureUploaded();
    return super.func_110552_b();
  }

  public void setBufferedImage(BufferedImage p_147641_1_) {
    this.bufferedImage = p_147641_1_;
    if (this.imageBuffer != null)
      this.imageBuffer.func_152634_a();
  }

  public void func_110551_a(IResourceManager resourceManager) throws IOException {
    if (this.bufferedImage == null && this.field_110568_b != null)
      super.func_110551_a(resourceManager);
    if (this.imageThread == null)
      if (this.cacheFile != null && this.cacheFile.isFile()) {
        logger.debug("Loading http texture from local cache ({})", new Object[] { this.cacheFile });
        try {
          this.bufferedImage = ImageIO.read(this.cacheFile);
          if (this.imageBuffer != null)
            setBufferedImage(this.imageBuffer.func_78432_a(this.bufferedImage));
        } catch (IOException ioexception) {
          logger.error("Couldn't load skin " + this.cacheFile, ioexception);
          loadTextureFromServer();
        }
      } else {
        loadTextureFromServer();
      }
  }

  protected void loadTextureFromServer() {
    this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
        private static final String __OBFID = "CL_00001050";

        public void run() {
          HttpURLConnection connection = null;
          ImageDownloadAlt.logger.debug("Downloading http texture from {} to {}", new Object[] { ImageDownloadAlt.access$000(this.this$0), ImageDownloadAlt.access$100(this.this$0) });
          try {
            BufferedImage bufferedimage;
            connection = (HttpURLConnection)(new URL(ImageDownloadAlt.this.imageUrl)).openConnection(Minecraft.getMinecraft().func_110437_J());
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
            connection.connect();
            if (connection.getResponseCode() / 100 != 2)
              return;
            if (ImageDownloadAlt.this.cacheFile != null) {
              FileUtils.copyInputStreamToFile(connection.getInputStream(), ImageDownloadAlt.this.cacheFile);
              bufferedimage = ImageIO.read(ImageDownloadAlt.this.cacheFile);
            } else {
              bufferedimage = TextureUtil.func_177053_a(connection.getInputStream());
            }
            if (ImageDownloadAlt.this.imageBuffer != null)
              bufferedimage = ImageDownloadAlt.this.imageBuffer.func_78432_a(bufferedimage);
            ImageDownloadAlt.this.setBufferedImage(bufferedimage);
          } catch (Exception exception) {
            ImageDownloadAlt.logger.error("Couldn't download http texture", exception);
          } finally {
            if (connection != null)
              connection.disconnect();
          }
        }
      };
    this.imageThread.setDaemon(true);
    this.imageThread.start();
  }
}
