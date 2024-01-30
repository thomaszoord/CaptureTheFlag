package br.com.thomaszoord.capturetheflag;

import br.com.thomaszoord.Partidas.Partida;
import br.com.thomaszoord.Partidas.Time.Time;
import br.com.thomaszoord.capturetheflag.Kits.KitsManager;
import br.com.thomaszoord.capturetheflag.Kits.enums.Kit;
import br.com.thomaszoord.Partidas.PartidaManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EquiparJogadores {


    public static void equiparEspera(Player p){

        Partida partida = PartidaManager.getPartidaPlayer(p);

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20);
        p.setFoodLevel(20);

        if(p.isOp()){
            if(!p.getAllowFlight()){
                p.setAllowFlight(true);
                p.setFlying(true);
            }
        }


        ItemStack chest = new ItemStack(Material.CHEST, 1);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.setDisplayName("§aSelecione seu kit!");
        ArrayList<String> loreChest = new ArrayList<>();
        loreChest.add("§7Clique para selecionar seu kit!");
        if(partida.playerKit.get(p) != null){
            loreChest.add("§7Kit atual: §e" + partida.playerKit.get(p).getNome());
        } else {
            loreChest.add("§7Kit atual: §8Nenhum");
        }
        chestMeta.setLore(loreChest);
        chest.setItemMeta(chestMeta);

        Time pTime = partida.getTimePlayer(p);

        ItemStack wool = getTime(pTime);

        ItemStack lobby = new ItemStack(Material.BED, 1);
        ItemMeta lobbyMeta = lobby.getItemMeta();
        lobbyMeta.setDisplayName("§cRetornar ao lobby! §7(clique para sair)");
        ArrayList<String> lorelobby = new ArrayList<>();
        lorelobby.add("§7Clique para retornar ao lobby!");
        lobbyMeta.setLore(lorelobby);
        lobby.setItemMeta(lobbyMeta);


        p.getInventory().setItem(0, chest);
        p.getInventory().setItem(4, wool);
        p.getInventory().setItem(8, lobby);


    }

    public static void equiparIngame(Player p){
        Partida partida = PartidaManager.getPartidaPlayer(p);

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        if(partida.playerKit.get(p) != null){
            partida.playerKit.get(p).equiparItens(p);
        } else {
            Kit kitDefault = KitsManager.tanqueKit;
            kitDefault.adicionarKit(p);
            kitDefault.equiparItens(p);
        }

        if(p.getAllowFlight()){
            p.setAllowFlight(false);
            p.setFlying(false);
        }

        ItemStack chest = new ItemStack(Material.CHEST, 1);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.setDisplayName("§aSelecione seu kit!");
        ArrayList<String> loreChest = new ArrayList<>();
        loreChest.add("§7Clique para selecionar seu kit!");
        if(partida.playerKit.get(p) != null){
            loreChest.add("§7Kit atual: §a" + partida.playerKit.get(p).getNome());
        } else {
            loreChest.add("§7Kit atual: §eNenhum");
        }
        chestMeta.setLore(loreChest);
        chest.setItemMeta(chestMeta);

        p.getInventory().setItem(8, chest);
    }

    public static void equiparEspectador(Player p){
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

    }
    private static ItemStack getTime(Time pTime) {
        short corLa;

        if(pTime == null){
            corLa = (short) 0;
        } else {
            corLa = pTime.corLa;
        }

        ItemStack wool = new ItemStack(Material.WOOL, 1, corLa);
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName("§aSelecione seu time!");
        ArrayList<String> lorewool = new ArrayList<>();
        lorewool.add("§7Clique para selecionar seu time!");

        if(pTime != null){
            lorewool.add("§7Time atual: " + pTime.cor + pTime.nome);
        } else {
            lorewool.add("§7Time atual: §8Nenhum");
        }

        woolMeta.setLore(lorewool);
        wool.setItemMeta(woolMeta);
        return wool;
    }
}
