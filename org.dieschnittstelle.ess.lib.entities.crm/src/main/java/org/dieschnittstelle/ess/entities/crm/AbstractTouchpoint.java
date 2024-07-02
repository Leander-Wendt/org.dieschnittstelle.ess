package org.dieschnittstelle.ess.entities.crm;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.GenericCRUDEntity;
import org.dieschnittstelle.ess.utils.jsonb.JsonbJsonTypeInfoHandler;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import static org.dieschnittstelle.ess.utils.jsonb.JsonbJsonTypeInfoHandler.KLASSNAME_PROPERTY;

/**
 * this is an abstraction over different touchpoints (with pos being the most
 * prominent one, others may be a service center, website, appsite, etc.)
 * 
 * @author kreutel
 * 
 */
// jpa annotations
@Entity
// inheritance
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="touchpointType", discriminatorType=DiscriminatorType.STRING)
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS);
//@Inheritance(strategy=InheritanceType.JOINED);

// do not use the sequence generator for the time being
//@SequenceGenerator(name = "touchpoint_sequence", sequenceName = "touchpoint_id_sequence")

// jrs/jackson annotations
// note that the value of the constant KLASSNAME_PROPERTY in this implementation is "@class". It
// specifies the name of the json property the value of which will be the classname of the respective
// concrete subclass of the abstract class, and thus allows to create correctly typed java objects
// based on the untyped json data.
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property=KLASSNAME_PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
// jsonb annotations
@JsonbTypeDeserializer(JsonbJsonTypeInfoHandler.class)
@JsonbTypeSerializer(JsonbJsonTypeInfoHandler.class)
@Schema(name="AbstractTouchpoint")
public abstract class AbstractTouchpoint implements Serializable, GenericCRUDEntity {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(AbstractTouchpoint.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 5207353251688141788L;

	
	@Id
	@GeneratedValue// for the time being, we do not use the sequence generator: (strategy = GenerationType.SEQUENCE, generator = "touchpoint_sequence")
	protected long id;

	/**
	 * the id of the PointOfSale from the erp data
	 */
	protected long erpPointOfSaleId;

	/**
	 * the name of the touchpoint
	 */
	protected String name;

	/*
	 * TODO JWS2: kommentieren Sie @XmlTransient aus
	 */
	@ManyToMany
	@JsonbTransient
	private Collection<Customer> customers = new HashSet<Customer>();
	
	@OneToMany(mappedBy="touchpoint")
	@JsonbTransient
	private Collection<CustomerTransaction> transactions;


	public AbstractTouchpoint() {
		logger.debug("<constructor>");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getErpPointOfSaleId() {
		return erpPointOfSaleId;
	}

	public void setErpPointOfSaleId(long erpPointOfSaleId) {
		this.erpPointOfSaleId = erpPointOfSaleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// here, this annotation must be set on accessor methods rather than on the attributes themselves
	@JsonIgnore
	@JsonbTransient
	public Collection<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(HashSet<Customer> customers) {
		this.customers = customers;
	}

	public void addCustomer(Customer customer) {
		this.customers.add(customer);
	}

	public boolean equals(Object obj) {

		if (obj == null || !(obj instanceof AbstractTouchpoint)) {
			return false;
		}

		return this.getId() == ((AbstractTouchpoint) obj).getId();
	}

	@JsonIgnore
	@JsonbTransient
	public Collection<CustomerTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Collection<CustomerTransaction> transactions) {
		this.transactions = transactions;
	}

}
