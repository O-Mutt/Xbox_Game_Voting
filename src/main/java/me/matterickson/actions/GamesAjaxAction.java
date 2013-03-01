/**
 *
 */
package me.matterickson.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import me.matterickson.business.XboxGameService;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Matt@MattErickson.ME
 *
 */
@SuppressWarnings("serial")
@ParentPackage("json-default")
public class GamesAjaxAction extends ActionSupport {
  private XboxGameService m_gameService = new XboxGameService();
  private Long m_gameId;
  private InputStream m_inputStream;

  /**
   * Ajax method to grab the image file for the game.  If it is not found or blank it will return the default image
   * @return string forward
   * @throws FileNotFoundException if the file is not found
   */
  @Action(value = "/ajax/getImage", results =
    {
      @Result(name = "success", type = "stream",
              params = {"inputName", "inputStream",
                        "contentType", "image/jpg"})
      })
  public String getGameImage() throws FileNotFoundException {
    String path = m_gameService.getGameImageLocationById(m_gameId);
    String fullLocation;
    ServletContext context = ServletActionContext.getServletContext();
    if (StringUtils.isEmpty(path)) {
      fullLocation = context.getRealPath("/WEB-INF/images/default");
    } else {
      fullLocation = context.getRealPath(path);
    }
    InputStream inputStream;
    try {
    inputStream = new FileInputStream(fullLocation);
    } catch (Exception e) {
      /*
       * Catch all if for some reason a game image fails to load, load the default
       * this is usually caused by a mvn clean install (wiping the target directory
       * where the images are stored)
       */
      inputStream = new FileInputStream(context.getRealPath("/WEB-INF/images/default"));
    }
    m_inputStream = inputStream;
    return SUCCESS;
  }

  /**
   * @return the gameId
   */
  public Long getGameId() {
    return m_gameId;
  }

  /**
   * @param gameId the gameId to set
   */
  public void setGameId(Long gameId) {
    m_gameId = gameId;
  }

  /**
   * @return the inputStream
   */
  public InputStream getInputStream() {
    return m_inputStream;
  }

  /**
   * @param inputStream the inputStream to set
   */
  public void setInputStream(InputStream inputStream) {
    m_inputStream = inputStream;
  }
}