package ci.mobio;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Path("/calcul")
@ApplicationScoped
@Transactional
public class ServeurCalculService {

    @Inject
    EntityManager em;

    // === Addition ===
    @GET
    @Path("/add")
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@QueryParam("a") double a, @QueryParam("b") double b) {
        double res = a + b;
        String result = String.valueOf(res);
        saveOperation("Addition", "a=" + a + ", b=" + b, result);
        return result;
    }

    // === Soustraction ===
    @GET
    @Path("/sub")
    @Produces(MediaType.TEXT_PLAIN)
    public String sub(@QueryParam("a") double a, @QueryParam("b") double b) {
        double res = a - b;
        String result = String.valueOf(res);
        saveOperation("Soustraction", "a=" + a + ", b=" + b, result);
        return result;
    }

    // === Multiplication ===
    @GET
    @Path("/mul")
    @Produces(MediaType.TEXT_PLAIN)
    public String mul(@QueryParam("a") double a, @QueryParam("b") double b) {
        double res = a * b;
        String result = String.valueOf(res);
        saveOperation("Multiplication", "a=" + a + ", b=" + b, result);
        return result;
    }

    // === Division ===
    @GET
    @Path("/div")
    @Produces(MediaType.TEXT_PLAIN)
    public String div(@QueryParam("a") double a, @QueryParam("b") double b) {
        if (b == 0) {
            throw new WebApplicationException("Division par zéro interdite", 400);
        }
        double res = a / b;
        String result = String.valueOf(res);
        saveOperation("Division", "a=" + a + ", b=" + b, result);
        return result;
    }

    // === Equation du 1er degré ===
@GET
@Path("/eq1")
@Produces(MediaType.TEXT_PLAIN)
public String eq1(@QueryParam("a") double a, @QueryParam("b") double b) {
    String result;
    if (a == 0) {
        result = (b == 0) ? "Infinité de solutions" : "Pas de solution";
    } else {
        result = "X = " + (-b / a);
    }
    saveOperation("Equation du 1er degré", "a=" + a + ", b=" + b, result);
    return result;
}

// === Equation du 2ème degré ===
@GET
@Path("/eq2")
@Produces(MediaType.TEXT_PLAIN)
public String eq2(@QueryParam("a") double a, @QueryParam("b") double b, @QueryParam("c") double c) {
    String result;
    double delta = b * b - 4 * a * c;

    if (a == 0) {
        result = "Pas une équation du 2e degré";
    } else if (delta < 0) {
        result = "Pas de solution réelle";
    } else if (delta == 0) {
        result = "X = " + (-b / (2 * a));
    } else {
        double x1 = (-b - Math.sqrt(delta)) / (2 * a);
        double x2 = (-b + Math.sqrt(delta)) / (2 * a);
        result = "X1 = " + x1 + "; X2 = " + x2;
    }

    saveOperation("Equation du 2ème degré", "a=" + a + ", b=" + b + ", c=" + c, result);
    return result;
}

    // === Historique des opérations ===
@GET
@Path("/history")
@Produces(MediaType.TEXT_PLAIN)
public String history() {
    List<Operation> ops = em.createQuery("SELECT o FROM Operation o ORDER BY o.createdAt DESC", Operation.class)
                            .getResultList();

    return ops.stream()
              .map(o -> o.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd -- HH:mm:ss")) 
                        + " - " + o.getType() + " : " + o.getInput() + " = " + o.getResult())
              .collect(Collectors.joining("\n"));
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
