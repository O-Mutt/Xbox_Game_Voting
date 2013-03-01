package me.matterickson.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * User representation for db
 * @author Matt@MattErickson.me
 */
@Entity
public class UserEntity extends PersistedEntity {

  private String m_username;
  private String m_password;

  private List<Vote> m_votes = Lists.newArrayList();

  /**
   * Default Constructor
   */
  public UserEntity() {
  }

  /**
   * @param username the username of the new user
   * @param password the password of the new user
   */
  public UserEntity(String username, String password) {
    m_username = username;
    m_password = password;

  }

  /**
   * Set the username.
   * @param username The username to set
   */
  public void setUsername(String username) {
    m_username = username;
  }

  /**
   * Get the username.
   * @return Returns the username
   */
  public String getUsername() {
    return m_username;
  }

  /**
   * Set the password.
   * @param password The password to set
   */
  public void setPassword(String password) {
    m_password = password;
  }

  /**
   * Get the password.
   * @return Returns the password
   */
  public String getPassword() {
    return m_password;
  }

  /**
   * @return the votes
   */
  @OneToMany(fetch = FetchType.EAGER)
  public List<Vote> getVotes() {
    return m_votes;
  }

  /**
   * @param votes the votes to set
   */
  public void setVotes(List<Vote> votes) {
    m_votes = votes;
  }

}
