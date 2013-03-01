package me.matterickson;

import junit.framework.Test;
import junit.framework.TestSuite;
import me.matterickson.model.UserEntity;

/**
 * @author mutmatt
 *
 */
public class UserTest extends TestBase {
  /**
   * setup test suite
   * @return the test
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(UserTest.class);
    //suite.addTestSuite(XboxGameTest.class);
    //suite.addTestSuite(VoteTest.class);
    return suite;
  }

  /**
   * test for all user service methods
   */
  public void testUserService() {
    UserEntity user = null;
    try {
      user = m_userService.getUserByUsername(null, null);
    } catch (Exception e) {
      fail("Failed with null username AND password");
    }
    assertNull(user);

    try {
      user = m_userService.getUserByUsername("user", null);
    } catch (Exception e) {
      fail("Failed with null password");
    }
    assertNull(user);

    try {
      user = m_userService.getUserByUsername(null, "password");
    } catch (Exception e) {
      fail("Failed with null username");
    }
    assertNull(user);

    try {
      user = m_userService.getUserByUsername("user", "password");
    } catch (Exception e) {
      fail("Failed getting user that is not in the db");
    }
    //User should have been saved to db
    assertNotNull(user);

    user = null;
    try {
      user = m_userService.getUserByUsername("user", "password");
    } catch (Exception e) {
      fail("Failed getting saved user");
    }

    assertNotNull(user);
    assertEquals(user.getUsername(), "user");
    assertEquals(user.getPassword(), "password");
  }
}
