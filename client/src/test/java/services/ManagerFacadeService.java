/**
 *
 * @author Julia Makulec
 *
 */
package services;

/**
 *
 * @author Hassan Kassem | git: stonebank | student: s205409
 *
 *
 */

import domain.responses.ManagerResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class ManagerFacadeService {

    private final Client client = ClientBuilder.newClient();
    private final WebTarget managerTarget = client.target("http://localhost:8000/manager");

    public ManagerResponse getManagerReportResponse() {
        return managerTarget.request().get().readEntity(ManagerResponse.class);
    }
}
