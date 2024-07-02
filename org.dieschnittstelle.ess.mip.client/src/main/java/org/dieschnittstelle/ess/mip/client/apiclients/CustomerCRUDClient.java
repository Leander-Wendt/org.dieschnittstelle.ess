package org.dieschnittstelle.ess.mip.client.apiclients;

import org.dieschnittstelle.ess.mip.components.crm.crud.api.CustomerCRUD;
import org.dieschnittstelle.ess.entities.crm.Customer;

public class CustomerCRUDClient implements CustomerCRUD {

	private CustomerCRUD serviceProxy;

	public CustomerCRUDClient() throws Exception {
		serviceProxy = ServiceProxyFactory.getInstance().getProxy(CustomerCRUD.class);
	}

	@Override
	public Customer readCustomerForEmail(String email) {
		return serviceProxy.readCustomerForEmail(email);
	}

	@Override
	public Customer createCustomer(Customer customer) {
		Customer created = serviceProxy.createCustomer(customer);
		
		// as a side-effect, we set the id on the customer object
		customer.setId(created.getId());
		// we also set the id of the address, which might have been initially created, as a side-effect
		// avoid errors in case no address is specified
		if (customer.getAddress() != null) {
			customer.getAddress().setId(created.getAddress().getId());
		}

		return created;
	}

	@Override
	public Customer readCustomer(long id) {
		return serviceProxy.readCustomer(id);
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return serviceProxy.updateCustomer(customer);
	}

	@Override
	public Customer updateCustomerWithSleep(Customer customer, long sleep) {
		return serviceProxy.updateCustomerWithSleep(customer, sleep);
	}

	@Override
	public boolean deleteCustomer(int id) {
		return serviceProxy.deleteCustomer(id);
	}

}
