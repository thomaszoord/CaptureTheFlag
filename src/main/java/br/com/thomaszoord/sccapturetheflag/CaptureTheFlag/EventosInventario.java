package br.com.thomaszoord.sccapturetheflag.CaptureTheFlag;

import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.KitsManager;
import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.enums.Kit;
import br.com.thomaszoord.sccapturetheflag.Partidas.Partida;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import br.com.thomaszoord.sccapturetheflag.Partidas.enums.Status;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EventosInventario implements Listener, CommandExecutor {

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part == null){
            return;
        }

        if(part.status == Status.FINALIZANDO){
            return;
        }

        if(e.getCurrentItem() == null){
            return;
        }

        if(e.getInventory().getName().contains("Selecione seu time!")){
            e.setCancelled(true);

            if(e.getCurrentItem().getItemMeta().getDisplayName() != null && e.getCurrentItem().getItemMeta().getDisplayName().contains("Azul")){

                part.timeAzul.entrarNoTime(p);
            } else if(e.getCurrentItem().getItemMeta().getDisplayName() != null && e.getCurrentItem().getItemMeta().getDisplayName().contains("Vermelho")){
                part.timeVermelho.entrarNoTime(p);
            } else {
                return;
            }


            if(part.status == Status.ESPERA || part.status == Status.INICIANDO){
                EquiparJogadores.equiparEspera(p);
                p.openInventory(selecionarTime(p,part));
            } else {
                PvPEvents.renascerPlayer(p, null);
            }

        } else if(e.getInventory().getName().contains("Selecione seu kit!")) {
            e.setCancelled(true);


            for(Kit kit : KitsManager.kitsList){
                if(e.getCurrentItem().getItemMeta().getDisplayName() != null && e.getCurrentItem().getItemMeta().getDisplayName().contains(kit.getNome())){
                    if(part.status == Status.ESPERA || part.status == Status.INICIANDO){
                        kit.adicionarKit(p);
                        EquiparJogadores.equiparEspera(p);
                        p.openInventory(kitsInventory(p));
                    } else if(part.status.equals(Status.EMJOGO)){
                        PvPEvents.renascerPlayer(p, null);
                    }
                }
            }


        }

        }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part == null){
            return;
        }

        if(p.getItemInHand() != null){
            if(p.getItemInHand().getType().equals(Material.CHEST)){
                p.openInventory(kitsInventory(p));
                return;
            }

            if(p.getItemInHand().getType().equals(Material.WOOL)){
                p.openInventory(selecionarTime(p, part));
                return;
            }

            if(p.getItemInHand().getType().equals(Material.BED)){
                p.kickPlayer("Lobby");
            }
        }
    }


    public static Inventory selecionarTime(Player p, Partida part){
        Inventory kitsInventory = Bukkit.createInventory(null, 27, "Selecione seu time!");

        ItemStack redWool = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta redWoolMeta = redWool.getItemMeta();
        redWoolMeta.setDisplayName("§cTime Vermelho");
        ArrayList<String> loreredWool = new ArrayList<>();
        loreredWool.add("§7Jogadores: " + part.timeVermelho.players.size() + "/10");
        loreredWool.add("");
        if(part.getTimePlayer(p) == part.timeVermelho){
            loreredWool.add("§aVocê já está neste time!");
        } else {
            loreredWool.add("§aClique para entrar!");
        }
        redWoolMeta.setLore(loreredWool);
        redWool.setItemMeta(redWoolMeta);


        ItemStack blueWool = new ItemStack(Material.WOOL, 1, (short) 11);
        ItemMeta blueWoolMeta = blueWool.getItemMeta();
        blueWoolMeta.setDisplayName("§9Time Azul");
        ArrayList<String> loreblueWool = new ArrayList<>();
        loreblueWool.add("§7Jogadores: " + part.timeAzul.players.size() + "/10");
        loreblueWool.add("");
        if(part.getTimePlayer(p) == part.timeAzul){
            loreblueWool.add("§aVocê já está neste time!");
        } else {
            loreblueWool.add("§aClique para entrar!");
        }
        blueWoolMeta.setLore(loreblueWool);
        blueWool.setItemMeta(blueWoolMeta);

        kitsInventory.setItem(11, redWool);
        kitsInventory.setItem(15, blueWool);

        return kitsInventory;
    }

    public Inventory kitsInventory(Player p){
        Inventory kitsInventory = Bukkit.createInventory(null, 54, "Selecione seu kit!");
        
        int inventorySlot = 10;

        for (Kit kit : KitsManager.kitsList) {
            if (inventorySlot == 17 || inventorySlot == 26) {
                inventorySlot += 2;
            }

            if (inventorySlot > 34) {
                break;
            }

            ItemStack item = kit.getItem(p);
            kitsInventory.setItem(inventorySlot, item);
            inventorySlot++;
        }

        return kitsInventory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            Partida part = PartidaManager.getPartidaPlayer(p);

            if(part == null){
                p.sendMessage("§cComando desconhecido.");
                return true;
            }

            if(part.status == Status.FINALIZANDO){
                return true;
            }

            if(cmd.getName().equalsIgnoreCase("time")){
                selecionarTime(p, part);
            } else if(cmd.getName().equalsIgnoreCase("kit")){
                kitsInventory(p);
            }

        }
        return false;
    }
}
