package noppes.mpm.client.gui.select;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LegacyV2Adapter;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.ModelData;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.util.MPMEntityUtil;

public class GuiTextureSelection extends GuiNPCInterface implements ICustomScrollListener {
  private String up = "..<" + I18n.translateToLocal("gui.up") + ">..";

  private GuiCustomScroll scrollCategories;

  private GuiCustomScroll scrollQuests;

  public String title = "";

  private String location = "";

  private String selectedDomain;

  public ResourceLocation selectedResource;

  private HashMap<String, List<TextureData>> domains = new HashMap<>();

  private HashMap<String, TextureData> textures = new HashMap<>();

  private ModelData playerdata;

  public GuiTextureSelection(ModelData playerdata) {
    this.playerdata = playerdata;
    this.drawDefaultBackground = false;
    setBackground("menubg.png");
    this.xSize = 366;
    this.ySize = 226;
    SimpleReloadableResourceManager simplemanager = (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
    Map<String, FallbackResourceManager> map = (Map<String, FallbackResourceManager>)ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, simplemanager, 2);
    HashSet<String> set = new HashSet<>();
    for (String name : map.keySet()) {
      FallbackResourceManager manager = map.get(name);
      List<IResourcePack> list1 = (List<IResourcePack>)ObfuscationReflectionHelper.getPrivateValue(FallbackResourceManager.class, manager, 1);
      for (IResourcePack pack : list1) {
        if (pack instanceof LegacyV2Adapter)
          pack = (IResourcePack)ObfuscationReflectionHelper.getPrivateValue(LegacyV2Adapter.class, (LegacyV2Adapter)pack, 0);
        if (pack instanceof AbstractResourcePack) {
          AbstractResourcePack p = (AbstractResourcePack)pack;
          File file = p.resourcePackFile;
          if (file != null)
            set.add(file.getAbsolutePath());
        }
      }
    }
    for (String file : set) {
      File f = new File(file);
      if (f.isDirectory()) {
        checkFolder(new File(f, "assets"), f.getAbsolutePath().length());
        continue;
      }
      progressFile(f);
    }
    for (ModContainer mod : Loader.instance().getModList()) {
      if (mod.getSource().exists())
        progressFile(mod.getSource());
    }
    ResourcePackRepository repos = Minecraft.getMinecraft().getResourcePackRepository();
    repos.updateRepositoryEntriesAll();
    List<ResourcePackRepository.Entry> list = repos.getRepositoryEntries();
    if (repos.	getResourcePackInstance() != null) {
      AbstractResourcePack p = (AbstractResourcePack)repos.	getResourcePackInstance();
      File file = p.resourcePackFile;
      if (file != null)
        progressFile(file);
    }
    for (ResourcePackRepository.Entry entry : list) {
      File file = new File(repos.getDirResourcepacks(), entry.getResourcePackName());
      if (file.exists())
        progressFile(file);
    }
    URL url = DefaultResourcePack.class.getResource("/");
    if (url != null) {
      File f = decodeFile(url.getFile());
      if (f.isDirectory()) {
        checkFolder(new File(f, "assets"), url.getFile().length());
      } else {
        progressFile(f);
      }
    }
    url = CraftingManager.class.getResource("/assets/.mcassetsroot");
    if (url != null) {
      File f = decodeFile(url.getFile());
      if (f.isDirectory()) {
        checkFolder(new File(f, "assets"), url.getFile().length());
      } else {
        progressFile(f);
      }
    }
    String texture = playerdata.url;
    if (texture != null && !texture.isEmpty() && !texture.startsWith("http")) {
      this.selectedResource = new ResourceLocation(texture);
      this.selectedDomain = this.selectedResource.getResourceDomain();
      if (!this.domains.containsKey(this.selectedDomain))
        this.selectedDomain = null;
      int i = this.selectedResource.getResourcePath().lastIndexOf('/');
      this.location = this.selectedResource.getResourcePath().substring(0, i + 1);
    }
  }

  public void initGui() {
    super.initGui();
    if (this.selectedDomain != null) {
      this.title = this.selectedDomain + ":" + this.location;
    } else {
      this.title = "";
    }
    addButton(new GuiNpcButton(2, this.guiLeft + 264, this.guiTop + 170, 90, 20, "gui.done"));
    addButton(new GuiNpcButton(1, this.guiLeft + 264, this.guiTop + 190, 90, 20, "gui.cancel"));
    if (this.scrollCategories == null) {
      this.scrollCategories = new GuiCustomScroll((GuiScreen)this, 0);
      this.scrollCategories.setSize(120, 200);
    }
    if (this.selectedDomain == null) {
      this.scrollCategories.setList(Lists.newArrayList(this.domains.keySet()));
      if (this.selectedDomain != null)
        this.scrollCategories.setSelected(this.selectedDomain);
    } else {
      List<String> list = new ArrayList<>();
      list.add(this.up);
      List<TextureData> data = this.domains.get(this.selectedDomain);
      for (TextureData td : data) {
        if (this.location.isEmpty() || (td.path.startsWith(this.location) && !td.path.equals(this.location))) {
          String path = td.path.substring(this.location.length());
          int i = path.indexOf('/');
          if (i < 0)
            continue;
          path = path.substring(0, i);
          if (!path.isEmpty() && !list.contains(path))
            list.add(path);
        }
      }
      this.scrollCategories.setList(list);
    }
    this.scrollCategories.guiLeft = this.guiLeft + 4;
    this.scrollCategories.guiTop = this.guiTop + 14;
    addScroll(this.scrollCategories);
    if (this.scrollQuests == null) {
      this.scrollQuests = new GuiCustomScroll((GuiScreen)this, 1);
      this.scrollQuests.setSize(130, 200);
    }
    if (this.selectedDomain != null) {
      this.textures.clear();
      List<TextureData> data = this.domains.get(this.selectedDomain);
      List<String> list = new ArrayList<>();
      String loc = this.location;
      if (this.scrollCategories.hasSelected() && !this.scrollCategories.getSelected().equals(this.up))
        loc = loc + this.scrollCategories.getSelected() + '/';
      for (TextureData td : data) {
        if (td.path.equals(loc) && !list.contains(td.name)) {
          list.add(td.name);
          this.textures.put(td.name, td);
        }
      }
      this.scrollQuests.setList(list);
    }
    if (this.selectedResource != null)
      this.scrollQuests.setSelected(this.selectedResource.getResourcePath());
    this.scrollQuests.guiLeft = this.guiLeft + 125;
    this.scrollQuests.guiTop = this.guiTop + 14;
    addScroll(this.scrollQuests);
  }

  protected void actionPerformed(GuiButton guibutton) {
    super.actionPerformed(guibutton);
    if (guibutton.id == 2) {
      this.playerdata.url = this.selectedResource.toString();
      this.playerdata.resourceInit = false;
      this.playerdata.resourceLoaded = false;
    }
    close();
    this.parent.initGui();
  }

  public void drawScreen(int i, int j, float f) {
    EntityPlayerSP entityPlayerSP = null;
    drawDefaultBackground();
    super.drawScreen(i, j, f);
    drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
    EntityLivingBase entity = this.playerdata.getEntity((EntityPlayer)this.mc.thePlayer);
    if (entity == null) {
      entityPlayerSP = this.player;
    } else {
      MPMEntityUtil.Copy((EntityLivingBase)this.mc.thePlayer, (EntityLivingBase)this.player);
    }
    drawNpc((EntityLivingBase)entityPlayerSP, this.guiLeft + 276, this.guiTop + 140, 1.0F, 0);
  }

  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    if (scroll == this.scrollQuests) {
      if (scroll.id == 1) {
        TextureData data = this.textures.get(scroll.getSelected());
        this.selectedResource = new ResourceLocation(this.selectedDomain, data.absoluteName);
        this.playerdata.url = this.selectedResource.toString();
        this.playerdata.resourceInit = false;
        this.playerdata.resourceLoaded = false;
      }
    } else {
      initGui();
    }
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    if (scroll == this.scrollCategories) {
      if (this.selectedDomain == null) {
        this.selectedDomain = selection;
      } else if (selection.equals(this.up)) {
        int i = this.location.lastIndexOf('/', this.location.length() - 2);
        if (i < 0) {
          if (this.location.isEmpty())
            this.selectedDomain = null;
          this.location = "";
        } else {
          this.location = this.location.substring(0, i + 1);
        }
      } else {
        this.location += selection + '/';
      }
      this.scrollCategories.selected = -1;
      this.scrollQuests.selected = -1;
      initGui();
    } else {
      close();
      this.parent.initGui();
    }
  }

  private void progressFile(File file) {
    try {
      if (!file.isDirectory() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
          ZipEntry zipentry = entries.nextElement();
          String entryName = zipentry.getName();
          addFile(entryName);
        }
        zip.close();
      } else if (file.isDirectory()) {
        int length = file.getAbsolutePath().length();
        checkFolder(file, length);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void checkFolder(File file, int length) {
    File[] files = file.listFiles();
    if (files == null)
      return;
    for (File f : files) {
      String name = f.getAbsolutePath().substring(length);
      name = name.replace("\\", "/");
      if (!name.startsWith("/"))
        name = "/" + name;
      if (f.isDirectory()) {
        addFile(name + "/");
        checkFolder(f, length);
      } else {
        addFile(name);
      }
    }
  }

  private void addFile(String name) {
    name = name.toLowerCase();
    if (name.startsWith("/"))
      name = name.substring(1);
    if (!name.startsWith("assets/") || !name.toLowerCase().endsWith(".png"))
      return;
    name = name.substring(7);
    int i = name.indexOf('/');
    String domain = name.substring(0, i);
    name = name.substring(i + 10);
    List<TextureData> list = this.domains.get(domain);
    if (list == null)
      this.domains.put(domain, list = new ArrayList<>());
    boolean contains = false;
    for (TextureData data : list) {
      if (data.absoluteName.equals(name)) {
        contains = true;
        break;
      }
    }
    if (!contains)
      list.add(new TextureData(domain, name));
  }

  private File decodeFile(String url) {
    if (url.startsWith("file:"))
      url = url.substring(5);
    url = url.replace('/', File.separatorChar);
    int i = url.indexOf('!');
    if (i > 0)
      url = url.substring(0, i);
    try {
      url = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    return new File(url);
  }

  class TextureData {
    String domain;

    String absoluteName;

    String name;

    String path;

    public TextureData(String domain, String absoluteName) {
      this.domain = domain;
      int i = absoluteName.lastIndexOf('/');
      this.name = absoluteName.substring(i + 1);
      this.path = absoluteName.substring(0, i + 1);
      this.absoluteName = "textures/" + absoluteName;
    }
  }

  public void save() {}

@Override
public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
	// TODO Auto-generated method stub

}
}