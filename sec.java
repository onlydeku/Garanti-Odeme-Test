package com.example.garanti;

import com.ramaznacr.garanti.GarantiClient;
import com.ramaznacr.garanti.exceptions.GarantiException;
import com.ramaznacr.garanti.models.AccountBalance;
import com.ramaznacr.garanti.models.Transaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Örnek uygulama:
 * - OAuth2 token alıp
 * - Hesap bakiyesi ve hareketleri çekip
 * - Callback ile gelen veriyi işleme
 */
@SpringBootApplication
@RestController
public class GarantiSampleApp {

    // ----------------------------------------------------
    // 1) OAuth2 ile token alma
    // ----------------------------------------------------
    private static final String CLIENT_ID     = "YOUR_CLIENT_ID";      // README’den client_id örneği :contentReference[oaicite:1]{index=1}
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";  // README’den client_secret örneği :contentReference[oaicite:2]{index=2}
    private static final String TOKEN_URL     = "https://fw.garanti.com.tr/oauth2/token"; // :contentReference[oaicite:3]{index=3}

    private final GarantiClient garantiClient;

    public GarantiSampleApp() {
        // GarantiClient, OAuth2 flow’unu kendi içinde yönetir.
        this.garantiClient = new GarantiClient.Builder()
                .withClientId(CLIENT_ID)
                .withClientSecret(CLIENT_SECRET)
                .withTokenEndpoint(TOKEN_URL)
                .build();
    }

    // ----------------------------------------------------
    // 2) Hesap Bakiyesi Sorgulama
    // ----------------------------------------------------
    @GetMapping("/balance")
    public AccountBalance getBalance() {
        try {
            return garantiClient.getAccountBalance();
        } catch (GarantiException e) {
            throw new RuntimeException("Hesap bakiyesi alınırken hata: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------
    // 3) Hesap Hareketleri Sorgulama
    // ----------------------------------------------------
    @GetMapping("/transactions")
    public List<Transaction> getTransactions(
            @RequestParam(defaultValue="2025-04-01") String fromDate,
            @RequestParam(defaultValue="2025-04-25") String toDate) {
        try {
            return garantiClient.getAccountTransactions(fromDate, toDate);
        } catch (GarantiException e) {
            throw new RuntimeException("Hareketler alınırken hata: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------
    // 4) OAuth Callback Endpoint
    // ----------------------------------------------------
    // README’deki örnekten uyarlanmıştır: :contentReference[oaicite:4]{index=4}
    @PostMapping("/callback")
    public String handleCallback(@RequestBody String body) {
        System.out.println("Callback body: " + body);
        // Burada kod içinde body’yi parse edip token’ı çekebilir veya loglayabilirsin
        return "Callback alındı";
    }

    public static void main(String[] args) {
        SpringApplication.run(GarantiSampleApp.class, args);
    }
}
