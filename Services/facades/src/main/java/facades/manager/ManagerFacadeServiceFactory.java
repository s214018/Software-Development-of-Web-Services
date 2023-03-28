package facades.manager;

import messaging.implementations.RabbitMqQueue;

/**
 * based on example code by Hubert
 */
public class ManagerFacadeServiceFactory {
	static ManagerFacadeService service = null;
	
	public ManagerFacadeService getService() {
		if (service != null) {
			return service;
		}

		var mq = new RabbitMqQueue("rabbitmq");
		service = new ManagerFacadeService(mq);
		return service;
	}
}
