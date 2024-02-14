package br.com.thomaszoord.api;


import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAPI {

    public static void Morte(Player p){
        TocarSom(Sound.BAT_DEATH ,p);
    }
    public static void Kill(Player p){
        TocarSom(Sound.ORB_PICKUP ,p);

    }
    public static void FimDaPartida(Player p){
        TocarSom(Sound.LEVEL_UP ,p);
    }
    public static void Vitoria(Player p){
        TocarSom(Sound.LEVEL_UP ,p);
    }
    public static void Teleportar(Player p){
        TocarSom(Sound.ENDERMAN_TELEPORT ,p);
    }
    public static void Contador(Player p, Integer i){
        switch (i){
            case 1:
                p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                break;
            case 2:
                p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 0.5f);
                break;
            case 3:
                p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 0.4f);
                break;
            case 4:
                p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 0.3f);
                break;
            case 5:
                p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 0.2f);
                break;
        }
    }
    public static void TocarSom(Sound s, Player p){
        p.playSound(p.getLocation(), s, 1.0f, 1.0f);
    }

}
