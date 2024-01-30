package br.com.thomaszoord.APIs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class F_Config {

    public F_Config(JavaPlugin plugin, String nome) {
        this.plugin = plugin;
        setName(nome);
        reloadConfig();
    }

    private JavaPlugin plugin;
    private String name;
    private File file;

    public JavaPlugin getPlugin() {
        return plugin;
    }


    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    private YamlConfiguration config;

    public void saveConfig() {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); // Cria a pasta se não existir
            }
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(file.getAbsolutePath());
    }

    public void saveDefault() {
        getConfig().options().copyDefaults(true);
    }

    public void saveDefaultConfig() {
        getPlugin().saveResource(getName(), false);
    }

    public void reloadConfig() {
        file = new File(getPlugin().getDataFolder(), getName());
        config = YamlConfiguration.loadConfiguration(getFile());

    }

    public void deleteConfig() {
        getFile().delete();
    }

    public boolean existeConfig() {
        return getFile().exists();
    }

    public String getString(String path) {
        return getConfig().getString(path);
    }

    public int getInt(String path) {
        return getConfig().getInt(path);
    }

    public boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public double getDouble(String path) {
        return getConfig().getDouble(path);
    }

    public Vector getVector(String path) {
        return getConfig().getVector(path);
    }

    public List<Map<String, Object>> getMapList(String path) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        ConfigurationSection section = getConfig().getConfigurationSection(path);

        if (section != null) {
            for (String key : section.getKeys(false)) {
                Map<String, Object> map = new HashMap<>();
                ConfigurationSection subSection = section.getConfigurationSection(key);

                if (subSection != null) {
                    for (String subKey : subSection.getKeys(false)) {
                        map.put(subKey, subSection.get(subKey));
                    }
                }

                mapList.add(map);
            }
        }

        return mapList;
    }

    public List<?> getList(String path) {
        return getConfig().getList(path);
    }

    public List<String> getStringList(String path) {
        return getConfig().getStringList(path);
    }

    public boolean contains(String path) {
        return getConfig().contains(path);
    }

    public void set(String path, Object value) {
        getConfig().set(path, value);
    }


    public void setLocationPrimary(String nomePartida, String localizacao) {

        Config.config.set("bw." + nomePartida + "." + localizacao + ".world", " ");
        Config.config.set("bw." + nomePartida + "." + localizacao + ".x", 0);
        Config.config.set("bw." + nomePartida + "." + localizacao + ".y", 0);
        Config.config.set("bw." + nomePartida + "." + localizacao + "z", 0);
        Config.config.set("bw." + nomePartida + "." + localizacao + ".yaw", 0);
        Config.config.set("bw." + nomePartida + "." + localizacao + ".pitch", 0);

        Config.config.saveConfig();
    }

    public void setLocationPrimary(String nomePartida, String localizacao, Location l) {

        Config.config.set("bw." + nomePartida + "." + localizacao + ".world", l.getWorld().getName());
        Config.config.set("bw." + nomePartida + "." + localizacao + ".x", l.getX());
        Config.config.set("bw." + nomePartida + "." + localizacao + ".y", l.getY());
        Config.config.set("bw." + nomePartida + "." + localizacao + "z", l.getZ());
        Config.config.set("bw." + nomePartida + "." + localizacao + ".yaw", l.getYaw());
        Config.config.set("bw." + nomePartida + "." + localizacao + ".pitch", l.getPitch());

        Config.config.saveConfig();
    }

}
