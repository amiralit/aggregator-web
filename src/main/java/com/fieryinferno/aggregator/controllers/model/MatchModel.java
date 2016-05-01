package com.fieryinferno.aggregator.controllers.model;

import com.fieryinferno.aggregator.repositories.Match;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by atahmasebi on 4/30/16.
 */
public class MatchModel {
    private String matchId;
    private String startDate;
    private String matchStatus;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static MatchModel of(Match match){
        final MatchModel matchModel = new MatchModel();

        matchModel.setMatchId(match.getMatchId());
        matchModel.setMatchStatus(match.getMatchStatus().name());
        matchModel.setStartDate(match.getStartDate().toString(FORMATTER));

        return matchModel;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }
}
