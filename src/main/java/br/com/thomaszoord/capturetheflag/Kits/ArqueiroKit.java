package br.com.thomaszoord.capturetheflag.Kits;

import br.com.thomaszoord.partidas.Partida;
import br.com.thomaszoord.capturetheflag.Kits.enums.Classe;
import br.com.thomaszoord.capturetheflag.Kits.enums.Kit;
import br.com.thomaszoord.capturetheflag.PvPEvents;
import br.com.thomaszoord.partidas.PartidaManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ArqueiroKit extends Kit {

    public int distancia = 20;

    public ArqueiroKit() {
        super("Arqueiro",
                Arrays.asList("§7Caso acerte um jogador a mais de ",
                        " §e20 §7blocos de distancia, ele é eliminado instantaneamente!"),
                Classe.DANO,
                new ItemStack(Material.BOW));
    }

    @Override
    public void equiparItens(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        Partida partida = PartidaManager.getPartidaPlayer(p);

        //ARMADURA
        p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));


        //ESPADA
        p.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));

        //arco
        p.getInventory().setItem(1, new ItemStack(Material.BOW));
        p.getInventory().getItem(1).addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        p.getInventory().getItem(1).addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        //FLECHAS
        p.getInventory().setItem(2, new ItemStack(Material.ARROW, 64));
        p.getInventory().setItem(3, new ItemStack(Material.ARROW, 64));

        //CURA
        p.getInventory().setItem(4, new ItemStack(Material.COOKED_BEEF, 2));

        this.definirItensInquebraveis(p);

        ItemStack chest = new ItemStack(Material.CHEST, 1);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.setDisplayName("§aSelecione seu kit!");
        ArrayList<String> loreChest = new ArrayList<>();
        loreChest.add("§7Clique para selecionar seu kit!");
        if (partida.playerKit.get(p) != null) {
            loreChest.add("§7Kit atual: §a" + partida.playerKit.get(p).getNome());
        } else {
            loreChest.add("§7Kit atual: §eNenhum");
        }
        chestMeta.setLore(loreChest);
        chest.setItemMeta(chestMeta);

        p.getInventory().setItem(8, chest);
    }

    @EventHandler
    public void damageEntityEventByArrow(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow)) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if (partida == null) {
            return;
        }

        Arrow arrow = (Arrow) e.getDamager();

        if (arrow.getShooter() instanceof Player) {
            Player damager = (Player) arrow.getShooter();

            if(e.getEntity().equals(damager)){
                e.setCancelled(true);
                return;
            }

            if (partida.playerKit.get(damager).equals(this)) {
                if (damager.getLocation().distance(p.getLocation()) > distancia) {
                    damager.sendMessage("§aVocê acertou um tiro critico!");
                    partida.enviarMensagem(partida.getTimePlayer(p).cor + p.getName() + " §7foi eliminado a mais de §e" + distancia + " §7blocos de distância por" + partida.getTimePlayer(damager).cor + damager.getName());
                    PvPEvents.renascerPlayer(p, damager);
                }
            }
        }
    }
}

