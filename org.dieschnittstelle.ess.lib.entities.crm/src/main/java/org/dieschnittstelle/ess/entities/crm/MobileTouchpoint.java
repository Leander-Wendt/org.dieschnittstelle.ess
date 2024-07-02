package org.dieschnittstelle.ess.entities.crm;

import java.util.Collection;
import java.util.HashSet;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import org.apache.logging.log4j.Logger;

@Entity
@DiscriminatorValue("mobile")
public class MobileTouchpoint extends AbstractTouchpoint {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(MobileTouchpoint.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3020587110269172721L;

	@ElementCollection
	private Collection<String> mobilePhoneIds = new HashSet<String>();

	public MobileTouchpoint() {
		logger.debug("<constructor>");
	}

	public MobileTouchpoint(String mobilePhoneId) {
		this.addMobilePhoneId(mobilePhoneId);
	}

	public Collection<String> getMobilePhoneIds() {
		return mobilePhoneIds;
	}

	public void addMobilePhoneId(String mobilePhoneId) {
		this.mobilePhoneIds.add(mobilePhoneId);
	}

	public String toString() {
		return "<MobileTouchpoint " + this.id + "/" + this.erpPointOfSaleId + " " + this.mobilePhoneIds + ">";
	}

	/*
	 * lifecycle logging
	 */
	
	@PostLoad
	public void onPostLoad() {
		logger.info("@PostLoad: " + this);
	}
	
	@PostPersist
	public void onPostPersist() {
		logger.info("@PostPersist: " + this);		
	}
	
	@PostRemove
	public void onPostRemove() {
		logger.info("@PostRemove: " + this);
	}

	@PostUpdate
	public void onPostUpdate() {
		logger.info("@PostUpdate: " + this);
	}
	
	@PrePersist
	public void onPrePersist() {
		logger.info("@PrePersist: " + this);
	}

	@PreRemove
	public void onPreRemove() {
		logger.info("@PreRemove: " + this);
	}

	@PreUpdate
	public void onPreUpdate() {
		logger.info("@PreUpdate: " + this);		
	}
	
}
