/**
 *
 */
package me.matterickson;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import me.matterickson.model.Vote;
import me.matterickson.util.HibernateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.classic.Session;

import com.google.common.collect.Lists;

/**
 * @author mutmatt
 *
 */
public class VoteTest extends TestBase {
  /**
   * setup test suite
   * @return the test
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //suite.addTestSuite(UserTest.class);
    //suite.addTestSuite(XboxGameTest.class);
    suite.addTestSuite(VoteTest.class);
    return suite;
  }

  public void testVote() {
    Session session = null;
    List<Vote> votes = Lists.newArrayList();
    try {
      session = HibernateUtils.getSessionFactory().openSession();
      session.beginTransaction();
      votes = m_voteService.getVotesByGameId(0, session);
    } catch (RuntimeException re) {
      fail("Exception while getting votes by game id that isn't in db");
    } finally {
      session.close();
    }
    assertTrue(CollectionUtils.isEmpty(votes));
  }
}
