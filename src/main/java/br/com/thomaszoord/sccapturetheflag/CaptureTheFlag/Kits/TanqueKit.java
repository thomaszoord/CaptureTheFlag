package br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits;

import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.enums.Kit;
import br.com.thomaszoord.sccapturetheflag.Partidas.Partida;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TanqueKit extends Kit{

    public TanqueKit(){
        super("Tanque",
                Arrays.asList("§7Ganha 5 carnes e uma", "§7armadura de diamante!"),
                new ItemStack(Material.DIAMOND_CHESTPLATE));
    }


    @Override
    public void equiparItens(Player p) {
        p.getInventory().clear();

        p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.COOKED_BEEF, 4));
        p.getInventory().setItem(8, new ItemStack(Material.CHEST));

    }


    @EventHandler
    public void comeCarne(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack itemNaMao = e.getItem();

        Partida partida = PartidaManager.getPartidaPlayer(p);


        if(partida == null){
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (itemNaMao != null && itemNaMao.getType() == Material.COOKED_BEEF) {

                if (partida.playerKit.get(p).equals(this)) {
                    double cura = 5;

                    if (p.getHealth() < 20) {
                        if(cura + p.getHealth() > 20){
                            p.setHealth(20);
                        } else {
                            p.setHealth(p.getHealth() + cura);
                        }

                        itemNaMao.setAmount(itemNaMao.getAmount() - 1);
                    }
                }
            }
        }
    }

}
