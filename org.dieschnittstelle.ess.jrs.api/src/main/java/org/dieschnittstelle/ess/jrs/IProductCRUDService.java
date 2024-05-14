package org.dieschnittstelle.ess.jrs;

import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import java.util.List;

/*
 * UE JRS2:
 * deklarieren Sie hier Methoden fuer:
 * - die Erstellung eines Produkts
 * - das Auslesen aller Produkte
 * - das Auslesen eines Produkts
 * - die Aktualisierung eines Produkts
 * - das Loeschen eines Produkts
 * und machen Sie diese Methoden mittels JAX-RS Annotationen als WebService verfuegbar
 */

/*
 * TODO JRS3: aendern Sie Argument- und Rueckgabetypen der Methoden von IndividualisedProductItem auf AbstractProduct
 */
public interface IProductCRUDService {

    IndividualisedProductItem createProduct(IndividualisedProductItem prod);

    List<IndividualisedProductItem> readAllProducts();

    IndividualisedProductItem updateProduct(long id,
                                            IndividualisedProductItem update);

    boolean deleteProduct(long id);

    IndividualisedProductItem readProduct(long id);

}
