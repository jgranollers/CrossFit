package com.example.crudthymeilif.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @Value("${app.base.url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String getStripePublicKey() {
        return stripePublicKey;
    }

    /**
     * Crea un PaymentIntent per a pagament embegut amb Payment Element.
     * Usa pagament amb targeta explícit (sense redireccions automàtiques).
     */
    public PaymentIntent crearPaymentIntent(Long competicioId, String competicioNom, Double preuEuros, String usuariEmail) throws StripeException {
        long preuCentims = Math.round(preuEuros * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(preuCentims)
                .setCurrency("eur")
                .setReceiptEmail(usuariEmail)
                .setDescription("Inscripció: " + competicioNom)
                .addPaymentMethodType("card")
                .putMetadata("competicio_id", competicioId.toString())
                .build();

        return PaymentIntent.create(params);
    }

    /**
     * Recupera un PaymentIntent per verificar el pagament.
     */
    public PaymentIntent obtenirPaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Crea un PaymentMethod amb les dades de targeta i confirma el PaymentIntent server-side.
     */
    public PaymentIntent confirmarAmbTargeta(String paymentIntentId, String cardNumber, Long expMonth, Long expYear, String cvc) throws StripeException {
        PaymentMethodCreateParams pmParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.CardDetails.builder()
                        .setNumber(cardNumber)
                        .setExpMonth(expMonth)
                        .setExpYear(expYear)
                        .setCvc(cvc)
                        .build())
                .build();

        PaymentMethod pm = PaymentMethod.create(pmParams);

        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(pm.getId())
                .build();
        return intent.confirm(confirmParams);
    }

    /**
     * Crea una sessió de Stripe Checkout (mètode anterior, conservat per compatibilitat).
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

