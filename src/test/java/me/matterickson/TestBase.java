/**
 *
 */
package me.matterickson;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import me.matterickson.business.UserService;
import me.matterickson.business.VoteService;
import me.matterickson.business.XboxGameService;

/**
 * @author mutmatt
 *
 */
public class TestBase extends TestCase {
  protected UserService m_userService = new UserService();
  protected VoteService m_voteService = new VoteService();
  protected XboxGameService m_xboxGameService = new XboxGameService();

  /**
   * setup test suite
   * @return the test
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    //suite.addTestSuite(UserTest.class);
    //suite.addTestSuite(XboxGameTest.class);
    //suite.addTestSuite(VoteTest.class);
    return suite;
  }

}
