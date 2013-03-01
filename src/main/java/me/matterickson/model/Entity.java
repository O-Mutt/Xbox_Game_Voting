package me.matterickson.model;

/**
 * Base entity interface to always have an id
 * @author Matt@MattErickson.me
 */
public interface Entity {
  /**
   * @return the id of this
   */
  public Long getId ();
}