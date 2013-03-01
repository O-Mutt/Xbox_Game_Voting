package me.matterickson.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Database model representation of a vote for a game.
 * Contains a reference to a game and the user who voted for it
 * @author Matt@MattErickson.me
 */
@Entity
public class Vote extends PersistedEntity {

  private XboxGame m_xboxGame;
  private UserEntity m_user;
  private Date m_voteDate;

  /**
   * Default Constructor
   */
  public Vote() {
  }

  /**
   * Constructor which takes the game into it sets the vote date to today
   * @param game the game that was voted for
   * @param user the user that voted for the game
   */
  public Vote(XboxGame game, UserEntity user) {
    m_voteDate = new Date();
    m_xboxGame = game;
    m_user = user;
  }

  /**
   * @return the voteDate
   */
  public Date getVoteDate() {
    return m_voteDate;
  }

  /**
   * @param m_voteDate the voteDate to set
   */
  public void setVoteDate(Date voteDate) {
    m_voteDate = voteDate;
  }

  /**
   * @return the user
   */
  @ManyToOne(fetch = FetchType.EAGER)
  public UserEntity getUser() {
    return m_user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(UserEntity user) {
    m_user = user;
  }


  /**
   * Get the xboxGame.
   * @return Returns the xboxGame
   */
  @ManyToOne(fetch = FetchType.EAGER)
  public XboxGame getXboxGame() {
    return m_xboxGame;
  }

  /**
   * Set the xboxGame.
   * @param xboxGame The xboxGame to set
   */
  public void setXboxGame(XboxGame xboxGame) {
    m_xboxGame = xboxGame;
  }
}
