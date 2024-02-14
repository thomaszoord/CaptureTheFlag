package br.com.thomaszoord.eventos;

import br.com.thomaszoord.partidas.Partida;
import br.com.thomaszoord.partidas.Arenas;
import br.com.thomaszoord.partidas.PartidaManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Espectador implements Listener {
    public static void itensStaffer(Player p){
        //PARTIDAS
        ItemStack chest = new ItemStack(Material.CHEST, 1);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.setDisplayName("§aPartidas §7(clique para selecionar)");
        ArrayList<String> loreChest = new ArrayList<>();
        loreChest.add("§7Clique para ver a lista de partidas disponíveis!");
        chestMeta.setLore(loreChest);
        chest.setItemMeta(chestMeta);

        ItemStack lobby = new ItemStack(Material.BED, 1);
        ItemMeta lobbyMeta = lobby.getItemMeta();
        lobbyMeta.setDisplayName("§cRetornar ao lobby! §7(clique para sair)");
        ArrayList<String> lorelobby = new ArrayList<>();
        lorelobby.add("§7Clique para retornar ao lobby!");
        lobbyMeta.setLore(lorelobby);
        lobby.setItemMeta(lobbyMeta);

        p.getInventory().setItem(2, chest);
        p.getInventory().setItem(6, lobby);
    }
    @EventHandler
    public void onBreakBlockEvent(BlockBreakEvent e){

        Player p = e.getPlayer();

        Partida partida = PartidaManager.getPartida(p.getWorld().getName());

        if(Arenas.arenas.contains(p.getWorld())){
            return;
        }

        if(partida == null){
            e.setCancelled(true);
            return;
        }

        if(partida.espectadores.contains(p)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void inventoryMoveEvent(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();

        Partida partida = PartidaManager.getPartida(p.getWorld().getName());

        if(Arenas.arenas.contains(p.getWorld())){
            return;
        }


        if(partida == null){
            e.setCancelled(true);
            return;
        }

        if(partida.espectadores.contains(p)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e){
        Player p = e.getPlayer();

        Partida partida = PartidaManager.getPartida(p.getWorld().getName());


        if(Arenas.arenas.contains(p.getWorld())){
            return;
        }


        if(partida == null){
            e.setCancelled(true);
            return;
        }

        if(partida.espectadores.contains(p)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageEntityEvent(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)){
            return;
        }
        Player p = (Player) e.getEntity();

        Partida partida = PartidaManager.getPartida(p.getWorld().getName());

        if(partida == null){
            e.setCancelled(true);
            return;
        }

        if(partida.espectadores.contains(p)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player)){
            return;
        }
        if(!(e.getEntity() instanceof Player)){
            return;
        }

        Player p = (Player) e.getEntity();

        Partida partida = PartidaManager.getPartida(p.getWorld().getName());

        if(partida == null){
            e.setCancelled(true);
            return;
        }

        if(partida.espectadores.contains((Player) e.getDamager()) || partida.espectadores.contains(p)){
            e.setCancelled(true);
        }
    }

}
