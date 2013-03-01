package me.matterickson.business;

import me.matterickson.model.UserEntity;
import me.matterickson.util.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for getting information about users from the db
 * @author author Matt@MattErickson.me
 */
public class UserService {
  private static final Logger s_log = LoggerFactory.getLogger(UserService.class);

  /**
   * Gets a user by username from the database, also validates that the user password matches
   * if the user is not in the database, considering the scope of the project, we will create the user
   * and save it.
   * @param username the username to get user by
   * @param password the password of the user
   * @return the user if found/created null if bad password
   */
  public UserEntity getUserByUsername(String username, String password) {
    if (username == null) {
      return null;
    }
    if (password == null) {
      return null;
    }
    username = StringUtils.trimToEmpty(StringUtils.lowerCase(username));
    password = StringUtils.trimToEmpty(StringUtils.lowerCase(password));
    Session session = HibernateUtils.getSessionFactory().openSession();
    session.beginTransaction();
    String hqlQuery = "FROM " + UserEntity.class.getName() + " n WHERE n.username = :username";
    UserEntity user = null;
    try {
      user = (UserEntity) session.createQuery(hqlQuery).setParameter("username", username).uniqueResult();
    } catch (Exception e) {
      s_log.debug("Failed to pull a user from the database (or failed because {}) for username {}", e, username);
    }
    try {
      if (user != null && StringUtils.equals(user.getPassword(), password)) {
        session.getTransaction().commit();
      } else if (user == null) {
        user = new UserEntity(username, password);
        session.save(user);
        session.flush();
        session.getTransaction().commit();
      } else {
        user = null;
      }
    } catch (RuntimeException re) {
      session.getTransaction().rollback();
      user = null;
    } finally {
      session.close();
    }
    return user;
  }
}
