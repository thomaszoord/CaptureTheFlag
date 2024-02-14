package br.com.thomaszoord.commands;

import br.com.thomaszoord.partidas.Partida;
import br.com.thomaszoord.partidas.Arenas;
import br.com.thomaszoord.partidas.PartidaManager;
import br.com.thomaszoord.partidas.enums.Status;
import br.com.thomaszoord.CaptureTheFlag;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class CTFCommands implements CommandExecutor {

    
    private final String prefix = CaptureTheFlag.prefix;
    private final String minigames = CaptureTheFlag.minigame;
    private final CaptureTheFlag plugin = CaptureTheFlag.plugin;
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (command.getName().equalsIgnoreCase(prefix)) {
            if (args.length == 0) {
                displayCommandList(p);
                return true;
            }

            String arg = args[0];

            if (arg.equalsIgnoreCase("help")) {
                displayCommandList(p);
            } else if (arg.equalsIgnoreCase("iniciar")) {
                handleIniciarCommand(p);
            } else if (arg.equalsIgnoreCase("info")) {
                handleInfoCommand(p);
            } else if (arg.equalsIgnoreCase("sair")) {
                handleSairCommand(p);
            } else if (arg.equalsIgnoreCase("entrar")) {
                handleEntrarCommand(p);
            } else if (arg.equalsIgnoreCase("arena")) {
                handleArenaCommand(p, args);
            } else if(arg.equalsIgnoreCase("partidas")){
                p.sendMessage("");
                p.sendMessage("§eLista de Partidas §7» §f" + minigames);
                p.sendMessage("");
                p.sendMessage("§7» §fClique para teletransportar-se!");
                p.sendMessage("");
                for (World arena : PartidaManager.partidas.keySet()) {
                    TextComponent arenaText = new TextComponent("§e» §f" + arena.getName() + " §eStatus: §f" + PartidaManager.partidas.get(arena).status + " §eJogadores: §f" + PartidaManager.partidas.get(arena).jogadores.size() + "/" + PartidaManager.partidas.get(arena).maxPlayers);
                    arenaText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + prefix + " arena goto " + arena.getName()));
                    arenaText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClique para se teletransportar \n§epara a Arena §7» §f" + arena.getName()).create()));

                    p.spigot().sendMessage(arenaText);
                }
                p.sendMessage("");
            }

            return true;
        }

        return false;
    }

    private void displayCommandList(Player p) {
        p.sendMessage("");
        p.sendMessage("§eLista de comandos §7» §fCapture the Flag");
        p.sendMessage("");
        p.sendMessage("§e/" + prefix + " §farena §ehelp §7» §7Lista de comandos da Arena");
        p.sendMessage("§e/" + prefix + " §finiciar §7» §7Inicia a partida");
        p.sendMessage("§e/" + prefix + " §fentrar §7» §7Entra na partida daquele mundo.");
        p.sendMessage("§e/" + prefix + " §fsair §7» §7Sai da partida");
        p.sendMessage("§e/" + prefix + " §finfo §7» §7Informações da partida atual");
        p.sendMessage("§e/" + prefix + " §fpartidas §7» §7Lista de partidas disponíveis");
        p.sendMessage("");
    }

    private void handleIniciarCommand(Player p) {
        Partida part = PartidaManager.getPartidaPlayer(p);

        if (part == null) {
            p.sendMessage("§cVocê precisa entrar em uma partida para inicia-la!");
            return;
        }

        if (part.status == Status.ESPERA) {
            part.iniciarPartida();
        } else if(part.status == Status.INICIANDO) {
            if(part.contador > part.forceStart){
                p.sendMessage("§aForçando inicio! Contador alterado para " + part.forceStart + " segundos!");
                part.contador = part.forceStart;
            }

        } else {
            p.sendMessage("§cA partida já está iniciada!");

        }
    }

    private void handleInfoCommand(Player p) {
        Partida part = PartidaManager.getPartidaPlayer(p);

        if (part == null) {
          if(PartidaManager.getPartida(p.getWorld().getName()).espectadores.contains(p)){
              part = PartidaManager.getPartida(p.getWorld().getName());
              p.sendMessage("");
              p.sendMessage("§eInformações da partida (Espectador) §7» §fCapture the Flag");
              p.sendMessage("");
          } else {
              p.sendMessage("§cVocê precisa entrar em uma partida para pegar suas informações!");
              return;
          }
        } else {
            p.sendMessage("");
            p.sendMessage("§eInformações da partida §7» §fCapture the Flag");
            p.sendMessage("");
        }


        p.sendMessage("§eNome §7»§f " + part.nome);
        p.sendMessage("§eStatus §7»§f " + part.status);

        StringBuilder players = new StringBuilder();
        StringBuilder espectadores = new StringBuilder();


        for (Player p1 : part.jogadores) {
            players.append(p1.getName()).append("§f, ");
        }

        for (Player pall : part.espectadores) {
            espectadores.append(pall.getName()).append("§8, ");
        }

        p.sendMessage("§ePlayers §7» §f" + players);
        p.sendMessage("§eEspectadores: §8" + espectadores);

        StringBuilder timeAzul = new StringBuilder();
        for (Player bPlayers : part.timeAzul.players) {
            timeAzul.append(bPlayers.getName()).append(", ");
        }

        StringBuilder timeVermelho = new StringBuilder();
        for (Player rPlayers : part.timeVermelho.players) {
            timeVermelho.append(rPlayers.getName()).append(", ");
        }

        p.sendMessage("§9Time Azul: " + timeAzul);
        p.sendMessage("§cTime Vermelho" + timeVermelho);

        p.sendMessage(" ");
    }

    private void handleSairCommand(Player p) {
        Partida part = PartidaManager.getPartidaPlayer(p);

        if (part != null) {
            p.sendMessage("§cVocê saiu da partida " + part.nome + "!");
            PartidaManager.sairPartidaPlayer(p);
        } else {
            p.sendMessage("§cVocê não está em nenhuma partida.");
        }
    }

    private void handleEntrarCommand(Player p) {
        PartidaManager.setPartidaPlayer(p, p.getWorld());
    }

    private void handleArenaCommand(Player p, String[] args) {
        if (args.length < 2) {
            p.sendMessage("§cUso correto: /" + prefix + "  arena help");
            return;
        }

        String subCommand = args[1];

        if (subCommand.equalsIgnoreCase("help")) {
            arenaHelp(p);
        } else if (subCommand.equalsIgnoreCase("criar")) {
            handleCriarArenaCommand(p);
        } else if (subCommand.equalsIgnoreCase("goto")) {
            handleGotoArenaCommand(p, args);
        } else if (subCommand.equalsIgnoreCase("setspawn")) {
            handleSetSpawnCommand(p, args);
        } else if(subCommand.equalsIgnoreCase("listar")){
            p.sendMessage("");
            p.sendMessage("§eLista de Arenas §7» §fCapture the Flag");
            p.sendMessage("");
            p.sendMessage("§7» §fClique para teletransportar-se!");
            p.sendMessage("");
            for (World arena : Arenas.arenas) {
                TextComponent arenaText = new TextComponent("§e» §f" + arena.getName());
                arenaText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + prefix + "  arena goto " + arena.getName()));
                arenaText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClique para se teletransportar \n§epara a Arena §7» §f" + arena.getName()).create()));

                p.spigot().sendMessage(arenaText);
            }
            p.sendMessage("");
        }
    }

    private void arenaHelp(Player p) {
        p.sendMessage("");
        p.sendMessage("§eLista de comandos de Arena §7» §fCapture the Flag");
        p.sendMessage("");
        p.sendMessage("§e/" + prefix + " farena §ehelp §7» §7Lista de comandos da Arena");
        p.sendMessage("§e/" + prefix + " §farena §ecriar §7» §7Cria uma arena");
        p.sendMessage("§e/" + prefix + " §farena §elistar §7» §7Lista todas as arenas");
        p.sendMessage("§e/" + prefix + " §farena §esetspawn §b[Azul/Vermelho/Espera] §7» §7Seta spawn na arena");
        p.sendMessage("");
    }

    private void handleCriarArenaCommand(Player p) {
        String nomeArena = p.getWorld().getName();

        if (Arenas.arenas.contains(p.getWorld())) {
            p.sendMessage("§cA arena §f" + nomeArena + "§cjá foi criada!");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(
               plugin.getDataFolder(), "arenas/" +
                p.getWorld().getName() + ".yml"));

        config.set("Mundo", p.getWorld().getName());

        try {
            p.sendMessage("§aSpawn do "+ minigames + " definido com sucesso!");
            config.save(new File(CaptureTheFlag.plugin.getDataFolder(), "arenas/" + p.getWorld().getName() + ".yml"));
        } catch (IOException e) {
            p.sendMessage("§cErro no salvamento da localização.");
            return;
        }

        Arenas.arenas.add(p.getWorld());

        p.sendMessage("§aA arena §f " + nomeArena + " §afoi criada com sucesso!");
    }

    private void handleGotoArenaCommand(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage("§cUso correto: /" + prefix + "  arena goto <arena>");
            return;
        }

        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part != null){
            part.sairDaPartida(p);
        }

        String arenaName = args[2];

        p.sendMessage(arenaName);
        World world = Bukkit.getWorld(arenaName);
        p.sendMessage("" + world);

        if (world != null && Arenas.arenas.contains(world)) {
            p.teleport(world.getSpawnLocation());

            if (!p.getAllowFlight()) {
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setGameMode(GameMode.SPECTATOR);
                p.setGameMode(GameMode.SURVIVAL);
            }

            p.sendMessage("§aVocê foi teleportado para a arena " + world.getName() + " com sucesso!");
        } else if(world != null && PartidaManager.partidas.containsKey(world)){
            Partida partidaAnterior = PartidaManager.getPartida(p.getWorld().getName());
            if(partidaAnterior.espectadores.contains(p)){
                partidaAnterior.sairDaPartida(p);
            }
            PartidaManager.getPartida(world.getName()).entrarEspectador(p);

            p.sendMessage("§aVocê foi teleportado para o mundo da partida " + world.getName() + " com sucesso!");
        } else if(world != null && !PartidaManager.partidas.containsKey(world) && Arenas.arenas.contains(world)) {
            p.teleport(world.getSpawnLocation());
            p.teleport(world.getSpawnLocation());



            if (!p.getAllowFlight()) {
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setGameMode(GameMode.SPECTATOR);
                p.setGameMode(GameMode.SURVIVAL);
            }

            p.sendMessage("§aVocê foi teleportado para o mundo " + world.getName() + " com sucesso!");
          p.sendMessage("");
            p.sendMessage("§cEsse mundo não possui uma arena/partida");
            p.sendMessage("§c para registra-lo use /" + prefix + "  arena criar");
            p.sendMessage("");

        } else {
            p.sendMessage("§cO mundo não existe.");
        }
    }


    private void handleSetSpawnCommand(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage("§cUso correto: /" + prefix + "  arena setspawn <azul/vermelho/espera>");
            return;
        }

        String team = args[2].toLowerCase();

        if(team.equalsIgnoreCase("espera")){
            String locationKey = "Spawn";

            if (!team.equals("espera")) {
                locationKey = "Time " + Character.toUpperCase(team.charAt(0)) + team.substring(1) + ".Spawn";
            }

            setLocationArena(locationKey, p);
        } else if (team.equals("azul") || team.equals("vermelho")) {
            if(args.length < 4){
                p.sendMessage("§cUso correto: /" + prefix + "  arena setspawn " + team + " [spawn/wool/kit/time]");
                return;
            }

            String spawn = args[3];

            if(spawn.equalsIgnoreCase("spawn")){
                setLocationArena("Time " + team + ".Spawn", p);
            } else if(spawn.equalsIgnoreCase("wool")){
                setLocationArena("Time " + team +".Wool", p);
            } else if(spawn.equalsIgnoreCase("kit")){
                setLocationArena("Time " + team +".NPCEscolherKit", p);

            } else if(spawn.equalsIgnoreCase("time")){
                setLocationArena("Time " + team +".NPCEscolherTime", p);
            } else {
                p.sendMessage("§cUso correto: /" + prefix + "  arena setspawn " + team + " [spawn/wool/kit/time]");
            }
        } else {
            p.sendMessage("§cUso correto: /" + prefix + "  arena setspawn <azul/vermelho/espera>");
        }
    }


    public void setLocationArena(String localizacao, Player p) {

        World world = p.getWorld();

        if(!Arenas.arenas.contains(world)) {
            p.sendMessage("§cVocê não está em um mundo de Arena");
            p.sendMessage("§cUse /" + prefix + "  arena goto <nomeDaArena>");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(
               plugin.getDataFolder(), "arenas/" +
                world.getName() + ".yml"));


        config.set(localizacao, p.getLocation().serialize());

        try {
            p.sendMessage("§aSpawn §f" + localizacao + " §ada arena §f" + world.getName() + " §adefinido com sucesso!");
            config.save(new File(CaptureTheFlag.plugin.getDataFolder(), "arenas/" + p.getWorld().getName() + ".yml"));
        } catch (IOException e) {
            p.sendMessage("§cErro no salvamento da localização.");
        }

    }





}
