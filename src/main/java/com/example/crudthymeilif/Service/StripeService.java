package com.example.crudthymeilif.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${app.base.url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Crea una sessió de Stripe Checkout per a la inscripció a una competició.
     *
     * @param competicioId   ID de la competició
     * @param competicioNom  Nom de la competició
     * @param preuEuros      Preu en euros (ex: 25.00)
     * @param usuariEmail    Email de l'usuari
     * @return URL de la sessió de Stripe Checkout
     */
    public String crearSessioCheckout(Long competicioId, String competicioNom, Double preuEuros, String usuariEmail) throws StripeException {
        long preuCentims = Math.round(preuEuros * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(usuariEmail)
                .setSuccessUrl(baseUrl + "/competiciones/" + competicioId + "/inscripcio/exit?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/competiciones/" + competicioId + "/inscripcio/cancelat")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(preuCentims)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Inscripció: " + competicioNom)
                                                                .setDescription("Inscripció a la competició de CrossFit")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("competicio_id", competicioId.toString())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * Recupera una sessió de checkout per verificar el pagament.
     */
    public Session obtenirSessio(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}
