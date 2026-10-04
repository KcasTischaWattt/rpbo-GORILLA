package com.backend.crosswords.corpus.controllers;

import com.backend.crosswords.corpus.dto.CreateTagDTO;
import com.backend.crosswords.corpus.dto.FoldersDTO;
import com.backend.crosswords.corpus.services.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tags")
@Tag(name = "Tag controller", description = "Controller for all operations with tags")
public class TagController {
    private final TagService tagService;
    @Value("${backend-secret-key}")
    private String backendSecretKey;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }
    @Operation(summary = "Create a tag", description = "This is a simple creating tag endpoint", deprecated = true)
    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestHeader("Authorization") String authHeader, @RequestBody CreateTagDTO createTagDTO) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!backendSecretKey.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your token is incorrect!");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This method is deprecated!");
        /*try {
            tagService.createTag(createTagDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);*/
    }
    @Operation(summary = "Delete a tag by id", description = "This is a very unsafe end point, as it leads to the deletion of all related information.", deprecated = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTagById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!backendSecretKey.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your token is incorrect!");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This method is deprecated!");
        /*try {
            tagService.deleteTagById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);*/
    }
}
