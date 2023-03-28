import io.quarkus.runtime.Startup;
import lombok.Getter;
import messaging.implementations.RabbitMqQueue;

import javax.enterprise.context.ApplicationScoped;
/**
 * based on example code by Hubert
 */
public class ReportServiceFactory {

    static ReportService service = null;

    @Startup
    @ApplicationScoped
    public ReportService startService() {
        if (service != null) return service;
        var mq = new RabbitMqQueue("rabbitmq");
        service = new ReportService(mq);
        return service;
    }

}
