package br.com.thomaszoord.Partidas;

import br.com.thomaszoord.CaptureTheFlag;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class Arenas {

    public static ArrayList<World> arenas = new ArrayList<>();

    public static void carregarArenasDoDiretorio() {
        File arenasFolder = new File(CaptureTheFlag.plugin.getDataFolder(), "arenas");

        if (!arenasFolder.exists()) {
            return;
        }


        File[] eventoFiles = arenasFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (eventoFiles == null) {
            return;
        }

        for (File configFile : eventoFiles) {

            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            World mundo = Bukkit.getWorld(config.getString("Mundo"));

            if(mundo != null) {
                Bukkit.getConsoleSender().sendMessage("§aA arena " + mundo.getName() + " foi encontrada com sucesso!");
                arenas.add(mundo);
            } else {
                Bukkit.getConsoleSender().sendMessage("§cO arena " + mundo.getName() + " não conseguiu carregar.");
            }

        }
    }


}
