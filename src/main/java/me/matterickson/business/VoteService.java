package me.matterickson.business;

import java.util.List;

import me.matterickson.model.Vote;

import org.hibernate.classic.Session;

import com.google.common.collect.Lists;

/**
 * Gets the votes by the game id, returns the list of votes
 * @author Matt@MattErickson.ME
 */
@SuppressWarnings("unchecked")
public class VoteService {
  public List<Vote> getVotesByGameId(long gameId, Session session) throws RuntimeException {
    List<Vote> votes = Lists.newArrayList();
    String hqlQuery = "FROM " + Vote.class.getName() + " v WHERE v.xboxGame.id = :id";
    votes = (List<Vote>) session.createQuery(hqlQuery).setLong("id", gameId).list();
    return votes;
  }
}
