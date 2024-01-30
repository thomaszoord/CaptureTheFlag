package br.com.thomaszoord.capturetheflag.Kits;

import br.com.thomaszoord.capturetheflag.Kits.enums.Kit;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

    public static List<Kit> kitsList = new ArrayList<>();

    public static TanqueKit tanqueKit = new TanqueKit();
    public static ArqueiroKit arqueiroKit = new ArqueiroKit();
    public static AssassinoKit assassinoKit = new AssassinoKit();

    static {
        kitsList.add(tanqueKit);
        kitsList.add(arqueiroKit);
        kitsList.add(assassinoKit);
    }

}
