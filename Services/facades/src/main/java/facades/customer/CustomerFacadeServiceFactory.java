/**
 *
 * @author Julia Makulec
 *
 * based on example code by Hubert
 */
package facades.customer;

import messaging.implementations.RabbitMqQueue;


public class CustomerFacadeServiceFactory {
	static CustomerFacadeService service = null;
	
	public CustomerFacadeService getService() {
		if (service != null) {
			return service;
		}

		var mq = new RabbitMqQueue("rabbitmq");
		service = new CustomerFacadeService(mq);
		return service;
	}
}
