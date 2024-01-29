package br.com.thomaszoord.sccapturetheflag.Partidas.Time;

import br.com.thomaszoord.sccapturetheflag.Partidas.Partida;
import br.com.thomaszoord.sccapturetheflag.Partidas.PartidaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Time {

    public String nome;
    public Location spawn;
    public Location wool;
    public ChatColor cor;

    public short corLa;


    public Player capturado;


    public int pontos = 0;

    public Location npcTime;
    public Location npcKit;


    public ArrayList<Player> players;


    public Time(Location wool, String nome, Location spawn, ChatColor cor, short corLa){
        this.wool = wool;
        this.spawn = spawn;
        this.nome = nome;
        this.cor = cor;
        this.corLa = corLa;

        players = new ArrayList<>();
    }


    public void entrarNoTime(Player p){
        Partida part = PartidaManager.getPartidaPlayer(p);

        if(part.getTimePlayer(p) != null){
            part.getTimePlayer(p).players.remove(p);
        }

        p.sendMessage("§aVocê entrou no " + cor + "Time " + nome + "§a!");
        players.add(p);
    }


}
