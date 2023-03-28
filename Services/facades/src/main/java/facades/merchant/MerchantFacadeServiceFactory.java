package facades.merchant;

import messaging.implementations.RabbitMqQueue;
/**
 *
 * @author Julia Makulec
 *
 **/
public class MerchantFacadeServiceFactory {
	static MerchantFacadeService service = null;
	
	public MerchantFacadeService getService() {
		if (service != null) {
			return service;
		}
		var mq = new RabbitMqQueue("rabbitmq");
		service = new MerchantFacadeService(mq);
		return service;
	}
}
