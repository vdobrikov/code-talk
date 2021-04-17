package com.codetalk.web;

import com.codetalk.model.Document;
import com.codetalk.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@Controller
public class IndexController {
    private final DocumentService documentService;

    public IndexController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/new")
    public String newDocument() {
        return documentService.create(new Document("Untitled", "JAVA", "// Lets code"))
                .map(Document::getId)
                .map(id -> "redirect:/" + id)
                .block();
    }

    @GetMapping("/{id}")
    public String document(@PathVariable String id, Model model) {
        model.addAttribute("document", documentService.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundResponseStatusException(id)))
                .block());
        return "editor";
    }
}
