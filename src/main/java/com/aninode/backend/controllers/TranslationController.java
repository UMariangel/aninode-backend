package com.aninode.backend.controllers;

import com.aninode.backend.services.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/utils")
@CrossOrigin(origins = "*")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    public static class TranslationReq {
        public String text;
    }

    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> traducir(@RequestBody TranslationReq req) {

        String resultado = translationService.translateToSpanish(req.text);
        return ResponseEntity.ok(Collections.singletonMap("translatedText", resultado));
    }
}