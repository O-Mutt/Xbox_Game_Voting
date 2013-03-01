package me.matterickson.model;

import javax.persistence.Entity;

import org.hibernate.annotations.Type;

/**
 * Database representation of an xbox game
 * @author Matt@MattErickson.me
 */
@Entity
public class XboxGame extends PersistedEntity {

  private boolean m_owned = false;
  private String m_name;
  private String m_gameImageLocation;

  /**
   * Default Constructor
   */
  public XboxGame() {
  }

  /**
   * Constructor with only required fields
   * @param name name of the game
   * @param owned true if owned false if not
   */
  public XboxGame(String name, boolean owned) {
    m_name = name;
    m_owned = owned;
  }

  /**
   * Constructor with required fields and image of the game
   * @param name name of the game
   * @param gameImageLocation the location of the image of the game
   * @param owned true if owned false if not
   */
  public XboxGame(String name, String gameImageLocation, boolean owned) {
    m_name = name;
    m_gameImageLocation = gameImageLocation;
    m_owned = owned;
  }

  /**
   * Set the isOwned.
   * @param owned The isOwned to set
   */
  public void setOwned(boolean owned) {
    m_owned = owned;
  }

  /**
   * Get the owned.
   * @return Returns the owned
   */
  public boolean isOwned() {
    return m_owned;
  }

  /**
   * Set the name.
   * @param name The name to set
   */
  public void setName(String name) {
    m_name = name;
  }

  /**
   * Get the name.
   * @return Returns the name
   */
  public String getName() {
    return m_name;
  }

  /**
   * @return the gameImageLocation
   */
  public String getGameImageLocation() {
      return m_gameImageLocation;
  }

  /**
   * @param gameImageLocation the gameImageLocation to set
   */
  public void setGameImageLocation(String gameImageLocation) {
      m_gameImageLocation = gameImageLocation;
  }

  /**
   * toString overridden for json purposes
   */
  @Override
  public String toString() {
    return "XboxGame [name=" + getName() + ", owned=" + isOwned() + ", id=" + getId() + "]";
  }
}
