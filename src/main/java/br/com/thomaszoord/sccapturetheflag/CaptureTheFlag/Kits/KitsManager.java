package br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits;

import br.com.thomaszoord.sccapturetheflag.CaptureTheFlag.Kits.enums.Kit;

import java.util.ArrayList;
import java.util.List;

public class KitsManager {

    public static List<Kit> kitsList = new ArrayList<>();

    public static TanqueKit tanqueKit = new TanqueKit();

    static {
        kitsList.add(tanqueKit);
    }

}
