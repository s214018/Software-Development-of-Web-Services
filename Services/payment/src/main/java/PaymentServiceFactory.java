import io.quarkus.runtime.Startup;
import messaging.implementations.RabbitMqQueue;

import javax.enterprise.context.ApplicationScoped;

/**
 * based on example code by Hubert
 */
public class PaymentServiceFactory {
	static PaymentService service = null;

	@Startup
	@ApplicationScoped
	public PaymentService getService() {
		if (service != null) {
			return service;
		}
		var mq = new RabbitMqQueue("rabbitmq");
		service = new PaymentService(mq);
		return service;
	}
}
