package br.com.thomaszoord.capturetheflag;

import br.com.thomaszoord.partidas.Partida;
import br.com.thomaszoord.partidas.time.Time;
import br.com.thomaszoord.partidas.enums.Status;
import br.com.thomaszoord.capturetheflag.Kits.KitsManager;
import br.com.thomaszoord.capturetheflag.Kits.enums.Classe;
import br.com.thomaszoord.capturetheflag.Kits.enums.Kit;
import br.com.thomaszoord.partidas.PartidaManager;
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
    public void inventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Partida part = PartidaManager.getPartidaPlayer(p);

        if (part == null || part.status == Status.FINALIZANDO || e.getCurrentItem() == null) {
            return;
        }

        e.setCancelled(true);

        if (e.getClickedInventory() != null && e.getClickedInventory().getName().contains("Selecione seu time!")) {
            e.setCancelled(true);

            if (e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
                Time timeAtual = part.getTimePlayer(p);

                Bukkit.getLogger().info("Display Name: " + displayName);
                Bukkit.getLogger().info("Time Atual: " + (timeAtual != null ? timeAtual.nome : "Nenhum"));

                if (displayName.contains("Azul") && isTeamChangeAllowed(part, part.timeAzul, timeAtual)) {
                    part.timeAzul.entrarNoTime(p);
                } else if (displayName.contains("Vermelho") && isTeamChangeAllowed(part, part.timeVermelho, timeAtual)) {
                    part.timeVermelho.entrarNoTime(p);
                } else {
                    p.sendMessage("§cTroca de time não permitida devido à diferença de jogadores.");
                    return;
                }

                if (part.status == Status.ESPERA || part.status == Status.INICIANDO) {
                    EquiparJogadores.equiparEspera(p);
                    p.openInventory(selecionarTime(p, part));
                } else {
                    PvPEvents.renascerPlayer(p, null);
                }
            }
        } else if (e.getClickedInventory() != null && e.getClickedInventory().getName().contains("Selecione seu kit!")) {
            e.setCancelled(true);

            for (Kit kit : KitsManager.kitsList) {
                if (e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName() != null &&
                        e.getCurrentItem().getItemMeta().getDisplayName().contains(kit.getNome())) {
                    if (part.status == Status.ESPERA || part.status == Status.INICIANDO) {
                        kit.adicionarKit(p);
                        EquiparJogadores.equiparEspera(p);
                        p.openInventory(kitsInventory(p));
                    } else if (part.status.equals(Status.EMJOGO)) {
                        kit.adicionarKit(p);

                        if(!part.morto.contains(p)){
                            PvPEvents.renascerPlayer(p, null);
                        }
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


    private boolean isTeamChangeAllowed(Partida part, Time novoTime, Time timeAtual) {
        int diferencaTamanho = 1;

        if (timeAtual == null) {
            if (novoTime == part.timeAzul) {
                if(novoTime.players.size() > part.timeVermelho.players.size()){
                    if((novoTime.players.size() - part.timeVermelho.players.size()) <= diferencaTamanho){
                        return false;
                    } else {
                        return true;
                    }
                } else if(novoTime.players.size() == part.timeVermelho.players.size()){
                    return true;
                } else {
                    return true;
                }


            } else if (novoTime == part.timeVermelho) {
                if(novoTime.players.size() > part.timeAzul.players.size()){
                    if((novoTime.players.size() - part.timeAzul.players.size()) <= diferencaTamanho){
                        return false;
                    } else {
                        return true;
                    }
                } else if(novoTime.players.size() == part.timeAzul.players.size()){
                    return true;
                } else {
                    return true;
                }

            }
        } else {
            int sePlayerEntrar = novoTime.players.size() + 1;

            if(sePlayerEntrar > timeAtual.players.size()){
                if((sePlayerEntrar - timeAtual.players.size()) <= diferencaTamanho){
                    return false;
                } else {
                    return true;
                }
            } else if(sePlayerEntrar == timeAtual.players.size()){
                return true;
            } else {
                return true;
            }
        }
        return false;
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
    public Inventory kitsInventory(Player p) {
        Inventory kitsInventory = Bukkit.createInventory(null, 54, "Selecione seu kit!");

        kitsInventory.setItem(10, Classe.TANQUE.getItemStack());
        kitsInventory.setItem(19, Classe.DANO.getItemStack());
        kitsInventory.setItem(28, Classe.SUPORTE.getItemStack());

        int tanqueSlot = 11;
        int danoSlot = 20;
        int suporteSlot = 29;

        for (Kit kit : KitsManager.kitsList) {
            if (kit.getClasse().equals(Classe.TANQUE)) {
                if (tanqueSlot <= 16) {
                    kitsInventory.setItem(tanqueSlot, kit.getItem(p));
                    tanqueSlot++;
                }
            } else if (kit.getClasse().equals(Classe.DANO)) {
                if (danoSlot <= 25) {
                    kitsInventory.setItem(danoSlot, kit.getItem(p));
                    danoSlot++;
                }
            } else if (kit.getClasse().equals(Classe.SUPORTE)) {
                if (suporteSlot <= 34) {
                    kitsInventory.setItem(suporteSlot, kit.getItem(p));
                    suporteSlot++;
                }
            }

            if (tanqueSlot == 17 || danoSlot == 26 || suporteSlot == 35) {
                tanqueSlot += 2;
                danoSlot += 2;
                suporteSlot += 2;
            }
        }

        return kitsInventory;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof  Player)) {
            return true;
        }


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
            p.openInventory(selecionarTime(p, part));
            return true;
        } else if(cmd.getName().equalsIgnoreCase("kit")){
            p.openInventory(kitsInventory(p));
            return true;
        }


        return false;
    }

}
