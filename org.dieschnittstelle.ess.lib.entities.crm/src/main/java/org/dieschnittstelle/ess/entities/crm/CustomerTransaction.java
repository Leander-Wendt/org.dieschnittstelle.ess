package org.dieschnittstelle.ess.entities.crm;

import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class CustomerTransaction implements Serializable {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(CustomerTransaction.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1251851309422364868L;

	@Id
	@GeneratedValue
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
	private Date date;

	@ManyToOne
	private Customer customer;

	@ManyToOne
	private AbstractTouchpoint touchpoint;

	/*
	 * UE JPA1.1
	 */
	@OneToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
	//@OneToMany
	private List<CustomerTransactionShoppingCartItem> items = new ArrayList<CustomerTransactionShoppingCartItem>();

	private int value;

	private boolean completed;
	
	public CustomerTransaction() {
		logger.debug("<constructor>");
	}
	
	public CustomerTransaction(Customer customer,AbstractTouchpoint tp,List<CustomerTransactionShoppingCartItem> products) {
		this.customer = customer;
		this.touchpoint = tp;
		this.items = products;
		this.date = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public AbstractTouchpoint getTouchpoint() {
		return touchpoint;
	}

	public void setTouchpoint(AbstractTouchpoint touchpoint) {
		this.touchpoint = touchpoint;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String toString() {
		return "<CustomerTransaction " + this.id + " " + this.customer + " "
				+ this.touchpoint + ", " + this.items + ">";
	}

	public List<CustomerTransactionShoppingCartItem> getItems() {
		return items;
	}

	public void setItems(List<CustomerTransactionShoppingCartItem> items) {
		this.items = items;
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
