package br.com.thomaszoord.capturetheflag.Kits.enums;

import br.com.thomaszoord.Eventos.EventosUtils;
import br.com.thomaszoord.Partidas.Partida;
import br.com.thomaszoord.Partidas.PartidaManager;
import br.com.thomaszoord.CaptureTheFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit implements Listener {


    protected String nome;
    protected List<String> descricao;
    protected ItemStack item;

    protected Classe classe;

    public Kit(String nome, List<String>  descricao, Classe classe, ItemStack item) {
        this.nome = nome;
        this.item = item;
        this.classe = classe;
        this.descricao = descricao;

        Bukkit.getServer().getPluginManager().registerEvents(this, CaptureTheFlag.plugin);
    }

    public abstract void equiparItens(Player p);


    public ItemStack getItem(Player p) {
        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part == null){
            return null;
        }


        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("§aKit " + nome);
        ArrayList<String> lore = new ArrayList<>(getDescricao());
        lore.add("");
        if(part.playerKit.get(p) == this){
            lore.add("§aKit selecionado!");
        } else {
            lore.add("§eClique para selecionar!");
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        
        return item;
    }

    public List<String> getDescricao() {
        return descricao;
    }

    public String getNome() {
        return nome;
    }

    public void adicionarKit(Player p){
        Partida partida = PartidaManager.getPartidaPlayer(p);

        if(partida == null){
            return;
        }

        partida.playerKit.put(p, this);
        partida.scoreboardGame.ScoreboardUpdate(partida, p);
        p.sendMessage("§aVocê selecionou o kit: §e" + getNome() + "§a§!");
    }

    public Classe getClasse() {
        return classe;
    }

    public void definirItensInquebraveis(Player p){
        for(ItemStack item : p.getInventory().getContents()){
            if (item != null) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.spigot().setUnbreakable(true);
                }
                item.setItemMeta(itemMeta);
            }
        }

        for(ItemStack item : p.getInventory().getArmorContents()){
            if (item != null) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.spigot().setUnbreakable(true);
                }
                item.setItemMeta(itemMeta);

            }
        }
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

                        EventosUtils.removeItemFromInventory(e.getPlayer(), Material.COOKED_BEEF, 1);

                    }
                }
            }
        }
    }
}
