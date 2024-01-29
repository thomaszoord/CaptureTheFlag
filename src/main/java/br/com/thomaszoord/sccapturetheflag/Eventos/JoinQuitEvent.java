package br.com.thomaszoord.sccapturetheflag.Eventos;

import br.com.thomaszoord.sccapturetheflag.Partidas.Partida;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import br.com.thomaszoord.sccapturetheflag.Partidas.enums.Status;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.ArrayList;

public class JoinQuitEvent implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){
        e.setJoinMessage(null);

        Player p = e.getPlayer();
        boolean isStaff = p.isOp(); //SUBSTITUIR

        p.getInventory().clear();

        if(p.isOp()){

            //ESCONDE STAFF PARA TODOS OS PLAYERS
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.hidePlayer(p);
                p.hidePlayer(pl);
            }

            Espectador.itensStaffer(p);
            return;
        }

        PartidaManager.setPartidaPlayer(p, PartidaManager.encontrarPartida());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
        PartidaManager.sairPartidaPlayer(e.getPlayer());
    }

    @EventHandler
    public void IntereractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if(partida != null){
            return;
        }

        if(p.getItemInHand().getType().equals(Material.CHEST)){
            p.openInventory(partidas());
        }

        //ENVIA PLAYER PRO LOBBY
        if(p.getItemInHand().getType().equals(Material.BED)){
            p.kickPlayer("Lobby");
        }
    }

    public Inventory partidas() {
        Inventory partidas = Bukkit.createInventory(null, 54, "Selecione uma partida");

        int inventorySlot = 10; // Inicializa a posição no inventário

        for (Partida partida : PartidaManager.partidas.values()) {
            if (inventorySlot == 17 || inventorySlot == 26) {
                inventorySlot += 2;
            }

            if (inventorySlot > 34) {
                // Se atingir o limite do inventário, saia do loop
                break;
            }

            ItemStack item = partidaItem(partida);
            partidas.setItem(inventorySlot, item);
            inventorySlot++;
        }

        return partidas;
    }
    public ItemStack partidaItem(Partida partida){
        ChatColor cor = null;
        ItemStack item = null;

        if(partida.status == Status.ESPERA){
            cor = ChatColor.YELLOW;
            item = criarCorante(DyeColor.YELLOW);
        } else if(partida.status == Status.INICIANDO){
            cor = ChatColor.GREEN;
            item = criarCorante(DyeColor.GREEN);
        } else if(partida.status == Status.EMJOGO){
            cor = ChatColor.RED;
            item = criarCorante(DyeColor.RED);
        } else if(partida.status == Status.FINALIZANDO){
            cor = ChatColor.GOLD;
            item = criarCorante(DyeColor.ORANGE);
        } else {
            cor = ChatColor.BLUE;
            item = criarCorante(DyeColor.BLUE);
        }
        
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(cor + partida.nome);
        ArrayList<String> loreitem = new ArrayList<>();
        loreitem.add("");
        loreitem.add(cor + "Status §7» §f" + partida.status);
        loreitem.add(cor + "Jogadores §7» §f" + partida.jogadores.size() + "/" + partida.maxPlayers);
        loreitem.add("");
        loreitem.add(cor + "Mapa §7» §f" + cor + partida.mundo.getName());
        loreitem.add(cor + "Arena §7» §f" + cor + partida.arena.getName());
        loreitem.add("");
        loreitem.add("§eBotão direito para entrar como espectador.");
        loreitem.add("§aBotão esquerdo para entrar na partida.");
        itemMeta.setLore(loreitem);
        item.setItemMeta(itemMeta);
       

        return item;
    }

    public static ItemStack criarCorante(DyeColor cor) {
        Dye corante = new Dye();
        corante.setColor(cor);

        ItemStack coranteItem = corante.toItemStack();
        coranteItem.setAmount(1);

        return coranteItem;
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if (partida != null) {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        if (e.getInventory().getName().equalsIgnoreCase("Selecione uma partida")) {
            Partida partSelecionada = null;

            for (Partida part : PartidaManager.partidas.values()) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains(part.nome)) {
                        partSelecionada = part;
                        break;
                    }
                }
            }

            if (partSelecionada != null && partSelecionada.mundo != null) {
                if (e.isLeftClick()) {
                   PartidaManager.setPartidaPlayer(p, partSelecionada);
                } else if (e.isRightClick()) {

                    Partida partidaAnterior = PartidaManager.getPartida(p.getWorld().getName());
                    if(partidaAnterior.espectadores.contains(p)){
                        partidaAnterior.sairDaPartida(p);
                    }
                    partSelecionada.entrarEspectador(p);

                    p.sendMessage("§aVocê entrou como espectador na partida " + partSelecionada.mundo.getName() + " com sucesso!");
                }
            }
        }
    }

}
