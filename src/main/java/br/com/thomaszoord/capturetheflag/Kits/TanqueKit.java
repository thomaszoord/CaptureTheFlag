package br.com.thomaszoord.capturetheflag.Kits;

import br.com.thomaszoord.capturetheflag.Kits.enums.Classe;
import br.com.thomaszoord.capturetheflag.Kits.enums.Kit;
import br.com.thomaszoord.partidas.Partida;
import br.com.thomaszoord.partidas.PartidaManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class TanqueKit extends Kit{

    public TanqueKit(){
        super("Tanque",
                Arrays.asList("§7Ganha 5 carnes e uma", "§7armadura de diamante!"), Classe.TANQUE,
                new ItemStack(Material.DIAMOND_CHESTPLATE));
    }


    @Override
    public void equiparItens(Player p) {
        p.getInventory().clear();

        Partida partida = PartidaManager.getPartidaPlayer(p);

        p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.COOKED_BEEF, 4));

        this.definirItensInquebraveis(p);

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




}
