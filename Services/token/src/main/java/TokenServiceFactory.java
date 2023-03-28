import io.quarkus.runtime.Startup;
import messaging.implementations.RabbitMqQueue;

import javax.enterprise.context.ApplicationScoped;

/**
 * based on example code
 */
public class TokenServiceFactory {
	static TokenService service = null;

	@Startup
	@ApplicationScoped
	public TokenService getService() {
		if (service != null) {
			return service;
		}
		var mq = new RabbitMqQueue("rabbitmq");
		service = new TokenService(mq);
		return service;
	}
}
