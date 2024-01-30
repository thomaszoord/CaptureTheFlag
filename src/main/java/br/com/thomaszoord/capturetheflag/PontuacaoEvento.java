package br.com.thomaszoord.capturetheflag;

import br.com.thomaszoord.Partidas.Partida;
import br.com.thomaszoord.Partidas.Time.Time;
import br.com.thomaszoord.Partidas.enums.Status;
import br.com.thomaszoord.Partidas.PartidaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PontuacaoEvento implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if (partida == null || partida.status != Status.EMJOGO) {
            return;
        }

        if (e.getClickedBlock() != null) {
            Location clickedLocation = e.getClickedBlock().getLocation();

            if (saoLocalizacoesIguais(partida.timeAzul.wool, clickedLocation, 1)) {
                if (partida.timeAzul.players.contains(p)) {
                    eventoDePonto(partida, partida.timeAzul, partida.timeVermelho, p);
                } else {
                    realizarAcaoClique(p, partida, partida.timeVermelho, partida.timeAzul);

                    for(Player pl : partida.jogadores){
                        partida.scoreboardGame.ScoreboardUpdate(partida, pl);
                    }
                }
            } else if (saoLocalizacoesIguais(partida.timeVermelho.wool, clickedLocation, 1)) {
                if (partida.timeVermelho.players.contains(p)) {
                    eventoDePonto(partida, partida.timeVermelho, partida.timeAzul, p);
                } else {
                    realizarAcaoClique(p, partida, partida.timeAzul, partida.timeVermelho);

                    for(Player pl : partida.jogadores){
                        partida.scoreboardGame.ScoreboardUpdate(partida, pl);
                    }
                }
            }
        }
    }

    private void realizarAcaoClique(Player p, Partida partida, Time timeAtacante, Time timeDefensor) {
        if (timeAtacante.players.contains(p)) {
            if (timeDefensor.capturado == null) {
                timeDefensor.wool.getBlock().setType(Material.WOOL);
                p.getWorld().strikeLightningEffect(timeDefensor.wool);
                timeDefensor.capturado = p;

                partida.enviarMensagem(timeAtacante.cor + p.getName() + " §aestá com a bandeira do " + timeDefensor.cor  + "Time "  + timeDefensor.nome + "§7!");

                for (Player redPlayers : timeDefensor.players) {
                    redPlayers.sendTitle("§c§LCAPTURADA!", "§7Sua bandeira foi capturada por: " + timeAtacante.cor + p.getName());
                    redPlayers.playSound(redPlayers.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 0.5f);
                }

                for (Player redPlayers : timeAtacante.players) {
                    redPlayers.sendTitle("§a§LCAPTURADA!", "§7A bandeira do time inimigo foi capturada por: " + timeAtacante.cor + p.getName());
                    redPlayers.playSound(redPlayers.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 0.5f);
                }

                for (Player espec : partida.espectadores) {
                    espec.sendTitle("§c§LCAPTURADA!", "§7A bandeira do time " + timeAtacante.cor + timeAtacante.nome + " foi capturada por: " + timeDefensor.cor + p.getName());
                    espec.playSound(espec.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 0.5f);
                }



            }
        }
    }
    private void eventoDePonto(Partida partida, Time timeAtacante, Time timeDefensor, Player p) {
            if (timeDefensor.capturado != null && timeDefensor.capturado.equals(p)) {
                partida.enviarMensagem(timeAtacante.cor + p.getName() + " §acapturou a bandeira do " + timeDefensor.cor + "Time " + timeDefensor.nome);

                timeDefensor.capturado = null;
                timeAtacante.capturado = null;

                timeDefensor.wool.getBlock().setData((byte) timeDefensor.corLa);
                timeAtacante.wool.getBlock().setData((byte) timeDefensor.corLa);

                timeAtacante.pontos++;


                if (timeAtacante.pontos >= partida.win) {
                    partida.finalizarPartida(timeAtacante);
                } else {
                    for(Player tDPlayers : timeDefensor.players){
                        tDPlayers.playSound(tDPlayers.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 0.5F);
                        tDPlayers.sendTitle("§c§lCAPTURADO!",
                                        "§7O Jogador(a) "+ timeAtacante.cor + p.getName() + " §7capturou sua bandeira!");
                    }

                    for(Player tDPlayers : timeAtacante.players){
                        tDPlayers.playSound(tDPlayers.getLocation(), Sound.LEVEL_UP, 0.5F, 0.5F);
                        tDPlayers.sendTitle("§a§lPONTO!",
                                "§7O Jogador(a) "+ timeAtacante.cor + p.getName() + " §7pontuou para o seu time!");
                    }

                    for(Player tDPlayers : partida.espectadores){
                        tDPlayers.playSound(tDPlayers.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 0.5F);
                        tDPlayers.sendTitle("§a§lCAPTURADO!",
                                "§7O Jogador(a) "+ timeAtacante.cor + p.getName() + " §7capturou a bandeira do " + timeDefensor.cor + "Time " +  timeDefensor.nome);
                    }
                    timeDefensor.wool.getWorld().createExplosion(timeDefensor.wool.getX(), timeDefensor.wool.getZ(), timeDefensor.wool
                            .getY(), 10, false, false);
                }

                for(Player pl : partida.jogadores){
                    partida.scoreboardGame.ScoreboardUpdate(partida, pl);
                }

            } else {
                p.sendMessage("§cVocê não pode capturar sua própria bandeira!");
            }



    }
    public boolean saoLocalizacoesIguais(Location loc1, Location loc2, double tolerancia) {
        if (loc1.getWorld() == loc2.getWorld()) {
            double diffX = Math.abs(loc1.getX() - loc2.getX());
            double diffY = Math.abs(loc1.getY() - loc2.getY());
            double diffZ = Math.abs(loc1.getZ() - loc2.getZ());

            return diffX < tolerancia && diffY < tolerancia && diffZ < tolerancia;
        }

        return false;
    }



}
