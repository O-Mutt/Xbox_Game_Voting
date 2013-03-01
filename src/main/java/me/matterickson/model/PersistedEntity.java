package me.matterickson.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Mapped super class for entities so we always have a id
 * @author Matt@MattErickson.me
 */
@MappedSuperclass
public class PersistedEntity implements Entity {
  private long m_id;

  /**
   * @param id the id to set
   */
  public void setId(long id) {
    m_id = id;
  }

  @Override
  @Id @XmlTransient
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long getId() {
    return m_id;
  }
}
