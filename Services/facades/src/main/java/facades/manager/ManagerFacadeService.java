/**
 *
 * @author Hjalte Bøgehave
 *
 */

package facades.manager;

import domain.CorrelationId;
import domain.manager.ManagerResponse;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static messaging.domain.EventTypeResource.MANAGER_REPORT_REQUEST;
import static messaging.domain.EventTypeResource.MANAGER_REPORT_RESPONSE;

public class ManagerFacadeService {

    private final Map<CorrelationId, CompletableFuture<ManagerResponse>> managerCorrelations = new ConcurrentHashMap<>();
    MessageQueue queue;
    public ManagerFacadeService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(MANAGER_REPORT_RESPONSE, this::handleManagerReportResponse);
    }

    public ManagerResponse requestGetManagerReport() {
        var correlationId = CorrelationId.randomId();
        managerCorrelations.put(correlationId,new CompletableFuture<>());
        Event event = new Event(MANAGER_REPORT_REQUEST, new Object[] { correlationId });
        queue.publish(event);
        System.out.println("Manager report requested");
        return managerCorrelations.get(correlationId).join();
    }

    public void handleManagerReportResponse(Event event) {
        var correlationId = event.getArgument(1, CorrelationId.class);
        var response = event.getArgument(0, ManagerResponse.class);
        managerCorrelations.get(correlationId).complete(response);
    }
}
