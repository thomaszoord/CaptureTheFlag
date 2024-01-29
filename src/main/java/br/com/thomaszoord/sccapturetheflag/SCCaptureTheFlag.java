package br.com.thomaszoord.sccapturetheflag;

import br.com.thomaszoord.sccapturetheflag.APIs.Config;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.EventosInventario;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.KitsManager;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.PvPEvents;
import br.com.thomaszoord.sccapturetheflag.Commands.CTFCommands;
import br.com.thomaszoord.sccapturetheflag.Eventos.Espectador;
import br.com.thomaszoord.sccapturetheflag.Eventos.EventosUtils;
import br.com.thomaszoord.sccapturetheflag.Eventos.JoinQuitEvent;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.PontuacaoEvento;
import br.com.thomaszoord.sccapturetheflag.Partidas.Arenas;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SCCaptureTheFlag extends JavaPlugin {

    public PartidaManager partidasManager;
    public Arenas arenasManager;

    public static SCCaptureTheFlag plugin;

    public static KitsManager kitsManager;


    public static String minigame = "CaptureTheFlag";
    public static String prefix = "ctf";

    @Override
    public void onEnable() {

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§6SC_" + minigame);
        Bukkit.getConsoleSender().sendMessage("§aPlugin habilitado.");
        Bukkit.getConsoleSender().sendMessage("");


        plugin = this;
        arenasManager = new Arenas();
        Arenas.carregarArenasDoDiretorio();
        partidasManager = new PartidaManager();
        kitsManager = new KitsManager();


        if (!Config.config.getConfig().getKeys(true).contains("Partidas")) {

            List<String> lista = new ArrayList<>();

            lista.add("Oriental1");
            Config.config.set("Partidas", lista);
            Config.config.saveConfig();

        }

        // CADASTRO DE PARTIDAS COLOCADAS NA CONFIG
        List<Map<String, Object>> partidasList = Config.config.getMapList("Partidas");

        for (Map<?, ?> partidaMap : partidasList) {
            String nomePartida = (String) partidaMap.get("Nome");
            String nomeArena = (String) partidaMap.get("Arena");

            PartidaManager.cadastrarPartida(nomePartida);
        }

        registrarComandos();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§6SC_" + minigame);
        Bukkit.getConsoleSender().sendMessage("§cPlugin desabilitado.");
        Bukkit.getConsoleSender().sendMessage("");



    }


    public static Location getLocationConfig(World arena, String local, World partida) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(
                plugin.getDataFolder(), "arenas/" +
                arena.getName() + ".yml"));

        if (config.getConfigurationSection(local) == null) {
            Bukkit.getConsoleSender().sendMessage("§cA localização §f" + local + " §cnão foi definida na arena original!");
            return null;
        } else {
            ConfigurationSection locationSection = config.getConfigurationSection(local);

            double x = locationSection.getDouble("x");
            double y = locationSection.getDouble("y");
            double z = locationSection.getDouble("z");
            float pitch = locationSection.getInt("pitch");
            float yaw = locationSection.getInt("yaw");

            return new Location(partida, x, y, z, (float) yaw, (float) pitch);
        }
    }



    public void registrarComandos(){
        CTFCommands comando = new CTFCommands();
        EventosInventario eventosInventario = new EventosInventario();
        getCommand(prefix).setExecutor(comando);

        getCommand("time").setExecutor(eventosInventario);
        getCommand("kit").setExecutor(eventosInventario);


        Bukkit.getPluginManager().registerEvents(new JoinQuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PontuacaoEvento(), this);
        Bukkit.getPluginManager().registerEvents(new EventosUtils(), this);
        Bukkit.getPluginManager().registerEvents(new Espectador(), this);
        Bukkit.getPluginManager().registerEvents(new PvPEvents(), this);
        Bukkit.getPluginManager().registerEvents(eventosInventario, this);

    }
}
