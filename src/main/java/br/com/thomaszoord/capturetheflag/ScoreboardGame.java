package br.com.thomaszoord.capturetheflag;

import br.com.thomaszoord.partidas.Partida;
import br.com.thomaszoord.partidas.time.Time;
import br.com.thomaszoord.partidas.enums.Status;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreboardGame {

    private final String displayName = "§e§lCTF";
    public void ScoreboardCTF(Partida partida, Player p){
        if(partida == null){
            return;
        }

        if (p.getScoreboard() != null) {
            Scoreboard scoreboard = p.getScoreboard();
            Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

            if (objective != null) {
                // Limpa todos os scores associados a este Objective
                for (String entry : scoreboard.getEntries()) {
                    objective.getScoreboard().resetScores(entry);
                }

                // Remove o Objective do Scoreboard
                objective.unregister();
            }
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(displayName, "dummy");
        Date dataAtual = new Date();

        String cor = "§e";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(dataAtual);

        if(partida.status == Status.ESPERA){
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§7" + dataFormatada ).setScore(10);
            objective.getScore("     ").setScore(9);
            objective.getScore("Mapa " + cor + partida.arena.getName()).setScore(8);
            if(partida.jogadores != null){
                objective.getScore("§fJogadores: " + cor + partida.jogadores.size() + "/" + partida.maxPlayers).setScore(7);
            } else {
                objective.getScore("§fJogadores: " + cor + "1" + "/" + partida.maxPlayers).setScore(67);
            }

            objective.getScore("   ").setScore(6);
            if(partida.getTimePlayer(p) != null){
                objective.getScore("Time: " + partida.getTimePlayer(p).cor + partida.getTimePlayer(p).nome).setScore(5);
            } else {
                objective.getScore("Time: §7Nenhum").setScore(5);

            }

            objective.getScore("  ").setScore(4);
            objective.getScore("Aguardando a entrada").setScore(3);
            objective.getScore("de mais jogadores...").setScore(2);
            objective.getScore(" ").setScore(1);
            objective.getScore("§7ip_server").setScore(0);
        } else if(partida.status == Status.INICIANDO){
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§7" + dataFormatada ).setScore(9);
            objective.getScore("     ").setScore(8);
            objective.getScore("Mapa " + cor + partida.arena.getName()).setScore(7);
            if(partida.jogadores != null){
                objective.getScore("§fJogadores: " + cor + partida.jogadores.size() + "/" + partida.maxPlayers).setScore(6);
            } else {
                objective.getScore("§fJogadores: " + cor + "1" + "/" + partida.maxPlayers).setScore(6);
            }

            objective.getScore("   ").setScore(5);
            if(partida.getTimePlayer(p) != null){
                objective.getScore("Time: " + partida.getTimePlayer(p).cor + partida.getTimePlayer(p).nome).setScore(4);
            } else {
                objective.getScore("Time: §7Nenhum" + partida.getTimePlayer(p)).setScore(4);

            }

            objective.getScore("  ").setScore(3);
            objective.getScore("Iniciando em: " + cor + partida.contador).setScore(2);
            objective.getScore(" ").setScore(1);
            objective.getScore("§7ip_server").setScore(0);
        } else if(partida.status == Status.EMJOGO){
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§7" + dataFormatada  + " §fTempo: " + cor + Tempo(partida.contador)).setScore(14);
            objective.getScore("      ").setScore(13);

            //TIME VERMELHO
            objective.getScore("§cTime Vermelho").setScore(12);

            if(partida.timeVermelho.capturado == null){
                objective.getScore("Capturado: §7Nenhum ").setScore(11);
            } else {
                objective.getScore("Capturado: " + partida.timeAzul.cor + partida.timeVermelho.capturado.getName()).setScore(11);
            }
            objective.getScore(pontuacaoTime(partida, partida.timeVermelho) + " ").setScore(10);
            objective.getScore("        ").setScore(9);
            
            
            //TIME AZUL
            objective.getScore("§9Time Azul").setScore(8);
            if(partida.timeAzul.capturado == null){
                objective.getScore("Capturado: §7Nenhum").setScore(7);
            } else {
                objective.getScore("Capturado: " + partida.timeVermelho.cor + partida.timeAzul.capturado.getName()).setScore(7);
            }
            objective.getScore(pontuacaoTime(partida, partida.timeAzul)).setScore(6);
            objective.getScore("  ").setScore(5);
            
            
            objective.getScore("Seu time: " + partida.getTimePlayer(p).cor + partida.getTimePlayer(p).nome).setScore(4);
            objective.getScore("KD: 0/0").setScore(3);
            objective.getScore("Kit: " + cor + partida.playerKit.get(p).getNome()).setScore(2);
            objective.getScore(" ").setScore(1);
            objective.getScore("§7ip_server").setScore(0);
        } else if(partida.status == Status.FINALIZANDO){
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§7" + dataFormatada + " §8mDsP").setScore(9);
            objective.getScore("       ").setScore(8);
            objective.getScore("Fim de Jogo!").setScore(7);
            objective.getScore("     ").setScore(6);
            objective.getScore("§fJogadores: " + cor + partida.jogadores.size() + "/" + partida.maxPlayers).setScore(5);
            objective.getScore("   ").setScore(4);
            if(partida.vencedor == null){
                objective.getScore("Time Vencedor: §E§LEMPATE").setScore(3);
            } else {
                objective.getScore("Time Vencedor: " + cor + partida.vencedor.nome).setScore(3);
            }
            objective.getScore(" ").setScore(2);
            objective.getScore("§7ip_server").setScore(1);
        }


        p.setScoreboard(scoreboard);
    }
    public void ScoreboardUpdate(Partida partida, Player p) {
        if(partida == null){
            return;
        }


        String cor = "§e";
        Date dataAtual = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(dataAtual);

        if(p.getScoreboard().equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        org.bukkit.scoreboard.Scoreboard score = p.getScoreboard();
        Objective objective = score.getObjective(displayName) == null ? score.registerNewObjective(displayName, "dummy") : score.getObjective(displayName);
        objective.setDisplayName(displayName);

        if(partida.status == Status.ESPERA) {
            replaceScore(objective, 10, "§7" + dataFormatada);
            replaceScore(objective, 9, "            ");
            replaceScore(objective, 8, "Mapa " + cor + partida.arena.getName());
            if(partida.jogadores != null){
                replaceScore(objective, 7, "§fJogadores: " + cor + partida.jogadores.size() + "/" + partida.maxPlayers);
            } else {
                replaceScore(objective, 7, "§fJogadores: " + cor + "1" + "/" + partida.maxPlayers);
            }
            replaceScore(objective, 6, "      ");
            if(partida.getTimePlayer(p) != null){
                replaceScore(objective, 5, "Time: " + partida.getTimePlayer(p).cor + partida.getTimePlayer(p).nome);
            } else {
                replaceScore(objective, 5, "Time: §7Nenhum");
            }
            replaceScore(objective, 4, "     ");
            replaceScore(objective, 3, "Aguardando a entrada");
            replaceScore(objective, 2, "de mais jogadores...");
            replaceScore(objective, 1, "  ");
            replaceScore(objective, 0, "§7ip_server");


        } else if(partida.status == Status.INICIANDO){

            replaceScore(objective, 10, "§7" + dataFormatada);
            replaceScore(objective, 9, "        ");
            replaceScore(objective, 8, "Mapa " + cor + partida.arena.getName());

            if(partida.jogadores != null){
                replaceScore(objective, 7, "§fJogadores: " + cor + partida.jogadores.size() + "/" + partida.maxPlayers);
            } else {
                replaceScore(objective, 7, "§fJogadores: " + cor + "1" + "/" + partida.maxPlayers);
            }

            replaceScore(objective, 6, "   ");

            if(partida.getTimePlayer(p) != null){
                replaceScore(objective, 5, "Time: " + partida.getTimePlayer(p).cor + partida.getTimePlayer(p).nome);
            } else {
                replaceScore(objective, 5, "Time: §7Nenhum");
            }
            replaceScore(objective, 4, "  ");
            replaceScore(objective, 3, "Iniciando em: " + cor + partida.contador);


            //FOOTER
            replaceScore(objective, 1, " ");
            replaceScore(objective, 0, "§7ip_server");
        } else if(partida.status == Status.EMJOGO){

            replaceScore(objective, 14, "§7" + dataFormatada + " §fTempo: " + cor + Tempo(partida.contador));

            replaceScore(objective, 13, "               ");

            // TIME VERMELHO
            replaceScore(objective, 12, "§cTime Vermelho ");
            if (partida.timeVermelho.capturado == null) {
                replaceScore(objective, 11, "Capturado: §7Nenhum ");
            } else {
                replaceScore(objective, 11, "Capturado: " + partida.timeAzul.cor + partida.timeVermelho.capturado.getName() + " ");
            }
            replaceScore(objective, 10, pontuacaoTime(partida, partida.timeVermelho) + " ");
            replaceScore(objective, 9, "      ");

            // TIME AZUL
            replaceScore(objective, 8, "§9Time Azul");
            if (partida.timeAzul.capturado == null) {
                replaceScore(objective, 7, "Capturado: §7Nenhum");
            } else {
                replaceScore(objective, 7, "Capturado: " + partida.timeVermelho.cor + partida.timeAzul.capturado.getName());
            }
            replaceScore(objective, 6, pontuacaoTime(partida, partida.timeAzul));
            replaceScore(objective, 5, "  ");

            replaceScore(objective, 4, "Seu time: " + partida.getTimePlayer(p).cor + partida.getTimePlayer(p).nome);
            replaceScore(objective, 3, "KD: 0/0");
            replaceScore(objective, 2, "Kit: " + cor + partida.playerKit.get(p).getNome());


            // FOOTER
            replaceScore(objective, 1, " ");
            replaceScore(objective, 0, "§7ip_server");


        } else if(partida.status == Status.FINALIZANDO){
            replaceScore(objective, 17, "§7" + dataFormatada + " §8LK3OS");
            replaceScore(objective, 16, "    ");

            // REPLACE
            replaceScore(objective, 14, "Fim de jogo! ");
            replaceScore(objective, 13, "      ");
            replaceScore(objective, 12, "§fJogadores " + cor + partida.jogando.size() + "/" + partida.maxPlayers);
            replaceScore(objective, 11, "  ");
            if (partida.vencedor == null) {
                replaceScore(objective, 10, "Time Vencedor: §E§LEMPATE");
            } else {
                replaceScore(objective, 10, "Time Vencedor: " + cor + partida.vencedor.nome);
            }

            // FOOTER
            replaceScore(objective, 1, " ");
            replaceScore(objective, 0, "§7ip_server");
        }



        if(objective.getDisplaySlot() != DisplaySlot.SIDEBAR) objective.setDisplaySlot(DisplaySlot.SIDEBAR); //Vital functionality
        p.setScoreboard(score);
    }


    public static String getEntryFromScore(Objective o, int score) {
        if(o == null) return null;
        if(!hasScoreTaken(o, score)) return null;
        for (String s : o.getScoreboard().getEntries()) {
            if(o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
        }
        return null;
    }

    public static boolean hasScoreTaken(Objective o, int score) {
        for (String s : o.getScoreboard().getEntries()) {
            if(o.getScore(s).getScore() == score) return true;
        }
        return false;
    }

    public static void replaceScore(Objective o, int score, String name) {
        if(hasScoreTaken(o, score)) {
            if(getEntryFromScore(o, score).equalsIgnoreCase(name)) return;
            if(!(getEntryFromScore(o, score).equalsIgnoreCase(name))) o.getScoreboard().resetScores(getEntryFromScore(o, score));
        }
        o.getScore(name).setScore(score);
    }

    public String Tempo(int contador) {
        int minutos = contador / 60;
        int segundos = contador % 60;

        return String.format("%02d:%02d", minutos, segundos);
    }

    public String pontuacaoTime(Partida part, Time time){
        StringBuilder pontosString = new StringBuilder();


        for (int i = 0; i < part.win; i++) {
            if (i < time.pontos) {
                pontosString.append(time.cor).append("⬤");
            } else {
                pontosString.append(ChatColor.GRAY).append("⬤");
            }
        }

        return pontosString.toString();
    }
}
