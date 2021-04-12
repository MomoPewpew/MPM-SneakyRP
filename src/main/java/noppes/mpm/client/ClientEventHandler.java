package noppes.mpm.client;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.ServerTickHandler;
import noppes.mpm.client.fx.EntityEnderFX;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.client.gui.GuiMPM;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.constants.EnumParts;
import noppes.mpm.sync.WebApi;
import noppes.mpm.util.MPMEntityUtil;
import org.lwjgl.input.Keyboard;

public class ClientEventHandler {
  private long lastAltClick = 0L;

  private boolean altIsPressed = false;

  private World prevWorld;

  private static final Predicate<EntityPlayer> playerSelector = Predicates.and(new Predicate[] { new Predicate<EntityPlayer>() {
          final double range = 6400.0D;

          public boolean apply(EntityPlayer entity) {
            return (entity != (Minecraft.getMinecraft()).thePlayer && entity.func_70068_e((Entity)(Minecraft.getMinecraft()).thePlayer) <= 6400.0D);
          }
        } });

  public static List<EntityPlayer> playerList;

  @SubscribeEvent
  public void onKey(InputEvent.KeyInputEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc == null || mc.thePlayer == null)
      return;
    if (ClientProxy.Screen.isPressed()) {
      ModelData data = ModelData.get((EntityPlayer)mc.thePlayer);
      data.setAnimation(EnumAnimation.NONE);
      if (mc.currentScreen  == null)
        mc.displayGuiScreen((GuiScreen)new GuiMPM());
    }
    if (mc.currentScreen  == null)
      this.slashPressed = (Keyboard.getEventCharacter() == '/');
    if (!mc.inGameHasFocus)
      return;
    if (ClientProxy.MPM1.isPressed())
      processAnimation(MorePlayerModels.button1);
    if (ClientProxy.MPM2.isPressed())
      processAnimation(MorePlayerModels.button2);
    if (ClientProxy.MPM3.isPressed())
      processAnimation(MorePlayerModels.button3);
    if (ClientProxy.MPM4.isPressed())
      processAnimation(MorePlayerModels.button4);
    if (ClientProxy.MPM5.isPressed())
      processAnimation(MorePlayerModels.button5);
    if (ClientProxy.Camera.isKeyDown() && mc.gameSettings.thirdPersonView  == 1) {
      long time = System.currentTimeMillis();
      if (!this.altIsPressed)
        if (time - this.lastAltClick < 400L) {
          camera.reset();
        } else {
          camera.enabled();
          this.lastAltClick = time;
        }
      this.altIsPressed = true;
    } else {
      this.altIsPressed = false;
    }
  }

  @SubscribeEvent
  public void keyEvent(GuiScreenEvent.KeyboardInputEvent.Pre event) {
    if (event.getGui() instanceof net.minecraft.client.gui.GuiChat) {
      if (!this.slashPressed && Keyboard.getEventCharacter() == '/')
        this.slashPressed = true;
    } else {
      this.slashPressed = false;
    }
  }

  private boolean slashPressed = false;

  @SubscribeEvent
  public void onCommand(CommandEvent event) {
    if (!(event.getCommand() instanceof noppes.mpm.commands.MpmCommandInterface) || event.getSender().getServer() != null || this.slashPressed)
      return;
    event.setCanceled(true);
  }

  @SubscribeEvent
  public void onMouse(MouseEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    if (event.getDwheel() == 0 || !mc.inGameHasFocus || mc.gameSettings.thirdPersonView  != 1 || !camera.enabled || !this.altIsPressed)
      return;
    camera.cameraDistance -= event.getDwheel() / 100.0F;
    if (camera.cameraDistance > 14.0F) {
      camera.cameraDistance = 14.0F;
    } else if (camera.cameraDistance < 1.0F) {
      camera.cameraDistance = 1.0F;
    }
    event.setCanceled(true);
  }

  public static void processAnimation(int type) {
    if (type < 0)
      return;
    if (MorePlayerModels.HasServerSide) {
      Client.sendData(EnumPackets.ANIMATION, new Object[] { Integer.valueOf(type) });
    } else {
      EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).thePlayer;
      EnumAnimation animation = EnumAnimation.values()[type];
      if (animation == EnumAnimation.SLEEPING_SOUTH) {
        float rotation = ((EntityPlayer)entityPlayerSP).rotationYaw;
        while (rotation < 0.0F)
          rotation += 360.0F;
        while (rotation > 360.0F)
          rotation -= 360.0F;
        int rotate = (int)((rotation + 45.0F) / 90.0F);
        if (rotate == 1)
          animation = EnumAnimation.SLEEPING_WEST;
        if (rotate == 2)
          animation = EnumAnimation.SLEEPING_NORTH;
        if (rotate == 3)
          animation = EnumAnimation.SLEEPING_EAST;
      }
      ModelData data = ModelData.get((EntityPlayer)entityPlayerSP);
      if (data.animationEquals(animation))
        animation = EnumAnimation.NONE;
      data.setAnimation(animation.ordinal());
    }
  }

  public static Camera camera = new Camera();

  @SubscribeEvent
  public void onRenderTick(TickEvent.RenderTickEvent event) {
    camera.update((event.phase == TickEvent.Phase.START));
  }

  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.side == Side.SERVER || event.phase == TickEvent.Phase.START)
      return;
    Minecraft mc = Minecraft.getMinecraft();
    if (mc.theWorld == null)
      return;
    if (this.prevWorld != mc.theWorld) {
      MorePlayerModels.HasServerSide = false;
      GuiCreationScreenInterface.Message = "message.noserver";
      ModelData data = ModelData.get((EntityPlayer)mc.thePlayer);
      Client.sendData(EnumPackets.PING, new Object[] { Integer.valueOf(MorePlayerModels.Version), data.writeToNBT() });
      this.prevWorld = (World)mc.theWorld;
      ClientProxy.fixModels(false);
    }
    RenderEvent.lastSkinTick++;
    if (mc.theWorld.func_72912_H().func_82573_f() % 20L == 0L) {
      playerList = mc.theWorld.func_175661_b(EntityPlayer.class, playerSelector);
      WebApi.instance.run();
    }
  }

  @SubscribeEvent
  public void onCamera(EntityViewRenderEvent.CameraSetup event) {
    Minecraft mc = Minecraft.getMinecraft();
    Entity entity = event.getEntity();
    if ((entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) || mc.gameSettings.thirdPersonView  != 1)
      return;
    float f = entity.getEyeHeight();
    double partialTicks = event.getRenderPartialTicks();
    double d0 = entity.posX + (entity.posX - entity.posX) * partialTicks;
    double d1 = entity.posY + (entity.posY - entity.posY) * partialTicks + f;
    double d2 = entity.posZ  + (entity.posZ - entity.posZ ) * partialTicks;
    double d3 = (camera.cameraDistance - 4.0F);
    float f1 = entity.rotationYaw;
    float f2 = entity.rotationPitch;
    double d4 = (-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
    double d5 = (MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
    double d6 = -MathHelper.sin(f2 * 0.017453292F) * d3;
    for (int i = 0; i < 8; i++) {
      float f3 = ((i & 0x1) * 2 - 1);
      float f4 = ((i >> 1 & 0x1) * 2 - 1);
      float f5 = ((i >> 2 & 0x1) * 2 - 1);
      f3 *= 0.1F;
      f4 *= 0.1F;
      f5 *= 0.1F;
      RayTraceResult raytraceresult = mc.theWorld.rayTraceBlocks(new Vec3d(d0 + f3, d1 + f4, d2 + f5), new Vec3d(d0 - d4 + f3 + f5, d1 - d6 + f4, d2 - d5 + f5));
      if (raytraceresult != null) {
        double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
        if (d7 < d3)
          d3 = d7;
      }
    }
    GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(0.0F, 0.0F, (float)-d3);
    GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side == Side.SERVER || event.phase == TickEvent.Phase.START)
      return;
    EntityPlayer player = event.player;
    ModelData data = ModelData.get(player);
    EntityLivingBase entity = data.getEntity(player);
    Minecraft mc = Minecraft.getMinecraft();
    if (entity != null) {
      entity.onUpdate();
      MPMEntityUtil.Copy((EntityLivingBase)player, entity);
      return;
    }
    if (!MorePlayerModels.HasServerSide)
      data.eyes.update(player);
    if (data.inLove > 0) {
      data.inLove--;
      if (player.getRNG().nextBoolean()) {
        double d0 = player.getRNG().nextGaussian() * 0.02D;
        double d1 = player.getRNG().nextGaussian() * 0.02D;
        double d2 = player.getRNG().nextGaussian() * 0.02D;
        player.worldObj.spawnParticle(EnumParticleTypes.HEART, player.posX + (player.getRNG().nextFloat() * player.width * 2.0F) - player.width, player.posY + 0.5D + (player.getRNG().nextFloat() * player.height), player.posZ + (player.getRNG().nextFloat() * player.width * 2.0F) - player.width, d0, d1, d2, new int[0]);
      }
    }
    if (data.animation == EnumAnimation.CRY) {
      float f1 = player.rotationYaw * 3.1415927F / 180.0F;
      float dx = -MathHelper.sin(f1);
      float dz = MathHelper.cos(f1);
      for (int i = 0; i < 10.0F; i++) {
        float f2 = (player.getRNG().nextFloat() - 0.5F) * player.width * 0.5F + dx * 0.15F;
        float f3 = (player.getRNG().nextFloat() - 0.5F) * player.width * 0.5F + dz * 0.15F;
        player.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, player.posX + f2, player.posY - data.getBodyY() + 1.100000023841858D - player.getYOffset(), player.posZ + f3, 1.0000000195414814E-25D, 0.0D, 1.0000000195414814E-25D, new int[0]);
      }
    }
    if (data.animation != EnumAnimation.NONE)
      ServerTickHandler.checkAnimation(player, data);
    if (data.animation == EnumAnimation.DEATH) {
      if (player.deathTime == 0)
        player.func_184185_a(SoundEvents.field_187543_bD, 1.0F, 1.0F);
      if (player.deathTime < 19)
        player.deathTime++;
    }
    if (data.prevAnimation != data.animation && data.prevAnimation == EnumAnimation.DEATH &&
      !player.isDead)
      player.deathTime = 0;
    data.prevAnimation = data.animation;
    data.prevPosX = player.posX;
    data.prevPosY = player.posY;
    data.prevPosZ = player.posZ;
    ModelPartData particles = data.getPartData(EnumParts.PARTICLES);
    if (particles != null)
      spawnParticles(player, data, particles);
  }

  private void spawnParticles(EntityPlayer player, ModelData data, ModelPartData particles) {
    if (!MorePlayerModels.EnableParticles)
      return;
    Minecraft minecraft = Minecraft.getMinecraft();
    double height = player.getYOffset() + data.getBodyY();
    Random rand = player.getRNG();
    for (int i = 0; i < 2; i++) {
      EntityEnderFX fx = new EntityEnderFX((AbstractClientPlayer)player, (rand.nextDouble() - 0.5D) * player.width, rand.nextDouble() * player.height - height - 0.25D, (rand.nextDouble() - 0.5D) * player.width, (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D, particles);
      minecraft.field_71452_i.func_78873_a((Particle)fx);
    }
  }
}