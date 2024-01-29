package br.com.thomaszoord.sccapturetheflag.Partidas;

import br.com.thomaszoord.sccapturetheflag.APIs.SoundAPI;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.EquiparJogadores;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.enums.Kit;
import br.com.thomaszoord.sccapturetheflag.Eventos.Espectador;
import br.com.thomaszoord.sccapturetheflag.Partidas.Time.Time;
import br.com.thomaszoord.sccapturetheflag.Partidas.enums.Status;
import br.com.thomaszoord.sccapturetheflag.SCCaptureTheFlag;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import scverso.VersoManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Partida {

    private final SCCaptureTheFlag plugin = SCCaptureTheFlag.plugin;

    // PARTIDA ==========================================
    public String nome;
    public World mundo;
    public World arena;

    public boolean resetar;


    public Location spawn;
    public int minPlayers = 2;
    public int maxPlayers = 100;

    public Status status = Status.ESPERA;

    public Partida(String nome, Boolean resetar){
        this.nome = nome;
        this.resetar = resetar;

        this.partida.runTaskTimer(SCCaptureTheFlag.plugin, 0L, 20L);

    }

    public Time timeAzul = new Time(null, "Azul", null, ChatColor.BLUE, (short) 11);
    public Time timeVermelho = new Time(null, "Vermelho", null, ChatColor.RED, (short) 14);

    public List<Time> times = Arrays.asList(timeAzul, timeVermelho);

    public List<Player> morto = new ArrayList<>();


    public boolean firstBlood = false;

    //TIMERS PARA MANUSEAMENTO ==========================================
    public int contador = 30;
    public int inicio = 60;
    public int forceStart = 10;
    public int game = 3600;  //UMA HORA
    public int finalizacao = 20;
    public int restauracao = 20;


    //LISTA DE JOGADORES ==========================================
    public ArrayList<Player> jogadores = new ArrayList<>();
    public ArrayList<Player> jogando = new ArrayList<>();
    public ArrayList<Player> espectadores = new ArrayList<>();
    public HashMap<Player, Kit> playerKit = new HashMap<>();
    public HashMap<Player, BukkitRunnable> danoTempoDamager = new HashMap<>();
    public HashMap<Player, Player> tempoKill = new HashMap<>();
    public Time getTimePlayer(Player p) {
        return times.stream()
                .filter(time -> time.players.contains(p))
                .findFirst()
                .orElse(null);
    }

    //VENCEDORES ==========================================
    public Time vencedor = null;
    public int win = 3;

    //METODOS -=====================

    public void entrarEspectador(Player p){
        p.teleport(spawn);

        espectadores.add(p);

        EquiparJogadores.equiparEspectador(p);

        for(Player pl : Bukkit.getOnlinePlayers()){
            pl.hidePlayer(p);
            if(mundo.getPlayers().contains(pl)){
                p.showPlayer(pl);
            }
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2));

        if (!p.getAllowFlight()) {
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setGameMode(GameMode.SPECTATOR);
            p.setGameMode(GameMode.SURVIVAL);
        }

    }
    public void entrarNaPartida(Player p){


        if(jogadores.isEmpty()){
            partida.run();
        }

        espectadores.remove(p);

        p.getInventory().clear();

        for(PotionEffect potionEffect : p.getActivePotionEffects()){
            p.removePotionEffect(potionEffect.getType());
        }

        for(Player pl : Bukkit.getOnlinePlayers()){
            if(!mundo.getPlayers().contains(pl)){
                pl.hidePlayer(p);
                p.hidePlayer(pl);
            } else {

                if(espectadores.contains(pl)){
                    pl.showPlayer(p);
                    p.hidePlayer(pl);
                } else {
                    pl.showPlayer(p);
                    p.showPlayer(pl);
                }

            }
        }

        jogadores.add(p);

        p.teleport(spawn);

        if(status == Status.ESPERA || status == Status.INICIANDO){
            if(status == Status.ESPERA){
                if(jogadores.size() >= minPlayers){
                   iniciarPartida();
                }

            } else {
                int fastStart = 6;

                if(jogadores.size() >= fastStart){
                    enviarMensagem("§aQuantidade de players suficiente! Contador alterado para 10 segundos..");
                    contador = 10;
                }

            }
            if(jogadores.contains(p)){
              enviarMensagem(p.getName() + " §eentrou na partida! " + "§7(" + jogadores.size() + "/" + maxPlayers + ")");
            }

            EquiparJogadores.equiparEspera(p);

        } else if(status == Status.EMJOGO){
            p.sendMessage("§aVocê entrou em uma partida já iniciada!");
            EquiparJogadores.equiparIngame(p);

            jogando.add(p);
        } else{

            Partida partida = PartidaManager.encontrarPartida();

            assert partida != null;
            p.sendMessage("§cPartida finalizando! Enviando para " + partida.nome + "..");
            PartidaManager.setPartidaPlayer(p, partida);
        }




    }
    public void sairDaPartida(Player p){
        if(status == Status.ESPERA || status == Status.INICIANDO){
            if(jogadores.contains(p)){
                enviarMensagem(p.getName() +  " §csaiu da partida! " + "§7(" + jogadores.size() + "/" + maxPlayers + ")");
            }
        } else if(status == Status.EMJOGO){
           if(jogando.contains(p)){
               enviarMensagem(getTimePlayer(p).cor + p.getName() +  " §cabandonou a partida! " + "§7(" + jogadores.size() + "/" + maxPlayers + ")");
           }
       }

        jogadores.remove(p);
        jogando.remove(p);
        morto.remove(p);
        espectadores.remove(p);
        getTimePlayer(p).players.remove(p);
        playerKit.remove(p);
        tempoKill.get(p).remove();
        tempoKill.values().remove(p);
        tempoKill.remove(p);
        if(danoTempoDamager.get(p) != null){
            danoTempoDamager.get(p).cancel();
        }
        danoTempoDamager.remove(p);

        for(Player pl : Bukkit.getOnlinePlayers()){
            p.hidePlayer(pl);
            pl.hidePlayer(p);
        }

        p.getInventory().clear();
        p.setHealth(20);
        p.setFoodLevel(20);

        Espectador.itensStaffer(p);

        if(status == Status.INICIANDO){
            if(jogadores.size() < minPlayers){
                espera();
            }
        } else if(status == Status.EMJOGO){
            if(jogando.size() <= 1){
                finalizarPartida(null);
            }
        }
    }
    public void enviarMensagem(String string){
      for(Player jogadoresDoMundo : mundo.getPlayers()){
          jogadoresDoMundo.sendMessage(string);
      }
    }

    public void enviarTitulo(String arg1, String arg2){
        for(Player jogadoresDoMundo : mundo.getPlayers()){
            jogadoresDoMundo.sendTitle(arg1, arg2);
        }
    }

    public void vencedor(Time vencedor){
        for(Player p : vencedor.players){
            p.sendTitle("§a§lVITÓRIA!", "§7Seu time venceu a partida! Parabéns!");
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 0.5F, 1F);
        }


        for(Player p : mundo.getPlayers()){
            if(!vencedor.players.contains(p)){
                p.sendTitle("§c§lFIM DE JOGO!", "§fTime vencedor: " + vencedor.cor + vencedor.nome);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 0.5F, 1F);
            }
        }

        enviarMensagem(" ");
        enviarMensagem("§c§lFIM DE JOGO!");
        enviarMensagem( "");
        enviarMensagem("§7O " + vencedor.cor + "Time " + vencedor.nome + " §7foi o vencedor!");
        enviarMensagem("");
    }

    public void empate(){
        for(Player p : mundo.getPlayers()){
            p.sendTitle("§e§lEMPATE", "§7Ops.. Que inesperado, um empate!");
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 0.5F, 1F);
        }
    }

    public void resetarPartida() throws IOException {

        String nomeMundo = mundo.getName();

        Bukkit.getServer().unloadWorld(mundo, false);
        Bukkit.getConsoleSender().sendMessage("§aPartida §f" + nome + " §adescarregando as Chunks...");

        VersoManager.getManager().copyVerso(arena.getName(), nomeMundo, false);
        mundo = VersoManager.getManager().getWorld(nomeMundo);

        mundo.setAutoSave(false);

        File arenaConfigFile = new File(plugin.getDataFolder(), "arenas/" + arena.getName() + ".yml");

        if (!arenaConfigFile.exists()) {
          Bukkit.getLogger().warning("Não foi possível encontrar o arquivo de configuração para a arena: " + arena.getName());
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(arenaConfigFile);

        spawn = SCCaptureTheFlag.getLocationConfig(arena, "Spawn", mundo);

        mundo = VersoManager.getManager().getWorld(nome);

        mundo.setGameRuleValue("doDaylightCycle", "false");
        mundo.setGameRuleValue("doMobSpawning", "false");
        mundo.setGameRuleValue("showDeathMessages", "false");
        mundo.setGameRuleValue("doFireTick", "false");

        spawn =  SCCaptureTheFlag.getLocationConfig(arena, "Spawn", mundo);

        timeAzul.spawn = SCCaptureTheFlag.getLocationConfig(arena, "Time azul.Spawn", mundo);
        timeAzul.npcTime = SCCaptureTheFlag.getLocationConfig(arena, "Time azul.NPCEscolherTime", mundo);
        timeAzul.npcKit = SCCaptureTheFlag.getLocationConfig(arena, "Time azul.NPCEscolherKit", mundo);
        timeAzul.wool =  SCCaptureTheFlag.getLocationConfig(arena, "Time azul.Wool", mundo);;

        timeVermelho.spawn = SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.Spawn", mundo);
        timeVermelho.npcTime = SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.NPCEscolherTime", mundo);;
        timeVermelho.npcKit = SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.NPCEscolherKit", mundo);;
        timeVermelho.wool =  SCCaptureTheFlag.getLocationConfig(arena, "Time vermelho.Wool", mundo);;



    }




    //GERENCIAMENTO DA PARTIDA ==================================================
    public void espera(){
        status = Status.ESPERA;
        contador = inicio;
        enviarMensagem("§ePartida sem jogadores suficientes! Precisa de no minimo " + minPlayers + " jogadores para começar!");
    }
    public void iniciarPartida(){
        status = Status.INICIANDO;
        contador = 60;

        enviarMensagem("§aA partida está iniciando!");
    }
    public void comecarPartida(){
        status = Status.EMJOGO;
        contador = game;

        for(Player p : this.jogadores){
            if(espectadores.contains(p)){
                return;
            }
           this.jogando.add(p);
        }

        int metade = jogando.size() / 2;

        // Distribui os jogadores pela metade
        timeAzul.players = new ArrayList<>(jogando.subList(0, metade));
        timeVermelho.players = new ArrayList<>(jogando.subList(metade, jogando.size()));

        if (Math.abs(timeAzul.players.size() - timeVermelho.players.size()) > 1) {
            if (timeAzul.players.size() > timeVermelho.players.size()) {
                Player jogadorMovido = timeAzul.players.remove(0);
                timeVermelho.players.add(jogadorMovido);
            } else {
                Player jogadorMovido = timeVermelho.players.remove(0);
                timeAzul.players.add(jogadorMovido);
            }
        }


        for(Player p : jogando){
            EquiparJogadores.equiparIngame(p);
        }

        for(Player bP : timeAzul.players){
            bP.teleport(timeAzul.spawn);
        }

        for(Player rP : timeVermelho.players){
            rP.teleport(timeVermelho.spawn);
        }
    }
    public void finalizarPartida(Time vencedor){
        contador = finalizacao;
        status = Status.FINALIZANDO;
        this.vencedor = vencedor;

        for(Player p : mundo.getPlayers()){
            p.teleport(spawn);
        }


        if(vencedor == null){

            //VERIFICA O TIME COM AMIS PONTOS
            if(timeVermelho.pontos > timeAzul.pontos){
                vencedor = timeVermelho;
            } else if(timeAzul.pontos > timeVermelho.pontos){
                vencedor = timeAzul;
            } else {
                empate();
                return;
            }


        }

        vencedor(vencedor);
    }
    public void fimDaPartida(){
        for (Player p : mundo.getPlayers()){
            if(p.isOp()){

                if(jogadores.contains(p)){
                    PartidaManager.setPartidaPlayer(p, PartidaManager.encontrarPartida());
                } else {
                    PartidaManager.encontrarPartida().entrarEspectador(p);
                }


            } else {
                PartidaManager.setPartidaPlayer(p, PartidaManager.encontrarPartida());
            }

        }

        jogadores = new ArrayList<>();
        jogando = new ArrayList<>();
        timeAzul.players = new ArrayList<>();
        timeVermelho.players = new ArrayList<>();
        playerKit.clear();
        tempoKill.clear();
        for(BukkitRunnable run : danoTempoDamager.values()){
            run.cancel();
        }
        danoTempoDamager.clear();


        timeAzul.pontos = 0;
        timeAzul.capturado = null;

        timeVermelho.pontos = 0;
        timeVermelho.capturado = null;

        contador = inicio;


        if(resetar){
            status = Status.RESTAURANDO;
            contador = restauracao;
            return;
        }


        espera();

    }

    //CONTADOR ==================================================

    BukkitRunnable partida = new BukkitRunnable() {
        @Override
        public void run() {
//            System.out.println("Partida: " + nome + "Status: " + status.toString() + " Tempo:" + Contador);

            if(status == Status.ESPERA){
                return;
            }
            switch (status){

                case INICIANDO:
                    if ((contador == 20 || contador == 10 || (contador >= 1 && contador <= 5))) {
                        enviarMensagem("§aPartida iniciando em §e"  + contador + " §asegundos!");

                        if (contador <= 5 && contador >= 1) {
                            String titleColor = contador == 5 ? "§45" : contador == 4 ? "§c4" : contador == 3 ? "§63" : contador == 2 ? "§e2" : "§a1";

                            for (Player p : jogadores) {
                                SoundAPI.Contador(p, contador);
                                p.sendTitle(titleColor, "§7A partida está prestes a iniciar!");
                            }
                        }
                    }

                    if(contador == 0){
                        comecarPartida();
                    }

                    contador--;

                    break;

                case EMJOGO:
                    if(contador == 60){
                        enviarMensagem("§cA partida irá finalizar em 60 segundos!");
                    }

                    if ((contador == 20 || contador == 10 || (contador >= 1 && contador <= 5))) {
                        enviarMensagem("§cPartida irá finalizar em §e"  + contador + " §csegundos!");

                        if (contador <= 5 && contador >= 1) {
                            for (Player p : jogadores) {
                                SoundAPI.Contador(p, contador);
                                p.sendTitle("§c" + contador, "§7A partida está prestes a finalizar!");
                            }
                        }
                    }

                    if(contador == 0){
                        finalizarPartida(null);
                    }

                    contador--;
                    break;

                case FINALIZANDO:

                    if(contador == 0){
                        fimDaPartida();
                    }

                    contador--;
                    break;
                case RESTAURANDO:
                    if(contador == restauracao / 2){
                        Bukkit.getConsoleSender().sendMessage("§aPartida §f" + nome + " §aentrando no modo de restauração...");

                        try {
                            resetarPartida();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (contador == 0){
                        espera();
                     }
                    break;

            }
        }
    };


}
