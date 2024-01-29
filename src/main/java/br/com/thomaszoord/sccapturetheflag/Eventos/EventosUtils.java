package br.com.thomaszoord.sccapturetheflag.Eventos;

import br.com.thomaszoord.sccapturetheflag.Partidas.Partida;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import br.com.thomaszoord.sccapturetheflag.Partidas.enums.Status;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EventosUtils implements Listener {

    @EventHandler
    public void damageEvent(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)){
            return;
        }
        Player p = (Player) e.getEntity();

        Partida partida = PartidaManager.getPartidaPlayer(p);

        if(partida == null){
            return;
        }

        if(partida.status != Status.EMJOGO){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void quebrarBloco(BlockBreakEvent e){
        Player p = e.getPlayer();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if(partida == null){
            return;
        }

        if(partida.status == Status.ESPERA || partida.status == Status.INICIANDO){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void colocarBloco(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if(partida == null){
            return;
        }

        if(partida.status == Status.ESPERA || partida.status == Status.INICIANDO){
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }
    @EventHandler
    public void tirarChuva(WeatherChangeEvent e) {
        if (e.toWeatherState())
            e.setCancelled(true);
    }
    @EventHandler
    public void tirarTempestade(ThunderChangeEvent e) {
        if (e.toThunderState())
            e.setCancelled(true);
    }

    @EventHandler
    public void CancelarConquistas(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            // Cancela o evento de propagação de fogo
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void CancelCraftEvent(CraftItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        // Verifique o tipo de inventário que está sendo aberto
        if (event.getInventory().getType() == org.bukkit.event.inventory.InventoryType.WORKBENCH
                || event.getInventory().getType() == org.bukkit.event.inventory.InventoryType.ANVIL) {
            event.setCancelled(true); // Cancela a abertura do inventário
        }
    }

}
