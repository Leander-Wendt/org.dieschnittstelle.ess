package org.dieschnittstelle.ess.entities.crm;

import java.io.Serializable;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;

/**
 * a product bundle on the part of the crm system that tracks the number of
 * units for some erpProductId and also tracks wheher the product is a campaign
 *
 * for separating the SHOPPING domain in a separate schema from the CRM domain,
 * we need two classes, as otherwise database constraints will be violated on
 * schema creation starting from some higher h2 db versions
 */
@Entity
public class CustomerTransactionShoppingCartItem implements Serializable {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(CustomerTransactionShoppingCartItem.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 5027719621777767575L;

	@Id
	@GeneratedValue
	private long id;

	private long erpProductId;

	private int units;

	private boolean isCampaign;
	
	@Transient
	@JsonIgnore
	@JsonbTransient
	private AbstractProduct productObj;
	
	public CustomerTransactionShoppingCartItem() {
		logger.debug("<constructor>");
	}
	
	public CustomerTransactionShoppingCartItem(long erpProductId, int units) {
		this(erpProductId, units, false);
	}

	public CustomerTransactionShoppingCartItem(long erpProductId, int units, boolean isCampaign) {
		this.erpProductId = erpProductId;
		this.units = units;
		this.isCampaign = isCampaign;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setErpProductId(long erpProductId) {
		this.erpProductId = erpProductId;
	}

	public void setCampaign(boolean campaign) {
		isCampaign = campaign;
	}

	public long getId() {
		return id;
	}

	public long getErpProductId() {
		return erpProductId;
	}

	public int getUnits() {
		return units;
	}
	
	public void setUnits(int units) {
		this.units = units;
	}

	public boolean isCampaign() {
		return isCampaign;
	}

	public String toString() {
		return "<ShoppingCartItem " + this.id + " (" + this.erpProductId + ":"
				+ this.units + ")>";
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

	public AbstractProduct getProductObj() {
		if (productObj == null) {
			throw new RuntimeException("ShoppingCartItem.productObj is transient and has not been set! Use erpProductId to access product.");
		}
		return productObj;
	}

	public void setProductObj(AbstractProduct productObj) {
		this.productObj = productObj;
	}

}
