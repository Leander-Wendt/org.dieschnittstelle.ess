package org.dieschnittstelle.ess.entities.crm;

import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.persistence.*;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.utils.jsonb.JsonbJsonTypeInfoHandler;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author kreutel
 */
@Entity
@DiscriminatorValue("stationary")
@JsonbTypeSerializer(JsonbJsonTypeInfoHandler.class)
@Schema(name="StationaryTouchpoint")
public class StationaryTouchpoint extends AbstractTouchpoint  implements Serializable {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(StationaryTouchpoint.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -6123798695442913993L;
	
	/**
	 * we assume a OneToOne association: this is for demonstrating simple cascading of composition-type associations,
	 * see Customer->Address for a more intuitive handling of an "address" association as ManyToOne
	 */
	@OneToOne(cascade={CascadeType.ALL})
	@Schema(implementation = Address.class)
	private Address address;
	
	public StationaryTouchpoint() {
		logger.debug("<constructor>");
	}
	
	public StationaryTouchpoint(int erpPointOfSaleId) {
		this.setErpPointOfSaleId(erpPointOfSaleId);
	}

	public StationaryTouchpoint(int erpPointOfSaleId,String name,Address address) {
		super.setErpPointOfSaleId(erpPointOfSaleId);
		super.setName(name);
		this.setAddress(address);
	}
	
	public String toString() {
		return "<StationaryTouchpoint " + this.id + "/" + this.erpPointOfSaleId + " " + this.name + " " + this.address + ">";
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
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
