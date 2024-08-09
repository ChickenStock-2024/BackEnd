package com.sascom.chickenstock.domain.ranking.util;

import com.sascom.chickenstock.domain.account.entity.Account;

import java.util.*;

/*
reference: [Codeforces Rating System](https://codeforces.com/blog/entry/20762)
 */
public class RatingCalculatorV1 {

    public static final int INITIAL_RATING = 1200;
    private final int RATING_LOWER_BOUND = 0;
    private final int RATING_UPPER_BOUND = 5000;

    public static int calculateRating(List<Account> accounts) {
        if(accounts == null || accounts.isEmpty()){
            return 0;
        }
        int rating = INITIAL_RATING;
        for (Account account : accounts) {
            rating += account.getRatingChange();
        }
        return rating;
    }

    public List<Account> processCompetitionResult(Map<Account, Integer> accountBeforeRatingMap) {
        // accountRatingMap
        //      keys  : accounts of competition
        //      values: rating before competition
        List<Participant> participants = accountBeforeRatingMap
                .entrySet()
                .stream()
                .map(entry -> new Participant(entry.getKey(), entry.getValue()))
                .toList();

        // calculate rank of competition.
        // time complexity: O(N log N)
        Collections.sort(participants, (lhs, rhs) -> Long.compare(rhs.account.getBalance(), lhs.account.getBalance()));
        for(int i = 0, j = 0; i < participants.size(); i = j) {
            while(j < participants.size() && participants.get(j).ranking == participants.get(i).ranking) {
                participants.get(j).expectedRanking = 1.0;
                participants.get(j++).ranking = i + 1;
            }
        }

        // expectedRanking_i = 1.0 + \sum_{j != i} eloWinProbability(ratingJ, ratingI)
        // why add 1.0? -> our ranking system is 1-based.
        // time complexity: O(N^2)
        for(int i = 0; i < participants.size() - 1; i++) {
            for(int j = i + 1; j < participants.size(); j++) {
                Participant participantI = participants.get(i),
                        participantJ = participants.get(j);
                participantI.expectedRanking +=
                        eloWinProbability(participantJ.beforeRating, participantI.beforeRating);
                participantJ.expectedRanking +=
                        eloWinProbability(participantI.beforeRating, participantJ.beforeRating);
            }
        }


        // ratingChange <- (performanceRating - beforeRating) / 2: this rating change formula can be modified.
        // time complexity: O(N^2 log (RATING_UPPER_BOUND - RATING_LOWER_BOUND))
        for(Participant participant : participants) {
            int left = getPerformanceRating(participant, participants);
            participant.ratingChange = (left - participant.beforeRating) / 2;
        }

        List<Account> resultList = new ArrayList<>(accountBeforeRatingMap.size());
        participants.forEach(participant -> {
            participant.account.updateRankingAndRatingChange(
                    participant.ranking,
                    participant.ratingChange
            );
            resultList.add(participant.account);
        });
        return resultList;
    }

    // find the performanceRating to receive the expectedRanking in this competition.
    // time complexity: O(N log (RATING_UPPER_BOUND - RATING_LOWER_BOUND))
    private int getPerformanceRating(Participant participant, List<Participant> participants) {
        int left = RATING_LOWER_BOUND, right = RATING_UPPER_BOUND;
        double expectedRanking = participant.expectedRanking;
        while(right - left > 1) {
            int mid = (left + right) / 2;
            double midExpectedRanking = 1.0;
            for(Participant otherParticipant : participants) {
                midExpectedRanking += eloWinProbability(otherParticipant.beforeRating, mid);
            }
            if(midExpectedRanking >= expectedRanking) {
                left = mid;
            }
            else {
                right = mid;
            }
        }
        return left;
    }

    /**
     * calculate win probability of playerA against playerB in Elo rating system.
     * @param ratingA - rating of playerA
     * @param ratingB - rating of playerB
     * @return win probability of playerA
     */
    private double eloWinProbability(int ratingA, int ratingB) {
        return 1.0 / (1.0 + Math.pow(10.0, 1.0 * (ratingB - ratingA) / 400.0));
    }

    private static class Participant {
        private final Account account;
        private final int beforeRating;
        private int ranking;
        private double expectedRanking;
        private int ratingChange;
        private Participant(Account account, Integer beforeRating) {
            this.account = account;
            this.beforeRating = beforeRating;
        }
    }
}
