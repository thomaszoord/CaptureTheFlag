package br.com.thomaszoord.Partidas;

import br.com.thomaszoord.APIs.Config;
import br.com.thomaszoord.CaptureTheFlag;
import br.com.thomaszoord.Partidas.enums.Status;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import scverso.VersoManager;

import java.io.File;
import java.util.HashMap;
public class PartidaManager {


    public static HashMap<World, Partida> partidas = new HashMap<>();
    public static void setPartidaPlayer(Player p, World world){
        if(getPartidaPlayer(p) != null){
            sairPartidaPlayer(p);
        }

        boolean partidaEncontrada = false;

        for(Partida part : partidas.values()){
            if(part.mundo == world){
                if(part.status == Status.FINALIZANDO){
                    p.sendMessage("§cA partida está finalizando! Você será enviado para outra partida..");
                    encontrarPartida().entrarNaPartida(p);
                }
                part.entrarNaPartida(p);
                p.sendMessage("§aVocê entrou na partida " + part.nome);
                partidaEncontrada = true;
                return;
            }
        }

        if(!partidaEncontrada) {
            p.kickPlayer("§cPartida não encontrada!");
        }

    }
    public static void setPartidaPlayer(Player p, Partida part){
        if(getPartidaPlayer(p) != null){
            sairPartidaPlayer(p);
        }

        if(part.status == Status.FINALIZANDO){
            p.sendMessage("§cA partida está finalizando! Você será enviado para outra partida..");
            p.sendMessage("§aVocê entrou na partida " + part.nome);
            encontrarPartida().entrarNaPartida(p);
            return;
        }

        p.sendMessage("§aVocê entrou na partida " + part.nome);
        part.entrarNaPartida(p);



    }
    public static Partida getPartidaPlayer(Player p){
        if(partidas.containsKey(p.getWorld()) &&
                partidas.get(p.getWorld()).jogadores.contains(p)){
            return partidas.get(p.getWorld());
        }

        return null;
    }

    public static void sairPartidaPlayer(Player p) {
        Partida partida = getPartidaPlayer(p);

        if(partida != null){
            partida.sairDaPartida(p);
        }
    }
    public static Partida encontrarPartida() {
        if (partidas.isEmpty()) {
            return null;
        }

        Partida partidaComMaisJogadores = null;

        for (Partida part : partidas.values()) {
            if (!part.status.equals(Status.FINALIZANDO)) {
                if (partidaComMaisJogadores == null || part.jogadores.size() > partidaComMaisJogadores.jogadores.size()) {
                    if(part.jogadores.size() < part.maxPlayers){
                        partidaComMaisJogadores = part;
                    }
                }
            }
        }

        return partidaComMaisJogadores;
    }

    public static Partida getPartida(String nomePartida) {
        Partida partida = null;

        // LOGICA PARA COLOCAR PLAYER NA PARTIDA
        for (Partida part : partidas.values()) {

            if (part.nome.equalsIgnoreCase(nomePartida)) {

                partida = part;

                break;
            }

        }

        return partida;
    }
    public static void cadastrarPartida(String nome){
        Partida partida = new Partida(nome, false);

        partida.nome = Config.config.getString("Partidas." + nome + ".Nome");
        World arena = Bukkit.getWorld(Config.config.getString("Partidas." + nome + ".Arena"));

        // INSTANCIA CONFIG DA ARENA
        if(arena == null){
            Bukkit.getConsoleSender().sendMessage("§cA arena " + Config.config.getString("Partidas." + nome + ".Arena") + " não existe.");
            return;
        }


        File arenaConfigFile = new File(CaptureTheFlag.plugin.getDataFolder(), "arenas/" + arena.getName() + ".yml");

            if (!arenaConfigFile.exists()) {
                Bukkit.getLogger().warning("Não foi possível encontrar o arquivo de configuração para a arena: " + arena.getName());
                return;
       }



        //COPIA MUNDO
        VersoManager.getManager().copyVerso(arena.getName(), partida.nome, false);
        partida.mundo = VersoManager.getManager().getWorld(partida.nome);
        partida.arena = arena;

        partida.mundo.setGameRuleValue("doDaylightCycle", "false");
        partida.mundo.setGameRuleValue("doMobSpawning", "false");
        partida.mundo.setGameRuleValue("showDeathMessages", "false");
        partida.mundo.setGameRuleValue("doFireTick", "false");

        partida.spawn =  CaptureTheFlag.getLocationConfig(arena, "Spawn", partida.mundo);

        partida.timeAzul.spawn = CaptureTheFlag.getLocationConfig(arena, "Time azul.Spawn", partida.mundo);
        partida.timeAzul.npcTime = CaptureTheFlag.getLocationConfig(arena, "Time azul.NPCEscolherTime", partida.mundo);
        partida.timeAzul.npcKit = CaptureTheFlag.getLocationConfig(arena, "Time azul.NPCEscolherKit", partida.mundo);
        partida.timeAzul.wool =  CaptureTheFlag.getLocationConfig(arena, "Time azul.Wool", partida.mundo);;

        partida.timeVermelho.spawn = CaptureTheFlag.getLocationConfig(arena, "Time vermelho.Spawn", partida.mundo);
        partida.timeVermelho.npcTime = CaptureTheFlag.getLocationConfig(arena, "Time vermelho.NPCEscolherTime", partida.mundo);
        partida.timeVermelho.npcKit = CaptureTheFlag.getLocationConfig(arena, "Time vermelho.NPCEscolherKit", partida.mundo);
        partida.timeVermelho.wool =  CaptureTheFlag.getLocationConfig(arena, "Time vermelho.Wool", partida.mundo);


        PartidaManager.partidas.put(partida.mundo, partida);
        Bukkit.getConsoleSender().sendMessage("§aPartida §f" + nome + " §acadastrada com sucesso na arena " + arena.getName());

    }
}
