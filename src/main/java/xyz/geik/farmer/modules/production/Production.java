package xyz.geik.farmer.modules.production;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.model.inventory.FarmerItem;
import xyz.geik.farmer.modules.FarmerModule;
import xyz.geik.farmer.modules.production.configuration.ConfigFile;
import xyz.geik.farmer.modules.production.handlers.ProductionCalculateEvent;
import xyz.geik.farmer.shades.storage.Config;
import xyz.geik.glib.GLib;
import xyz.geik.glib.chat.ChatUtils;
import xyz.geik.glib.shades.okaeri.configs.ConfigManager;
import xyz.geik.glib.shades.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Production module main class
 */
@Getter
public class Production extends FarmerModule {

    /**
     * Constructor of class
     */
    public Production() {}

    @Getter
    private static Production instance;

    private Config langFile;

    private static ProductionCalculateEvent productionCalculateEvent;

    private String[] numberFormat = new String[]{"k", "m", "b", "t"};

    private long reCalculate = 15L;

    private List<String> productionItems = new ArrayList<>();

    private ConfigFile configFile;

    /**
     * onEnable method of module
     */
    @Override
    public void onEnable() {
        instance = this;
        this.setEnabled(true);
        setupFile();

        if (configFile.isStatus()) {
            getProductionItems().addAll(configFile.getItems());
            productionCalculateEvent = new ProductionCalculateEvent();
            Bukkit.getPluginManager().registerEvents(productionCalculateEvent, Main.getInstance());
            setLang(Main.getConfigFile().getSettings().getLang(), Main.getInstance());
            numberFormat[0] = getLang().getText("numberFormat.thousand");
            numberFormat[1] = getLang().getText("numberFormat.million");
            numberFormat[2] = getLang().getText("numberFormat.billion");
            numberFormat[3] = getLang().getText("numberFormat.trillion");
            reCalculate = configFile.getReCalculate();
            String messagex = "&3[" + GLib.getInstance().getName() + "] &a" + getName() + " enabled.";
            ChatUtils.sendMessage(Bukkit.getConsoleSender(), messagex);
        }
        else {
            String messagex = "&3[" + GLib.getInstance().getName() + "] &c" + getName() + " is not loaded.";
            ChatUtils.sendMessage(Bukkit.getConsoleSender(), messagex);
        }
    }

    /**
     * onReload method of module
     */
    @Override
    public void onReload() {
        if (!this.isEnabled())
            return;
        numberFormat[0] = getLang().getText("numberFormat.thousand");
        numberFormat[1] = getLang().getText("numberFormat.million");
        numberFormat[2] = getLang().getText("numberFormat.billion");
        numberFormat[3] = getLang().getText("numberFormat.trillion");
        reCalculate = configFile.getReCalculate();
    }

    /**
     * onDisable method of module
     */
    @Override
    public void onDisable() {
        HandlerList.unregisterAll(productionCalculateEvent);
    }

    /**
     * is item suitable to calculate
     *
     * @param item item of farmer
     * @return boolean
     */
    public static boolean isCalculateItem(@NotNull FarmerItem item) {
        return instance.getProductionItems().contains(item.getName())
                || instance.getProductionItems().isEmpty();
    }

    public void setupFile() {
        configFile = ConfigManager.create(ConfigFile.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(new File(Main.getInstance().getDataFolder(), String.format("/modules/%s/config.yml", getName())));
            it.saveDefaults();
            it.load(true);
        });
    }

}
