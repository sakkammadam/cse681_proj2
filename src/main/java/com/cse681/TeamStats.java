package com.cse681;

import java.lang.String;


// POJO for defining stats for a team - Check with Prof. if we could use Record
public class TeamStats {
    // attributes
    String statIdCode;
    String gameCode;
    int teamCode;
    String gameDate;
    int rushYds;
    int rushAtt;
    int passYds;
    int passAtt;
    int passComp;
    int penalties;
    int penaltYds;
    int fumblesLost;
    int interceptionsThrown;
    int firstDowns;
    int thridDownAtt;
    int thirdDownConver;
    int fourthDownAtt;
    int fourthDownConver;
    int timePoss;
    int score;
    String matchup;
    int opponentScore;
    String matchResult;

    // Default Constructor
    public TeamStats() {}

    // Constructor
    public TeamStats(
            String statIdCode, String gameCode, int teamCode,
            String gameDate, int rushYds, int rushAtt,
            int passYds, int passAtt, int passComp,
            int penalties, int penaltYds, int fumblesLost,
            int interceptionsThrown, int firstDowns,
            int thridDownAtt, int thirdDownConver,
            int fourthDownAtt, int fourthDownConver, int timePoss, int score,
            String matchup, int opponentScore, String matchResult
    ){
        this.statIdCode = statIdCode;
        this.gameCode = gameCode;
        this.teamCode = teamCode;
        this.gameDate = gameDate;
        this.rushYds = rushYds;
        this.rushAtt = rushAtt;
        this.passYds = passYds;
        this.passAtt = passAtt;
        this.passComp = passComp;
        this.penalties = penalties;
        this.penaltYds = penaltYds;
        this.fumblesLost = fumblesLost;
        this.interceptionsThrown = interceptionsThrown;
        this.firstDowns = firstDowns;
        this.thridDownAtt = thridDownAtt;
        this.thirdDownConver = thirdDownConver;
        this.fourthDownAtt = fourthDownAtt;
        this.fourthDownConver = fourthDownConver;
        this.timePoss = timePoss;
        this.score = score;
        this.matchup = matchup;
        this.opponentScore = opponentScore;
        this.matchResult = matchResult;
    }

    // Getters
    public String getStatIdCode() {
        return statIdCode;
    }
    public String getGameCode() {
        return gameCode;
    }
    public int getTeamCode() {
        return teamCode;
    }
    public String getGameDate() {
        return gameDate;
    }
    public int getRushYds() {
        return rushYds;
    }
    public int getRushAtt() {
        return rushAtt;
    }
    public int getPassYds() {
        return passYds;
    }
    public int getPassAtt() {
        return passAtt;
    }
    public int getPassComp() {
        return passComp;
    }
    public int getPenalties() {
        return penalties;
    }
    public int getPenaltYds() {
        return penaltYds;
    }
    public int getFumblesLost() {
        return fumblesLost;
    }
    public int getInterceptionsThrown() {
        return interceptionsThrown;
    }
    public int getFirstDowns() {
        return firstDowns;
    }
    public int getThridDownAtt() {
        return thridDownAtt;
    }
    public int getThirdDownConver() {
        return thirdDownConver;
    }
    public int getFourthDownAtt() {
        return fourthDownAtt;
    }
    public int getFourthDownConver() {
        return fourthDownConver;
    }
    public int getTimePoss() {
        return timePoss;
    }
    public int getScore() {
        return score;
    }
    public String getMatchup() { return matchup; }
    public int getOpponentScore() { return opponentScore; }
    public String getMatchResult() { return matchResult;}

    @Override
    public String toString() {
        return "TeamStats{" +
                "statIdCode='" + statIdCode + '\'' +
                ", gameCode='" + gameCode + '\'' +
                ", teamCode=" + teamCode +
                ", gameDate='" + gameDate + '\'' +
                ", rushYds=" + rushYds +
                ", rushAtt=" + rushAtt +
                ", passYds=" + passYds +
                ", passAtt=" + passAtt +
                ", passComp=" + passComp +
                ", penalties=" + penalties +
                ", penaltYds=" + penaltYds +
                ", fumblesLost=" + fumblesLost +
                ", interceptionsThrown=" + interceptionsThrown +
                ", firstDowns=" + firstDowns +
                ", thridDownAtt=" + thridDownAtt +
                ", thirdDownConver=" + thirdDownConver +
                ", fourthDownAtt=" + fourthDownAtt +
                ", fourthDownConver=" + fourthDownConver +
                ", timePoss=" + timePoss +
                ", score=" + score +
                ", matchup=" + matchup + '\'' +
                ", opponentScore=" + opponentScore +
                ", matchResult=" + matchResult + '\'' +
                '}';
    }
}
