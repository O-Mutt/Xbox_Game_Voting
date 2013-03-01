package me.matterickson.business;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import me.matterickson.model.UserEntity;
import me.matterickson.model.Vote;
import me.matterickson.model.XboxGame;
import me.matterickson.util.HibernateUtils;
import me.matterickson.util.UserUtils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Game service for interacting with the database for getting game info
 * @author Matt@MattErickson.me
 */
//TODO fix transaction roll backs
@SuppressWarnings("unchecked")
public class XboxGameService {
  private static final Logger s_log = LoggerFactory.getLogger(XboxGameService.class);
  private VoteService m_voteService = new VoteService();

  /**
   * Gets all xbox games that are unowned.
   * @return Map of Integer to list of xbox games.  The integer is the int representation of votes for the
   * corresponding list of xbox games
   */
  public Map<Integer, List<XboxGame>> getAllUnownedXboxGames() {
    Session session = null;
    List<XboxGame> games = Lists.newArrayList();
    //Some games may have the same number of votes (Large -> small ordering)
    SortedMap<Integer, List<XboxGame>> votesToGames = Maps.newTreeMap(Collections.reverseOrder());
    try {
      session = HibernateUtils.getSessionFactory().openSession();
      session.beginTransaction();
      String hqlQuery = "FROM " + XboxGame.class.getName() + " x WHERE x.owned = :owned";
      games = (List<XboxGame>) session.createQuery(hqlQuery).setParameter("owned", false).list();
      session.getTransaction().commit();

      //Game name to vote count to not lose any games before we sort them
      Map<String, Integer> gameNamesToVoteCount = Maps.newHashMap();
      for (XboxGame game : games) {
        gameNamesToVoteCount.put(game.getName(), m_voteService.getVotesByGameId(game.getId(), session).size());
      }

      for (XboxGame game : games) {
        Integer voteCount = gameNamesToVoteCount.get(game.getName());
        if (votesToGames.containsKey(voteCount)) {
          votesToGames.get(voteCount).add(game);
        } else {
          votesToGames.put(voteCount, Lists.newArrayList(game));
        }
      }
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
    } finally {
      session.close();
    }
    return votesToGames;
  }

  /**
   * Gets the list of xbox games that are owned
   * @return the list of owned xbox games
   */
  public List<XboxGame> getAllOwnedXboxGames() {
    Session session = null;
    List<XboxGame> games = Lists.newArrayList();
    try {
      session = HibernateUtils.getSessionFactory().openSession();
      session.beginTransaction();
      String hqlQuery = "FROM " + XboxGame.class.getName() + " x WHERE x.owned = :owned order by x.name ASC";
      games = (List<XboxGame>) session.createQuery(hqlQuery).setParameter("owned", true).list();
      session.getTransaction().commit();
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
    } finally {
      session.close();
    }
    return games;
  }

  /**
   * Get all xbox games
   * @return List of all xbox games
   */
  public List<XboxGame> getAllXboxGames() {
    Session session = null;
    List<XboxGame> games = Lists.newArrayList();
    try {
      session = HibernateUtils.getSessionFactory().openSession();
      session.beginTransaction();
      String hqlQuery = "FROM " + XboxGame.class.getName() + " x";
      games = (List<XboxGame>) session.createQuery(hqlQuery).list();
      session.getTransaction().commit();
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
    } finally {
      session.close();
    }
    return games;
  }

  /**
   * Gets an xbox game by the game's id
   * @return the xbox game, null if none was found
   */
  public XboxGame getXboxGameById(long id, Session session) {
    if (id == 0) {
      return null;
    }
    String hqlQuery = "FROM " + XboxGame.class.getName() + " x WHERE x.id = :id";
    XboxGame game = (XboxGame) session.createQuery(hqlQuery).setParameter("id", id).uniqueResult();
    return game;
  }

  /**
   * Gets an xbox game by the game's name
   * @return the xbox game, null if none was found
   */
  public XboxGame getXboxGameByName(String name) {
    XboxGame game = null;
    if (StringUtils.isEmpty(name)) {
      return game;
    }

    Session session =  null;
    try {
      s_log.debug("Starting session to get game by name");
      session = HibernateUtils.getSessionFactory().openSession();
      session.beginTransaction();
      String hqlQuery = "FROM " + XboxGame.class.getName() + " x WHERE x.name = :name";
      game = (XboxGame) session.createQuery(hqlQuery).setParameter("name", name).uniqueResult();
      session.getTransaction().commit();
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
    } finally {
      session.close();
    }
    return game;
  }


  /**
   * Gets an xbox game image location on the server by the game's id
   * @return the xbox game, null if none was found
   */
  public String getGameImageLocationById(long id) {
    if (id == 0) {
      return null;
    }
    Session session = null;
    String image = "";
    try {
    session = HibernateUtils.getSessionFactory().openSession();
    session.beginTransaction();
    String hqlQuery = "SELECT x.gameImageLocation FROM " + XboxGame.class.getName() + " x WHERE x.id = :id";
    image = (String) session.createQuery(hqlQuery).setParameter("id", id).uniqueResult();
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
    } finally {
      session.close();
    }
    return image;
  }


  /**
   * Votes for the xbox game by the game id.
   * @return boolean, true if the vote was successful, false otherwise
   */
  public Vote voteForXboxGame(long gameId, UserEntity user, Session session) {
    Vote vote = null;
    if (!UserUtils.userCanVoteToday(user) || gameId == 0) {
      return vote;
    }
    boolean hasWrappedSession = true;
    try {
      s_log.error("Starting session to get game by name");
      if (session == null) {
        s_log.error("Session null when going to vote for game, creating a new one");
        session = HibernateUtils.getSessionFactory().openSession();
        session.beginTransaction();
        hasWrappedSession = false;
      }
      String hqlGameQuery = "FROM " + XboxGame.class.getName() + " x WHERE x.id = :id";
      s_log.error("HQL query for games by id {}", hqlGameQuery);
      XboxGame game = (XboxGame) session.createQuery(hqlGameQuery).setParameter("id", gameId).uniqueResult();

      vote = new Vote(game, user);
      session.save(vote);
      session.flush();
      user.getVotes().add(vote);
      session.update(user);
      session.flush();
      if (!hasWrappedSession) {
        session.getTransaction().commit();
      }
    } catch (RuntimeException re) {
      if (vote != null) {
        user.getVotes().remove(vote);
      }
      session.getTransaction().rollback();
    } finally {
      if (!hasWrappedSession) {
        session.close();
      }
    }
    return vote;
  }


  /**
   * Adds an xbox game for a user, also casts a vote for the game
   * @return boolean, true if the game was successfully added, false otherwise
   */
  public XboxGame addXboxGame(XboxGame xgame, UserEntity user) {
    if (!UserUtils.userCanVoteToday(user) || xgame == null) {
      return null;
    } else {
      //Check for game with same name
      XboxGame gameWithName = getXboxGameByName(xgame.getName());
      if (gameWithName != null) {
        return null;
      }
      Session session = null;
      try {
        session = HibernateUtils.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(xgame);
        session.flush();
        voteForXboxGame(xgame.getId(), user, session);
        session.getTransaction().commit();
      } catch (RuntimeException re) {
        session.getTransaction().rollback();
        s_log.debug("Failed to commit or vote for xbox game id {} for user {}", xgame.getId(), user.getUsername());
        xgame = null;
      } finally {
        session.close();
      }
    }
    return xgame;
  }

  /**
   * Sets an xbox game as owned
   * @return boolean true if saved, false otherwise
   */
  public void setXboxGameAsOwned(long gameId) {
    Session session = null;
    try {
      session = HibernateUtils.getSessionFactory().openSession();

      session.beginTransaction();
      XboxGame xgame = getXboxGameById(gameId, session);

      xgame.setOwned(true);
      session.saveOrUpdate(xgame);
      session.getTransaction().commit();
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
    } finally {
      session.close();
    }
  }
}
