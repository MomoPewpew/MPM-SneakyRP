package noppes.mpm.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import net.minecraft.client.renderer.ImageBufferDownload;

public class ImageBufferDownloadAlt extends ImageBufferDownload {
     private int[] imageData;
     private int imageWidth;
     private int imageHeight;
     private boolean fix64;

     public ImageBufferDownloadAlt(boolean fix64) {
          this.fix64 = fix64;
     }

     @Override
     public BufferedImage parseUserSkin(BufferedImage bufferedimage) {
          this.imageWidth = bufferedimage.getWidth((ImageObserver)null);
          this.imageHeight = bufferedimage.getHeight((ImageObserver)null);
          BufferedImage bufferedimage1 = new BufferedImage(this.imageWidth, this.fix64 ? this.imageWidth : this.imageHeight, 2);
          Graphics graphics = bufferedimage1.getGraphics();
          graphics.drawImage(bufferedimage, 0, 0, (ImageObserver)null);
          float scale = (float)this.imageWidth / 64.0F;
          boolean bo = this.fix64 && this.imageWidth / 2 >= this.imageHeight;
          if (bo) {
               graphics.setColor(new Color(0, 0, 0, 0));
               graphics.fillRect(0, 32, 64, 32);
               this.drawImage(graphics, bufferedimage1, 24, 48, 20, 52, 4, 16, 8, 20, scale);
               this.drawImage(graphics, bufferedimage1, 28, 48, 24, 52, 8, 16, 12, 20, scale);
               this.drawImage(graphics, bufferedimage1, 20, 52, 16, 64, 8, 20, 12, 32, scale);
               this.drawImage(graphics, bufferedimage1, 24, 52, 20, 64, 4, 20, 8, 32, scale);
               this.drawImage(graphics, bufferedimage1, 28, 52, 24, 64, 0, 20, 4, 32, scale);
               this.drawImage(graphics, bufferedimage1, 32, 52, 28, 64, 12, 20, 16, 32, scale);
               this.drawImage(graphics, bufferedimage1, 40, 48, 36, 52, 44, 16, 48, 20, scale);
               this.drawImage(graphics, bufferedimage1, 44, 48, 40, 52, 48, 16, 52, 20, scale);
               this.drawImage(graphics, bufferedimage1, 36, 52, 32, 64, 48, 20, 52, 32, scale);
               this.drawImage(graphics, bufferedimage1, 40, 52, 36, 64, 44, 20, 48, 32, scale);
               this.drawImage(graphics, bufferedimage1, 44, 52, 40, 64, 40, 20, 44, 32, scale);
               this.drawImage(graphics, bufferedimage1, 48, 52, 44, 64, 52, 20, 56, 32, scale);
          }

          graphics.dispose();
          this.imageData = ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
          if (bo) {
               this.setAreaTransparent(this.imageWidth / 2, 0, this.imageWidth, this.imageHeight / 2);
          }

          return bufferedimage1;
     }

     private void drawImage(Graphics graphics, BufferedImage bufferedimage, int x, int y, int x2, int y2, int xx, int yy, int xx2, int yy2, float scale) {
          graphics.drawImage(bufferedimage, (int)((float)x * scale), (int)((float)y * scale), (int)((float)x2 * scale), (int)((float)y2 * scale), (int)((float)xx * scale), (int)((float)yy * scale), (int)((float)xx2 * scale), (int)((float)yy2 * scale), (ImageObserver)null);
     }

     private void setAreaTransparent(int par1, int par2, int par3, int par4) {
          if (!this.hasTransparency(par1, par2, par3, par4)) {
               for(int i1 = par1; i1 < par3; ++i1) {
                    for(int j1 = par2; j1 < par4; ++j1) {
                         int[] var10000 = this.imageData;
                         int var10001 = i1 + j1 * this.imageWidth;
                         var10000[var10001] &= 16777215;
                    }
               }
          }

     }

     private boolean hasTransparency(int par1, int par2, int par3, int par4) {
          for(int i1 = par1; i1 < par3; ++i1) {
               for(int j1 = par2; j1 < par4; ++j1) {
                    int k1 = this.imageData[i1 + j1 * this.imageWidth];
                    if ((k1 >> 24 & 255) < 128) {
                         return true;
                    }
               }
          }

          return false;
     }
}
