package org.example.gestionproduit.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.example.gestionproduit.entity.CartItem;

import java.util.List;

public class StripePaymentService {

    static {
        Stripe.apiKey = "sk_test_51RGHGC05PMDh0bJDMCOFkklMmrxTPxmhgzuSi9AYgGijxnPthBoe20hEe1jyd1XZD20I3jID3Tm41qwDEXlfzgr800Bn6y7jly";
    }

    public static String createStripeSession(List<CartItem> cart) throws Exception {
        List<SessionCreateParams.LineItem> lineItems = cart.stream().map(item ->
                SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getQuantity())
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount((long) (item.getProduit().getPrix() * 100))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduit().getNom())
                                                        .build()
                                        ).build()
                        ).build()
        ).toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addAllLineItem(lineItems)
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // ⬅ We'll use this in WebView
    }
}
