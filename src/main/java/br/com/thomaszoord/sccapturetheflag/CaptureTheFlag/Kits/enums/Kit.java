package br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.enums;

import br.com.thomaszoord.sccapturetheflag.Partidas.Partida;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import br.com.thomaszoord.sccapturetheflag.SCCaptureTheFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit implements Listener {


    protected String nome;
    protected List<String> descricao;
    protected ItemStack item;

    public Kit(String nome, List<String>  descricao, ItemStack item) {
        this.nome = nome;
        this.item = item;
        this.descricao = descricao;

        Bukkit.getServer().getPluginManager().registerEvents(this, SCCaptureTheFlag.plugin);
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
            lore.add("§aClique para selecionar!");
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
        p.sendMessage("§aVocê selecionou o kit: §e" + getNome() + "§a§!");
    }
}
