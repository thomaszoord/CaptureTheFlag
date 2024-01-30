package br.com.thomaszoord.capturetheflag;

import br.com.thomaszoord.Partidas.Partida;
import br.com.thomaszoord.Partidas.Time.Time;
import br.com.thomaszoord.Partidas.PartidaManager;
import br.com.thomaszoord.CaptureTheFlag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PvPEvents implements Listener {


    public static void renascerPlayer(Player p, Player damager){
        Partida partida = PartidaManager.getPartidaPlayer(p);

        assert partida != null;

        if(damager == null){
            if(partida.getTimePlayer(p).equals(partida.timeAzul)){
                recuperarBandeira(partida, null, p, partida.timeVermelho, partida.getTimePlayer(p));
            } else {
                recuperarBandeira(partida, null, p, partida.timeAzul, partida.getTimePlayer(p));
            }

            partida.enviarMensagem(partida.getTimePlayer(p).cor + p.getName() + " §7morreu sozinho!");
        } else {
            recuperarBandeira(partida, damager, p, partida.getTimePlayer(damager), partida.getTimePlayer(p));
            damager.playSound(damager.getLocation(), Sound.ORB_PICKUP, 0.5F, 1F);
        }

        p.setFoodLevel(20);
        p.setHealth(20);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
        Location locDeath = new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY() + 2, p.getLocation().getZ());
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2));

        for(Player pW : partida.mundo.getPlayers()){
            pW.hidePlayer(p);
        }
        p.teleport(locDeath);
        p.setGameMode(GameMode.SURVIVAL);

        if(!p.getAllowFlight()){
            p.setAllowFlight(true);
            p.setFlying(true);
        }

        partida.morto.add(p);

        p.playSound(p.getLocation(), Sound.ZOMBIE_HURT, 0.3F, 1F);

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

        p.getInventory().setItem(0, chest);


        iniciarContadorRenascer(p);
    }


    public static void iniciarContadorRenascer(Player p){
        Partida partida = PartidaManager.getPartidaPlayer(p);

        new BukkitRunnable(){

            int renascer = 3;
            @Override
            public void run() {
                p.sendTitle("§c§lVOCÊ MORREU!", "§7Você irá renascer em §e" + renascer + " §7segundos!" );

                if(renascer == 0){
                    p.teleport(partida.getTimePlayer(p).spawn);
                    p.setFoodLevel(20);
                    p.setHealth(20);

                    if(partida.playerKit.get(p) != null){
                        partida.playerKit.get(p).equiparItens(p);
                    }

                    for(Player pW : partida.mundo.getPlayers()){
                        pW.showPlayer(p);
                    }

                    for(PotionEffect pE : p.getActivePotionEffects()){
                        p.removePotionEffect(pE.getType());
                    }

                    if(p.getAllowFlight()){
                        p.setAllowFlight(false);
                        p.setFlying(false);
                    }

                    p.sendTitle("§a§lRENASCEU", "§7Você renasceu! Volte para o campo de batalha!");
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 0.5F, 1F);

                    partida.morto.remove(p);

                    this.cancel();
                }
                renascer--;
            }
        }.runTaskTimer(CaptureTheFlag.plugin, 0L, 20L);

    }
    public static void recuperarBandeira(Partida partida, Player damager, Player p, Time timeDefensor, Time timeAtacante) {
        if (damager == null) {
            if (timeDefensor.capturado != null && timeDefensor.capturado.equals(p)) {
                timeDefensor.capturado = null;

                timeDefensor.wool.getBlock().setType(Material.WOOL);
                timeDefensor.wool.getBlock().setData((byte) timeDefensor.corLa);

                partida.enviarMensagem("§aA bandeira do " + timeDefensor.cor +  "Time " + timeDefensor.nome + " §afoi recuperada!");

                for(Player pT : timeDefensor.players){
                    pT.playSound(pT.getLocation(), Sound.ORB_PICKUP, 0.5F, 0.5F);
                    pT.sendTitle("§a§lBANDEIRA RECUPERADA!", "§7A bandeira do seu time foi recuperada!");
                }

                for(Player pT : timeAtacante.players){
                    pT.playSound(pT.getLocation(), Sound.ANVIL_BREAK, 0.5F, 0.5F);
                    pT.sendTitle("§c§lBANDEIRA RECUPERADA!", "§7A bandeira do time inimigo foi recuperada!");
                }
            }
        } else {
            if (timeDefensor.capturado != null && timeDefensor.capturado.equals(p)) {
                timeDefensor.capturado = null;


                timeDefensor.wool.getBlock().setData((byte) timeDefensor.corLa);

                partida.enviarMensagem("§aA bandeira do " + timeDefensor.cor +  "Time " + timeDefensor.nome + " §afoi recuperada por " + timeDefensor.cor + damager.getName());


                for(Player pT : timeDefensor.players){
                    pT.playSound(pT.getLocation(), Sound.ORB_PICKUP, 0.5F, 0.5F);
                    pT.sendTitle("§a§lBANDEIRA RECUPERADA!", "§7A bandeira do seu time foi recuperada!");
                }

                for(Player pT : timeAtacante.players){
                    pT.playSound(pT.getLocation(), Sound.AMBIENCE_CAVE, 0.5F, 0.5F);
                    pT.sendTitle("§c§lBANDEIRA RECUPERADA!", "§7A bandeira do time inimigo foi recuperada!");
                }
            }
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)){
            return;
        }

        Player p = (Player) e.getEntity();
        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part == null){
            return;
        }

        if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            return;
        }


        if(part.morto.contains(p)){
            e.setCancelled(true);

            if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                e.setCancelled(true);
            }

            return;
        }


        String mensagem = "";

        Player damager = null;

        if(e.getCause() == EntityDamageEvent.DamageCause.VOID){
            Time timePlayer = part.getTimePlayer(p);

            if(part.tempoKill.get(p) != null){
                damager = part.tempoKill.get(p);
                mensagem = timePlayer.cor + p.getName() + " §7foi jogado ao void por " + part.getTimePlayer(damager).cor + damager.getName();
            }


            renascerPlayer(p, damager);

            if(damager != null){
                part.enviarMensagem(mensagem);
            }

            return;
        }

        double damageLeft = p.getHealth() - e.getFinalDamage();


        if (damageLeft <= 0) {
            e.setCancelled(true);

            if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
                Time timePlayer = part.getTimePlayer(p);

                if(part.tempoKill.get(p) != null){
                    damager = part.tempoKill.get(p);
                    mensagem = timePlayer.cor + p.getName() + " §7morreu de queda tentando fugir de " + part.getTimePlayer(damager).cor + damager.getName();
                }

                renascerPlayer(p, damager);

                if(damager != null){
                    part.enviarMensagem(mensagem);
                }
            } else if(e.getCause() == EntityDamageEvent.DamageCause.FIRE){

                Time timePlayer = part.getTimePlayer(p);

                if(part.tempoKill.get(p) != null){
                    damager = part.tempoKill.get(p);
                    mensagem = timePlayer.cor + p.getName() + " §7morreu pegando fogo tentando fugir de " + part.getTimePlayer(damager).cor + damager.getName();
                }

                renascerPlayer(p, damager);

                if(damager != null){
                    part.enviarMensagem(mensagem);
                }
            }  else if(e.getCause() == EntityDamageEvent.DamageCause.LAVA){
                Time timePlayer = part.getTimePlayer(p);

                if(part.tempoKill.get(p) != null){
                    damager = part.tempoKill.get(p);
                    mensagem = timePlayer.cor + p.getName() + " §7morreu pegando fogo tentando fugir de " + part.getTimePlayer(damager).cor + damager.getName();
                }

                renascerPlayer(p, damager);

                if(damager != null){
                    part.enviarMensagem(mensagem);
                }
            } else if(e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
                Time timePlayer = part.getTimePlayer(p);

                if(part.tempoKill.get(p) != null){
                    damager = part.tempoKill.get(p);
                    mensagem = timePlayer.cor + p.getName() + " §7morreu sufocado por " + part.getTimePlayer(damager).cor + damager.getName();
                }

                renascerPlayer(p, damager);

                if(damager != null){
                    part.enviarMensagem(mensagem);
                }
            }



        }



    }

    @EventHandler
    public void onDamageEntityByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();


            Partida part = PartidaManager.getPartidaPlayer(p);

            if(part == null){
                event.setCancelled(true);
                return;
            }

            Entity damagerEntity = event.getDamager();

            //SE PLAYER ESTA MORTO CANCELA EVENTO DE DANO
            if(part.morto.contains(p)){
                event.setCancelled(true);

                if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                    event.setCancelled(true);
                }

                return;
            }


            //CANCELA EVENTO DE DANO SE SAO DO MESMO TIME
            if(damagerEntity instanceof Player){
                if(part.getTimePlayer((Player) damagerEntity).equals(part.getTimePlayer(p))){
                    event.setCancelled(true);
                    return;
                }
            } else if (damagerEntity instanceof Arrow){
                Arrow arrow = (Arrow) damagerEntity;

                if(arrow.getShooter() instanceof Player){
                    Player damager = ((Player) arrow.getShooter()).getPlayer();
                    if(part.getTimePlayer(damager).equals(part.getTimePlayer(p))){
                        event.setCancelled(true);
                        return;
                    }

                }
            }



            double damageLeft = p.getHealth() -  event.getFinalDamage();


            if (damageLeft <= 0) {
                event.setCancelled(true);

                String mensagem = "";
                String pName = part.getTimePlayer(p).cor + p.getName();

                if (damagerEntity instanceof Player) {
                    Player damager = (Player) damagerEntity;
                    String damagerName = part.getTimePlayer(damager).cor + damager.getName();
                    mensagem = pName + " §7foi morto por " + damagerName;

                    renascerPlayer(p, damager);
                }


                else if (damagerEntity instanceof Arrow) {
                    Arrow arrow = (Arrow) damagerEntity;

                    if (arrow.getShooter() instanceof Player) {
                        Player damager = (Player) arrow.getShooter();
                        String damagerName = part.getTimePlayer(damager).cor + damager.getName();
                        mensagem = pName + " §7foi alvejado por " + damagerName;

                        renascerPlayer(p, damager);
                    }

                }



                part.enviarMensagem(mensagem);

            } else {


                if (damagerEntity instanceof Arrow) {
                    Arrow arrow = (Arrow) damagerEntity;

                    if (arrow.getShooter() instanceof Player) {
                        Player damager = (Player) arrow.getShooter();
                        setDanoTimer(p, damager);
                    }
                } else if(damagerEntity instanceof Player){
                    Player damager = (Player) damagerEntity;

                    if(part.morto.contains(damager)){
                        event.setCancelled(true);
                        return;
                    }

                    setDanoTimer(p, damager);
                }


            }

        }




    }

    @EventHandler
    public void onEntityPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e){
        e.setCancelled(true);
    }


    public void setDanoTimer(Player p, Player damager) {
        Partida part = PartidaManager.getPartidaPlayer(p);


        if (part == null) {
            return;
        }

        if (part.danoTempoDamager.containsKey(p)) {
            part.danoTempoDamager.get(p).cancel();
        }

        part.tempoKill.put(p, damager);

        BukkitRunnable runnable = new BukkitRunnable() {
            int contador = 10;

            @Override
            public void run() {
                if (contador == 0) {
                    part.tempoKill.remove(p);
                    if (part.danoTempoDamager.get(p) != null) {
                        part.danoTempoDamager.get(p).cancel();
                    }
                }
                contador--;
            }
        };

        part.danoTempoDamager.put(p, runnable);
        runnable.runTaskTimer(CaptureTheFlag.plugin, 0L, 20L);

    }

}








