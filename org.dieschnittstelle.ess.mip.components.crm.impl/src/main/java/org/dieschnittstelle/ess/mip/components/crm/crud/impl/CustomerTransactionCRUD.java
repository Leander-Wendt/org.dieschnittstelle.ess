package org.dieschnittstelle.ess.mip.components.crm.crud.impl;

import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.crm.CustomerTransaction;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

public interface CustomerTransactionCRUD {

	public boolean createTransaction(CustomerTransaction transaction);

	public List<CustomerTransaction> readAllTransactionsForTouchpoint(long touchpointId);

	public List<CustomerTransaction> readAllTransactionsForCustomer(long customerId);

	public List<CustomerTransaction> readAllTransactionsForTouchpointAndCustomer(long touchpointId, long customerId);

	public List<CustomerTransaction> readAllTransactions();

	public List<CustomerTransaction> readAllTransactionsForProduct(long productId);

	public List<Customer> readAllCustomersForProduct(long productId);

}
