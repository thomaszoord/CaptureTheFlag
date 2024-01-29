package br.com.thomaszoord.sccapturetheflag.Partidas;

import br.com.thomaszoord.sccapturetheflag.APIs.Config;
import br.com.thomaszoord.sccapturetheflag.Partidas.enums.Status;
import br.com.thomaszoord.sccapturetheflag.SCCaptureTheFlag;
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

        part.entrarNaPartida(p);
        p.sendMessage("§aVocê entrou na partida " + part.nome);


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
            if(part.status == Status.ESPERA || part.status == Status.INICIANDO){
                if (partidaComMaisJogadores == null || part.jogadores.size() > partidaComMaisJogadores.jogadores.size()) {
                    if(part.jogadores.size() < part.maxPlayers){
                        partidaComMaisJogadores = part;

                    }
                }
            }
//            if (!part.status.equals(Status.FINALIZANDO) || !part.status.equals(Status.EMJOGO)) {
//
//            }
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


        File arenaConfigFile = new File(SCCaptureTheFlag.plugin.getDataFolder(), "arenas/" + arena.getName() + ".yml");

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

        partida.spawn =  SCCaptureTheFlag.getLocationConfig(arena, "Spawn", partida.mundo);

        partida.timeAzul.spawn = SCCaptureTheFlag.getLocationConfig(arena, "Time azul.Spawn", partida.mundo);
        partida.timeAzul.npcTime = SCCaptureTheFlag.getLocationConfig(arena, "Time azul.NPCEscolherTime", partida.mundo);
        partida.timeAzul.npcKit = SCCaptureTheFlag.getLocationConfig(arena, "Time azul.NPCEscolherKit", partida.mundo);
        partida.timeAzul.wool =  SCCaptureTheFlag.getLocationConfig(arena, "Time azul.Wool", partida.mundo);;

        partida.timeVermelho.spawn = SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.Spawn", partida.mundo);
        partida.timeVermelho.npcTime = SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.NPCEscolherTime", partida.mundo);
        partida.timeVermelho.npcKit = SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.NPCEscolherKit", partida.mundo);
        partida.timeVermelho.wool =  SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.Wool", partida.mundo);


        PartidaManager.partidas.put(partida.mundo, partida);
        Bukkit.getConsoleSender().sendMessage("§aPartida §f" + nome + " §acadastrada com sucesso na arena " + arena.getName());

    }
}
