package me.matterickson.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author Matt@MattErickson.ME
 */
public class HibernateUtils {

  private static final SessionFactory s_sessionFactory = buildSessionFactory();

  /**
   * Builds a hibernate session factory for interacting with the db
   * @return the session factory
   */
  private static SessionFactory buildSessionFactory() {
    try {
      // Create the SessionFactory from hibernate.cfg.xml
      return new Configuration().configure().buildSessionFactory();
    } catch (Throwable ex) {
      // Make sure you log the exception, as it might be swallowed
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  /**
   * @return
   */
  public static SessionFactory getSessionFactory() {
    return s_sessionFactory;
  }
}
