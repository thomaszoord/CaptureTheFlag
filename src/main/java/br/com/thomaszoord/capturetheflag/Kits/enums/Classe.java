package br.com.thomaszoord.capturetheflag.Kits.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Classe {
    SUPORTE("Suporte", (short) 5, ChatColor.GREEN),
    TANQUE("Tanque", (short) 4, ChatColor.YELLOW),
    DANO("Dano", (short) 14, ChatColor.RED);


    public final String nome;
    public final short corClasse;
    public final ChatColor corClasseNome;

    Classe(String nome, short corClasse, ChatColor corClasseNome) {
        this.nome = nome;
        this.corClasse = corClasse;
        this.corClasseNome = corClasseNome;
    }

    public ItemStack getItemStack(){
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, this.corClasse);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(corClasseNome + "§l" + nome);
        item.setItemMeta(itemMeta);

        return item;
    }

}
