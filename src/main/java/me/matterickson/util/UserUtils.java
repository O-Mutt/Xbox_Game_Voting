package me.matterickson.util;

import java.util.Calendar;
import java.util.Date;

import me.matterickson.model.UserEntity;
import me.matterickson.model.Vote;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Matt@MattErickson.ME
 */
public class UserUtils {
  /**
   * Helper method for checking if a user can vote today
   * @param user the user tha is voting
   * @return boolean, true if user CAN vote else false
   */
  public static boolean userCanVoteToday(UserEntity user) {
    if (user == null) {
      return false;
    } else if (user.getId() == 0) {
      return false;
    }
    Date today = new Date();

    if (CollectionUtils.isEmpty(user.getVotes()) && isWeekday()) {
      return true;
    }
    if (!isWeekday()) {
      return false;
    }

    if (CollectionUtils.isNotEmpty(user.getVotes()) && isWeekday()) {
      for (Vote vote : user.getVotes()) {
        if (DateUtils.isSameDay(today, vote.getVoteDate())) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Helper method to find if today is a weekday
   * @return boolean true if the day is a weekday otherwise false
   */
  public static boolean isWeekday() {
    Calendar cal = Calendar.getInstance();
    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      return false; //TODO this is for testing on the weekends!!! should return false
    } else {
      return true;//TODO this is for testing on the weekends!!! should return true
    }
  }
}
