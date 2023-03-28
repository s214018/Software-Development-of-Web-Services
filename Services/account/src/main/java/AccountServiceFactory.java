import io.quarkus.runtime.Startup;
import messaging.implementations.RabbitMqQueue;

import javax.enterprise.context.ApplicationScoped;

/**
 * based on example code by Hubert Baumeister
 */
public class AccountServiceFactory {
	static AccountService service = null;

	@Startup
	@ApplicationScoped
	public AccountService getService() {
		if (service != null) {
			return service;
		}
		var mq = new RabbitMqQueue("rabbitmq");
		service = new AccountService(mq);
		return service;
	}
}
