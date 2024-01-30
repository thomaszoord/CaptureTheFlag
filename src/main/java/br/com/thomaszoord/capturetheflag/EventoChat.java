package br.com.thomaszoord.capturetheflag;

import br.com.thomaszoord.Partidas.Partida;
import br.com.thomaszoord.Partidas.Time.Time;
import br.com.thomaszoord.Partidas.PartidaManager;
import br.com.thomaszoord.Partidas.enums.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EventoChat implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part == null){
            p.sendMessage("§cComando desconhecido.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("g")) {
            if (args.length == 0) {
                p.sendMessage("§cUso correto: /g <mensagem>");
                return true;
            }

            if(!(part.status.equals(Status.EMJOGO))) {
                p.sendMessage("§cComando desconhecido.");
                return true;
            }

            String msg = String.join(" ", args);

            Time timeP = part.getTimePlayer(p);

            if(part.espectadores.contains(p)){
                p.sendMessage("§cComando desconhecido.");
                return true;
            }

            if(timeP != null){
                part.enviarMensagem("§7[g] " + timeP.cor + p.getName() + "§8: §7" + msg);
            }


        }

            return true;
        }



    @EventHandler
    public void ChatCTF(AsyncPlayerChatEvent e) {

        e.setCancelled(true);
        String mensagem = e.getMessage();

        Player player = e.getPlayer();
        Partida part = PartidaManager.getPartida(player.getWorld().getName());

       if(part == null){
           e.setCancelled(true);
           return;
       }


        if(part.espectadores.contains(player)){
            for(Player p : part.mundo.getPlayers()){
                if(part.espectadores.contains(p) || p.isOp()){
                    p.sendMessage("§8[ESPEC] " + player.getName() + ": §7" + mensagem);
                }
            }
            return;
        }
        else if (!part.jogadores.contains(player) && !part.espectadores.contains(player)){
            e.setCancelled(true);
            return;
        }


       if(part.status == Status.ESPERA || part.status == Status.INICIANDO){
           part.enviarMensagem("§7" + player.getName() + "§8: §7" + mensagem);
       }

       if(part.status == Status.EMJOGO){
           if(part.getTimePlayer(player) != null){
               part.getTimePlayer(player).enviarMensagemTime(player, e.getMessage());
           }
       }

       if(part.status == Status.FINALIZANDO){
           Time timeP = part.getTimePlayer(player);

           if(timeP != null){
               part.enviarMensagem("§7[g] " + timeP.cor + player.getName() + "§8: §7" + e.getMessage());
           } else {
               part.enviarMensagem("§7[g] " + player.getName() + "§8: §7" + e.getMessage());

           }
       }
    }
    

}
