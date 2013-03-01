/**
 *
 */
package me.matterickson;

import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import me.matterickson.model.UserEntity;
import me.matterickson.model.Vote;
import me.matterickson.model.XboxGame;
import me.matterickson.util.HibernateUtils;
import me.matterickson.util.UserUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.classic.Session;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author mutmatt
 *
 */
public class XboxGameTest extends TestBase {

  /**
   * setup test suite
   * @return the test
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //suite.addTestSuite(UserTest.class);
    suite.addTestSuite(XboxGameTest.class);
    //suite.addTestSuite(VoteTest.class);
    return suite;
  }
  public void testGetGame() {
    List<XboxGame> games = Lists.newArrayList();
    try {
      games = m_xboxGameService.getAllOwnedXboxGames();
    } catch (Exception e) {
      fail("Exception getting owned games");
    }
    assertNotNull(games);

    Map<Integer, List<XboxGame>> votesToGames = Maps.newHashMap();
    try {
      votesToGames = m_xboxGameService.getAllUnownedXboxGames();
    } catch (Exception e) {
      fail("Exception getting unowned games" + e);
    }
    assertNotNull(votesToGames);

    games = Lists.newArrayList();

    try {
      games = m_xboxGameService.getAllXboxGames();
    } catch (Exception e) {
      fail("Exception getting all games");
    }
    assertNotNull(games);

    XboxGame testGame = null;
    try {
      testGame =  m_xboxGameService.getXboxGameByName(null);
    } catch (Exception e) {
      fail("Exception getting game by null name");
    }
    assertNull(testGame);

    try {
      testGame =  m_xboxGameService.getXboxGameByName("");
    } catch (Exception e) {
      fail("Exception getting game by blank name");
    }
    assertNull(testGame);
  }


  public void testGameImage() {
    String testLocation = "";
    try {
      testLocation =  m_xboxGameService.getGameImageLocationById(0);
    } catch (Exception e) {
      fail("Exception getting game image location by game that doesn't exist");
    }
    assertNull(testLocation);
  }


  public void testVoteForXboxGame() {
    UserEntity user = null;
    user = m_userService.getUserByUsername("user1", "password");
    Vote vote = null;
    try {
      vote = m_xboxGameService.voteForXboxGame(0, null, null);
    } catch (Exception e) {
      fail("Exception voting for game with null user");
    }
    assertNull(vote);

    try {
      vote = m_xboxGameService.voteForXboxGame(0, user, null);
    } catch (Exception e) {
      fail("Exception voting for game with saved user");
    }
    assertNull(vote);

    XboxGame game =  newXboxGame("New Game Number", "Game Image", false, true);
    System.out.println("Game Id " + game.getId());
    try {
      vote = m_xboxGameService.voteForXboxGame(game.getId(), user, null);
    } catch (Exception e) {
      fail("Exception voting for game with saved user");
    }
    assertNotNull(vote);
  }

  public void testAddGame() {
    UserEntity newUser = m_userService.getUserByUsername("user5", "password");
    XboxGame returnGame = null;
    try {
      returnGame = m_xboxGameService.addXboxGame(null, null);
    } catch (Exception e) {
      fail("Exception adding game with null game and null user");
    }
    assertNull(returnGame);

    try {
      returnGame = m_xboxGameService.addXboxGame(null, newUser);
    } catch (Exception e) {
      fail("Exception adding game with null game");
    }
    assertNull(returnGame);

    returnGame = newXboxGame("New Game", "Game Image", false, false);
    try {
      returnGame = m_xboxGameService.addXboxGame(returnGame, newUser);
    } catch (Exception e) {
      fail("Exception adding game with not saved game");
    }
    assertNotNull(returnGame);

    returnGame = newXboxGame("New Game Name", "Game Image", false, true);
    System.out.println("Return new game: " + returnGame);
    try {
      returnGame = m_xboxGameService.addXboxGame(returnGame, newUser);
    } catch (Exception e) {
      fail("Exception adding game with saved game");
    }
    System.out.println("Return game at 153: " + returnGame);
    assertNull(returnGame);

    try {
      returnGame = m_xboxGameService.addXboxGame(returnGame, newUser);
    } catch (Exception e) {
      fail("Exception adding game with not saved game");
    }
    //False because that game already exists
    assertNull(returnGame);
  }

  private XboxGame newXboxGame(String gameName, String gameImage, boolean isOwned, boolean isSaved) {
    XboxGame game = m_xboxGameService.getXboxGameByName(gameName);
      if (game == null) {
          game = new XboxGame(gameName, gameImage, isOwned);
      }
      if (isSaved) {
        Session session = null;
        try {
          session = HibernateUtils.getSessionFactory().openSession();
          session.beginTransaction();
          session.save(game);
          session.getTransaction().commit();
        } catch (RuntimeException re) {
          session.getTransaction().rollback();
          game = null;
        } finally {
          session.close();
        }
      }
    return game;
  }
}
