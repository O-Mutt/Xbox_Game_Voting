package me.matterickson.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import me.matterickson.business.XboxGameService;
import me.matterickson.model.UserEntity;
import me.matterickson.model.Vote;
import me.matterickson.model.XboxGame;
import me.matterickson.util.UserUtils;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


/**
 * Class containing all methods for dealing with xbox games
 * @author Matt@MattErickson.me
 */
@SuppressWarnings("serial")
public class GamesAction extends ActionSupport {
  private static final Logger s_log = LoggerFactory.getLogger(GamesAction.class);
  //Lists for games to be displayed
  private List<XboxGame> m_ownedGames = Lists.newArrayList();
  private Map<Integer, List<XboxGame>> m_unownedGames = Maps.newLinkedHashMap();

  //Business layer service
  private XboxGameService m_gameService = new XboxGameService();

  //Id for voting for the game
  private long m_voteGameId;
  private String m_voteGameName;

  //Adding a new game
  private String m_addGameName;
  private boolean m_addGameOwned = false;
  private File m_addGameImage;
  private String m_addGameImageContentType;
  private String m_addGameImageFileName;
  /**
   * Populates all games into respective lists/maps to show them on the main pages
   * @return string forward
   */
  @Action(value = "xboxGames", results = {
    @Result(name = "success", location = "games.jsp"),
    @Result(name = "input", location = "games.jsp")
  })
  public String listGames() {
    UserEntity me = (UserEntity) ActionContext.getContext().getSession().get("user");
    s_log.debug("Show game list");
    populateGames(me);
    return SUCCESS;
  }

  /**
   * Action for voting for the game.  User must be logged in before they can vote
   * @return string forward
   */
  @Action(value = "voteForGame", results = {
         @Result(name = "success", location = "games.jsp"),
         @Result(name = "input", location = "games.jsp"),
         @Result(name = "error", location = "games.jsp")
      })
  public String voteForGame() {
    UserEntity me = (UserEntity) ActionContext.getContext().getSession().get("user");
    try {
      s_log.debug("Try load user from session");
      Vote vote = m_gameService.voteForXboxGame(m_voteGameId, me, null);
      if (vote != null) {
        addActionMessage("Your vote for " + m_voteGameName + " was successful");
        populateGames(me);
        return SUCCESS;
      } else {
        if (!UserUtils.userCanVoteToday(me)) {
          addActionError("You seem to have already voted today for a different game or it is the weekend, get outside!");
          populateGames(me);
          return ERROR;
        } else {
          addActionError("There was a problem voting for " + m_addGameName + ".  Please try again later.");
          populateGames(me);
          return ERROR;
        }
      }
    } catch (Exception e) {
      s_log.debug("Failed to load user from session =(");
      addActionError("Voting for " + m_voteGameName + " failed. Please try again later.");
      populateGames(me);
      return ERROR;
    }
  }

  /**
   * Action for adding game.
   * @return string forward
   */
  @Action(value = "addGame", results = {
          @Result(name = "success", location = "games.jsp"),
          @Result(name = "error", location = "games.jsp")
      })
  public String addGame() {
    UserEntity me = (UserEntity) ActionContext.getContext().getSession().get("user");
    XboxGame xgame = m_gameService.getXboxGameByName(m_addGameName);

    if (xgame != null && xgame.isOwned()) {
      addActionError("Sorry, we actually already own " + m_addGameName);
      populateGames(me);
      return ERROR;
    } else if (xgame != null && !xgame.isOwned()) {
      addActionError("Sorry, " + m_addGameName + " has already been added.  Please vote for " + m_addGameName + " on the unowned games page.");
      populateGames(me);
      return ERROR;
    }
    if (m_addGameImage != null) {
      try {
        ServletContext context = ServletActionContext.getServletContext();
        String filePath = context.getRealPath("/WEB-INF/images/");
        File gameImageFile = new File(filePath, m_addGameImageFileName);
        FileUtils.copyFile(m_addGameImage, gameImageFile);
        xgame = new XboxGame(m_addGameName, "/WEB-INF/images/" + m_addGameImageFileName, m_addGameOwned);
      } catch (FileNotFoundException e) {} catch (IOException e) {}
    } else {
      xgame = new XboxGame(m_addGameName, m_addGameOwned);
    }

    xgame = m_gameService.addXboxGame(xgame, me);
    if (xgame == null) {
      if (!UserUtils.userCanVoteToday(me)) {
        addActionError("You seem to have already voted today for a different game or it is the weekend, get outside!");
        populateGames(me);
        return ERROR;
      } else {
        addActionError("There was a problem adding " + m_addGameName + ".  Please try again later.");
        populateGames(me);
        return ERROR;
      }
    }
    String secondPartOfSentence = "";
    if (!m_addGameOwned) {
      secondPartOfSentence = " and your vote was cast for it. Please see the \"Unowned Game\" tab.";
    } else {
      secondPartOfSentence = ". Please see the \"Owned Games\" tab.";
    }
    addActionMessage(m_addGameName + " was successfully added" + secondPartOfSentence);
    m_addGameName = "";//This field was persisting for some odd reason
    populateGames(me);
    return SUCCESS;
  }

  /**
   * Action for voting for the game.  User must be logged in before they can vote
   * @return string forward
   */
  @Action(value = "markAsOwned", results = {
      @Result(name = "success", location = "games.jsp"),
      @Result(name = "input", location = "games.jsp"),
      @Result(name = "error", location = "games.jsp")
   })
  public String markGameAsOwned() {
    try {
      m_gameService.setXboxGameAsOwned(m_voteGameId);
      addActionMessage(m_voteGameName + " was successfully added to \"Owned Games\"");
      populateGames(null);
      return SUCCESS;
    } catch (Exception e) {
      populateGames(null);
      addActionMessage("Could not add " + m_voteGameName + " to \"Owned Games\"");
      return ERROR;
    }
  }

  /**
   * Populate helper
   */
  public void populateGames(UserEntity user) {
    if (user == null) {

    }
    m_ownedGames.addAll(m_gameService.getAllOwnedXboxGames());
    m_unownedGames.putAll(m_gameService.getAllUnownedXboxGames());
  }

  /**
   * All following getters and setters are for struts2 auto population purposes
   */
  /**
   * @return the ownedGames
   */
  public List<XboxGame> getOwnedGames() {
    return m_ownedGames;
  }

  /**
   * @param ownedGames the ownedGames to set
   */
  public void setOwnedGames(List<XboxGame> ownedGames) {
    m_ownedGames = ownedGames;
  }

  /**
   * @return the unownedGames
   */
  public Map<Integer, List<XboxGame>> getUnownedGames() {
    return m_unownedGames;
  }

  /**
   * @param unownedGames the unownedGames to set
   */
  public void setUnownedGames(Map<Integer, List<XboxGame>> unownedGames) {
    m_unownedGames = unownedGames;
  }

  /**
   * Set the voteGameId.
   * @param voteGameId The voteGameId to set
   */
  public void setVoteGameId(long voteGameId) {
    m_voteGameId = voteGameId;
  }

  /**
   * Get the voteGameId.
   * @return Returns the voteGameId
   */
  public long getVoteGameId() {
    return m_voteGameId;
  }

  /**
   * @return the gameService
   */
  public XboxGameService getGameService() {
    return m_gameService;
  }

  /**
   * @param gameService the gameService to set
   */
  public void setGameService(XboxGameService gameService) {
    m_gameService = gameService;
  }

  /**
   * @return the voteGameName
   */
  public String getVoteGameName() {
    return m_voteGameName;
  }

  /**
   * @param voteGameName the voteGameName to set
   */
  public void setVoteGameName(String voteGameName) {
    m_voteGameName = voteGameName;
  }

  /**
   * @return the addGameName
   */
  public String getAddGameName() {
    return m_addGameName;
  }

  /**
   * @param addGameName the addGameName to set
   */
  public void setAddGameName(String addGameName) {
    m_addGameName = addGameName;
  }

  /**
   * @return the addGameOwned
   */
  public boolean isAddGameOwned() {
    return m_addGameOwned;
  }

  /**
   * @param addGameOwned the addGameOwned to set
   */
  public void setAddGameOwned(boolean addGameOwned) {
    m_addGameOwned = addGameOwned;
  }

  /**
   * @return the addGameImage
   */
  public File getAddGameImage() {
    return m_addGameImage;
  }

  /**
   * @param addGameImage the addGameImage to set
   */
  public void setAddGameImage(File addGameImage) {
    m_addGameImage = addGameImage;
  }

  /**
   * @return the addGameImageContentType
   */
  public String getAddGameImageContentType() {
    return m_addGameImageContentType;
  }

  /**
   * @param addGameImageContentType the addGameImageContentType to set
   */
  public void setAddGameImageContentType(String addGameImageContentType) {
    m_addGameImageContentType = addGameImageContentType;
  }

  /**
   * @return the addGameImageFileName
   */
  public String getAddGameImageFileName() {
    return m_addGameImageFileName;
  }

  /**
   * @param addGameImageFileName the addGameImageFileName to set
   */
  public void setAddGameImageFileName(String addGameImageFileName) {
    m_addGameImageFileName = addGameImageFileName;
  }
}
