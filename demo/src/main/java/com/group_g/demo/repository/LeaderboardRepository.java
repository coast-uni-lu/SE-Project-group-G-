package com.group_g.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;

import com.group_g.demo.model.FinalResult;



public interface LeaderboardRepository extends MongoRepository<FinalResult, String> {
    List<FinalResult> findByOrderByFinalScoreDescTimestampAsc(Pageable pageable);
    // spring reads the method's namee and automatically builds the querry based on it
    // this sorts leaderboard entries by score (best on top), if tied, the earliest submit will be top
    // find all records, order by score, then by timestamp (if tied)
}
