package com.aninode.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslationService {

    public String translateToSpanish(String text) {
        if (text == null || text.trim().isEmpty()) return "Sin descripción disponible.";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // Añadimos un User-Agent para intentar engañar a Google y que no nos bloquee tan rápido
            headers.add("User-Agent", "Mozilla/5.0");

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client", "gtx");
            map.add("sl", "en");
            map.add("tl", "es");
            map.add("dt", "t");
            map.add("q", text);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            String response = restTemplate.postForObject("https://translate.googleapis.com/translate_a/single", request, String.class);

            //VALIDACIÓN ANTICRASHEO
            if (response == null || response.trim().startsWith("<")) {
                System.err.println("⚠️ Google Translate bloqueó la petición.");
                return text; // Devolvemos el original en inglés
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode textNodes = rootNode.get(0);

            StringBuilder traduccionCompleta = new StringBuilder();

            if (textNodes != null && textNodes.isArray()) {
                for (JsonNode node : textNodes) {
                    if (node.has(0) && !node.get(0).isNull()) {
                        traduccionCompleta.append(node.get(0).asText());
                    }
                }
            }

            String resultado = traduccionCompleta.toString();
            return resultado.isEmpty() ? text : resultado;

        } catch (Exception e) {
            // Si hay un error de parseo o conexión, capturamos el error
            // y devolvemos el texto original para que la App siga funcionando
            System.err.println("❌ Error en el servicio de traducción: " + e.getMessage());
            return text;
        }
    }
}