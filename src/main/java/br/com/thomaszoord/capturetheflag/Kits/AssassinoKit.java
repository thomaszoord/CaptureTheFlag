package br.com.thomaszoord.capturetheflag.Kits;

import br.com.thomaszoord.Eventos.EventosUtils;
import br.com.thomaszoord.Partidas.Partida;
import br.com.thomaszoord.capturetheflag.Kits.enums.Classe;
import br.com.thomaszoord.capturetheflag.Kits.enums.Kit;
import br.com.thomaszoord.capturetheflag.PvPEvents;
import br.com.thomaszoord.Partidas.PartidaManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;

public class AssassinoKit extends Kit {

    public AssassinoKit() {
        super("Assassino",
                Arrays.asList("§7Acerte 3 hits em um jogador inimigo",
                        "§7enquanto ele ainda está", "§7invisivel e elimine-o instantaneamente"),
                Classe.DANO
                , new ItemStack(Material.IRON_SWORD));
    }

    @Override
    public void equiparItens(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        Partida partida = PartidaManager.getPartidaPlayer(p);

        //ARMADURA
        p.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));


        p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));

        Potion pocao = new Potion(PotionType.INVISIBILITY);

        pocao.setLevel(1);
        pocao.setSplash(false);

        p.getInventory().setItem(1, new ItemStack(pocao.toItemStack(1)));


        p.getInventory().setItem(2, new ItemStack(pocao.toItemStack(1)));

        p.getInventory().setItem(3, new ItemStack(Material.COOKED_BEEF, 2));

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
    public void beberPocao(PlayerItemConsumeEvent e){
        if(e.getItem().getType().equals(Material.POTION)){
            e.setCancelled(true);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1));
            EventosUtils.removeItemFromInventory(e.getPlayer(), Material.POTION, 1);
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageEvent e){

        if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
            return;
        }

        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            Partida partida = PartidaManager.getPartidaPlayer(p);

            if (partida == null) {
                return;
            }



            if(partida.playerKit.get(p).equals(this)){
                removerEfeitosInvisibilidade(p);
            }

        }
    }
    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if (partida == null) {
            return;
        }

        if (e.getDamager() instanceof Arrow) {
            if (partida.playerKit.get(p).equals(this)) {
                removerEfeitosInvisibilidade(p);
            }
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }


        if(partida.playerKit.get(p).equals(this)){
            removerEfeitosInvisibilidade((Player) e.getEntity());
        }


        Player damager = (Player) e.getDamager();

        if (partida.playerKit.get(damager).equals(this)) {
            if (damager.getItemInHand().getType().equals(Material.IRON_SWORD)) {
                boolean estaInvisivel = false;

                for (PotionEffect potionEffect : damager.getActivePotionEffects()) {
                    if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                        estaInvisivel = true;
                        removerEfeitosInvisibilidade(damager);
                    }
                }

                if (estaInvisivel) {
                    PvPEvents.renascerPlayer(p, damager);
                    partida.enviarMensagem(partida.getTimePlayer(p).cor + p.getName()
                            + " §7foi §eassassinado §7por "
                            + partida.getTimePlayer(damager).cor + damager.getName());
                }
            }
        }
    }

    private void removerEfeitosInvisibilidade(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5F, 0.5F);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.SPEED);

                break;
            }
        }

    }

}
