package com.fieryinferno.aggregator.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fieryinferno.aggregator.controllers.model.AddMatchModel;
import com.fieryinferno.aggregator.controllers.model.MatchModel;
import com.fieryinferno.aggregator.gateway.MatchGateway;
import com.fieryinferno.aggregator.repositories.Match;
import com.fieryinferno.aggregator.repositories.MatchRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by atahmasebi on 4/30/16.
 */
@RestController
@RequestMapping("/matches")
public class MatchController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchGateway matchGateway;

    @RequestMapping("")
    public ResponseEntity<List<MatchModel>> getAllMatches(){
        final List<MatchModel> matchModels = matchRepository.findAll().stream().map(m -> MatchModel.of(m)).collect(Collectors.toList());

        return ResponseEntity.ok().body(matchModels);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Void> addMatch(@RequestBody AddMatchModel addMatchModel){
        final Optional<JsonNode> matchDetails = matchGateway.getMatcheDetails(addMatchModel.getMatchId());

        matchDetails.ifPresent(m -> {
            final String matchId = m.get("id").asText();

            final Match mongoMatch = matchRepository.findByMatchId(matchId);

            if (mongoMatch == null) {
                final Match matchToInsert = new Match();
                matchToInsert.setMatchId(matchId);
                matchToInsert.setStartDate(getLocalDateTime(m.get("schedule").asText()));
                matchToInsert.setMatchStatus(Match.MatchStatus.NOT_STARTED);
                LOGGER.info("Inserting match: {}", matchToInsert);
                matchRepository.insert(matchToInsert);
            }
        });

        return ResponseEntity.ok().body(null);
    }

    private LocalDateTime getLocalDateTime(final String dateStr){
        final LocalDateTime now = LocalDateTime.parse(dateStr, FORMATTER);
        final DateTime dateTime = new DateTime(now.toString(), DateTimeZone.forID("Europe/Madrid"));
        final DateTime dateTime1 =  new DateTime(dateTime).withZone(DateTimeZone.forID("America/Los_Angeles"));
        return dateTime1.toLocalDateTime();
    }

}
