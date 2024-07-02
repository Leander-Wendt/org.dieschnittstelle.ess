package org.dieschnittstelle.ess.mip.components.crm.crud.impl;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.crm.CustomerTransaction;
import org.dieschnittstelle.ess.entities.crm.CustomerTransactionShoppingCartItem;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Logged
@ApplicationScoped
@Transactional
/*
 * MIP: in contrast to hibernate, openjpa implementation does not accept id comparison for entity attributes in jpql queries.
 * comparison must be done for the id attribute of the entity attribute
 */
public class CustomerTransactionCRUDImpl implements CustomerTransactionCRUD {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(CustomerTransactionCRUDImpl.class);

	@Inject
	@EntityManagerProvider.CRMDataAccessor
	private EntityManager em;

	@Override
	public boolean createTransaction(CustomerTransaction transaction) {
		// check whether the transaction fields are detached or not
		logger.info("createTransaction(): customer attached (before): "
				+ em.contains(transaction.getCustomer()));
		logger.info("createTransaction(): touchpoint attached (before): "
				+ em.contains(transaction.getTouchpoint()));
		/*
		 * UE JPA1.1
		 */
		// persist each bundle
//		for (ShoppingCartItem item : transaction.getItems()) {
//			logger.info("createTransaction(): will manually persist item: " + item);
//			em.persist(item);
//			logger.info("createTransaction(): persisted bundle: " + item);
//		}

		// persit the transaction
		em.persist(transaction);
				
		return true;
	}

	@Override
	public List<CustomerTransaction> readAllTransactionsForTouchpoint(
			long touchpointId) {
		// as there is a bidirectional association between AbstractTouchpoint and
		// CustomerTransaction, we can also read the transactions from the touchpoint
		// object itself
		AbstractTouchpoint touchpoint = em.find(AbstractTouchpoint.class, touchpointId);

		List<CustomerTransaction> trans = new ArrayList<>(touchpoint.getTransactions());

		logger.info("readAllTransactionsForTouchpoint(): transactions on touchpoint object are: "
				+ touchpoint.getTransactions());

		return trans;
	}

	@Override
	public List<CustomerTransaction> readAllTransactionsForCustomer(
			long customerId) {
		Query query = em
				.createQuery("SELECT DISTINCT t FROM CustomerTransaction t WHERE t.customer.id = "
						+ customerId);
		logger.info("readAllTransactionsForCustomer(): created query: " + query);

		List<CustomerTransaction> trans = query.getResultList();
		logger.info("readAllTransactionsForCustomer(): " + trans);
		logger.info("readAllTransactionsForCustomer(): class is: "
				+ (trans == null ? "<null pointer>" : String.valueOf(trans
						.getClass())));

		return trans;
	}

	@Override
	public List<CustomerTransaction> readAllTransactions() {
		return em.createQuery("SELECT DISTINCT t FROM CustomerTransaction t").getResultList();
	}

	@Override
	public List<CustomerTransaction> readAllTransactionsForProduct(long productId) {
		// this is an example for a jpql query using JOIN to express conditions on
		// entities to which entities are related via a ToMany relation
		Query query = em.createQuery("SELECT DISTINCT t FROM CustomerTransaction t JOIN t.items item WHERE item.erpProductId = " + productId);
		return query.getResultList();
	}

	@Override
	public List<Customer> readAllCustomersForProduct(long productId) {
		// this shows a jpql query with JOIN using the structured creation of queries
		// with CriteriaBuilder as an alternative to string concatenation
		// (note that this functionality could also have been brought about by calling
		// readAllTransactionsForProduct() and collection the customers from the transactions)

		// this is how the query would be created and executed using string concatenation
		if (false) {
			Query query = em.createQuery("SELECT DISTINCT t.customer FROM CustomerTransaction t JOIN t.items item WHERE item.erpProductId = " + productId);
			return query.getResultList();
		}

		// this is the same query using the criteria builder
		CriteriaBuilder cb = em.getCriteriaBuilder();

		// which type of data shall be retrieved by the query?
		CriteriaQuery<Customer> queryDescription = cb.createQuery(Customer.class);

		// FROM part of the query
		Root<CustomerTransaction> from = queryDescription.from(CustomerTransaction.class);
		// JOIN
		Join<CustomerTransaction, CustomerTransactionShoppingCartItem> joineditem = from.join("items");

		// CONDITION on the joined items, using a parameter, the value of which can be passed later on
		// (rather than passing the value directly to the equal() condition)
		ParameterExpression<Long> itemErpProductIdCondition = cb.parameter(Long.class);
		queryDescription.where(cb.equal(joineditem.get("erpProductId"),itemErpProductIdCondition));

		// SELECT part, which specifies which attribute of the selected CustomerTransactions
		// specified in FROM will be selected for the result set
		queryDescription.select(from.get("customer"));
		// specify that we want to have distinct results
		queryDescription.distinct(true);

		TypedQuery<Customer> actualQuery = em.createQuery(queryDescription);

		// specify the parameter value for the product id
		actualQuery.setParameter(itemErpProductIdCondition,productId);

		// execute the query
		logger.info("running criteria query: " + actualQuery);
		return actualQuery.getResultList();
	}

	@Override
	public List<CustomerTransaction> readAllTransactionsForTouchpointAndCustomer(
			long touchpointId, long customerId) {
		Query query = em
				.createQuery("SELECT DISTINCT t FROM CustomerTransaction t WHERE t.customer.id = "
						+ customerId
						+ " AND t.touchpoint.id = "
						+ touchpointId);
		logger.info("readAllTransactionsForTouchpointAndCustomer(): created query: "
				+ query);

		List<CustomerTransaction> trans = query.getResultList();
		logger.info("readAllTransactionsForTouchpointAndCustomer(): " + trans);
		logger.info("readAllTransactionsForTouchpointAndCustomer(): class is: "
				+ (trans == null ? "<null pointer>" : String.valueOf(trans
						.getClass())));

		return trans;
	}

}
