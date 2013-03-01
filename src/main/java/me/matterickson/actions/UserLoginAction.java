package me.matterickson.actions;

import java.util.Map;

import me.matterickson.business.UserService;
import me.matterickson.model.UserEntity;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Class that contains all methods for dealing with user accounts
 * @author Matt@MattErickson.me
 */
@SuppressWarnings("serial")
public class UserLoginAction extends ActionSupport implements SessionAware {
  private static final Logger s_log = LoggerFactory.getLogger(UserLoginAction.class);

  private Map<String, Object> m_sessionMap;
  private String m_returnAction;
  private String m_username;
  private String m_password;
  private UserService m_userService = new UserService();

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSession(Map<String, Object> map) {
    m_sessionMap = map;
  }


  /**
   * Action to login a user and store the user into the session
   * @return string forward
   */
  @Action(value = "login", results = {
     @Result(name = "success", location = "xboxGames.action", type = "redirectAction"),
     @Result(name = "input", location = "login.jsp"),
     @Result(name = "login", location = "login.jsp")
  })
  public String login()
  {
    s_log.debug("Start login action username: {} password: {}", m_username, m_password);
    if (StringUtils.isBlank(m_username) || StringUtils.isBlank(m_password)) {
      addActionError("Username and password are required fields");
      return "login";
    } else {
      UserEntity user = m_userService.getUserByUsername(m_username, m_password);
      if (user == null) {
        addActionError("There seems to be a problem logging you in. Please try again.");
        return INPUT;
      } else {
        m_sessionMap.put("user", user);
        m_sessionMap.put("loggedin", true);
        return SUCCESS;
      }
    }
  }

  /**
   * Action to log out the user and remove them from the session
   * @return string forward
   */
  @Action(value = "logout", results = {
    @Result(name = "success", location = "login.jsp")
  })
  public String logout()
  {
    m_sessionMap.remove("user");
    m_sessionMap.remove("loggedin");
    return SUCCESS;
  }

  /**
   * Get the sessionMap.
   * @return Returns the sessionMap
   */
  public Map<String, Object> getSessionMap() {
    return m_sessionMap;
  }

  /**
   * Get the returnAction.
   * @return Returns the returnAction
   */
  public String getReturnAction() {
    return m_returnAction;
  }

  /**
   * Get the username.
   * @return Returns the username
   */
  public String getUsername() {
    return m_username;
  }

  /**
   * Set the sessionMap.
   * @param sessionMap The sessionMap to set
   */
  public void setSessionMap(Map<String, Object> sessionMap) {
    m_sessionMap = sessionMap;
  }

  /**
   * Set the returnAction.
   * @param returnAction The returnAction to set
   */
  public void setReturnAction(String returnAction) {
    m_returnAction = returnAction;
  }

  /**
   * Set the username.
   * @param username The username to set
   */
  public void setUsername(String username) {
    m_username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return m_password;
  }


  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    m_password = password;
  }
}
