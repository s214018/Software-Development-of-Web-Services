/**
 *
 * @author Hjalte Bøgehave
 *
 */

package facades.manager;

import domain.manager.ManagerResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/manager")
public class ManagerFacade {
    ManagerFacadeService service = new ManagerFacadeServiceFactory().getService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ManagerResponse getManagerReport() { return service.requestGetManagerReport(); }
}
