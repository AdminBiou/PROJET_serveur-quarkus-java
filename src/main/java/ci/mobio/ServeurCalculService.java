package ci.mobio;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@Path("/calcul")
@ApplicationScoped
public class ServeurCalculService {

    @Inject
    EntityManager em;

    // === Addition ===
    @GET
    @Path("/add")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String add(@QueryParam("a") double a, @QueryParam("b") double b) {
        double res = a + b;
        saveOperation("add", "a=" + a + ", b=" + b, String.valueOf(res));
        return String.valueOf(res);
    }

    // === Soustraction ===
    @GET
    @Path("/sub")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String sub(@QueryParam("a") double a, @QueryParam("b") double b) {
        double res = a - b;
        saveOperation("sub", "a=" + a + ", b=" + b, String.valueOf(res));
        return String.valueOf(res);
    }

    // === Multiplication ===
    @GET
    @Path("/mul")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String mul(@QueryParam("a") double a, @QueryParam("b") double b) {
        double res = a * b;
        saveOperation("mul", "a=" + a + ", b=" + b, String.valueOf(res));
        return String.valueOf(res);
    }

    // === Division ===
    @GET
    @Path("/div")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String div(@QueryParam("a") double a, @QueryParam("b") double b) {
        if (b == 0) {
            throw new WebApplicationException("Division par zéro interdite", 400);
        }
        double res = a / b;
        saveOperation("div", "a=" + a + ", b=" + b, String.valueOf(res));
        return String.valueOf(res);
    }

    // === Equation du 1er degré : ax + b = 0 ===
    @GET
    @Path("/eq1")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String eq1(@QueryParam("a") double a, @QueryParam("b") double b) {
        String result;
        if (a == 0) {
            result = (b == 0) ? "Infinité de solutions" : "Pas de solution";
        } else {
            result = String.valueOf(-b / a);
        }
        saveOperation("eq1", "a=" + a + ", b=" + b, result);
        return "x = " + (-b / a);
    }

    // === Equation du 2ème degré : ax² + bx + c = 0 ===
    @GET
    @Path("/eq2")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String eq2(@QueryParam("a") double a, @QueryParam("b") double b, @QueryParam("c") double c) {
        String result;
        double delta = b * b - 4 * a * c;

        if (a == 0) {
            result = "Pas une équation du 2e degré";
        } else if (delta < 0) {
            result = "Pas de solution réelle";
        } else if (delta == 0) {
            result = "x = " + (-b / (2 * a));
        } else {
            double x1 = (-b - Math.sqrt(delta)) / (2 * a);
            double x2 = (-b + Math.sqrt(delta)) / (2 * a);
            result = "x1 = " + x1 + ", x2 = " + x2;
        }

        saveOperation("eq2", "a=" + a + ", b=" + b + ", c=" + c, result);
        return result;
    }

// === Historique des opérations ===
    @GET
@Path("/history")
@Produces(MediaType.APPLICATION_JSON)
public List<Operation> history() {
    return em.createQuery(
            "SELECT o FROM Operation o ORDER BY o.createdAt DESC", Operation.class)
            .getResultList();
}


    // === Méthode utilitaire pour enregistrer en BD ===
    private void saveOperation(String type, String input, String result) {
        Operation op = new Operation();
        op.setType(type);
        op.setInput(input);
        op.setResult(result);
        em.persist(op);
    }
}
