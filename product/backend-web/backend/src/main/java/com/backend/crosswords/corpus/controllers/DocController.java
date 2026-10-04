package com.backend.crosswords.corpus.controllers;

import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.dto.*;
import com.backend.crosswords.corpus.models.Comment;
import com.backend.crosswords.corpus.models.Package;
import com.backend.crosswords.corpus.services.DocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/documents")
@Tag(name = "Doc controller", description = "Controller for all operations with documents")
public class DocController {
    private final DocService docService;
    private final ModelMapper modelMapper;
    @Value("${backend-secret-key}")
    private String backendSecretKey;

    public DocController(DocService docService, ModelMapper modelMapper) {
        this.docService = docService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Create a document", description = "This is a simple creating document endpoint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully created a document"),
            @ApiResponse(responseCode = "400", description = "There is no sources with such id!")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createDoc(@RequestHeader("Authorization") String authHeader, @RequestBody CreateDocDTO createDocDTO) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!backendSecretKey.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your token is incorrect!");
        }
        try {
            docService.createDoc(createDocDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Get all docs", description = "This endpoint gets you all documents with all possibly required information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Searched documents",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocDTO.class)))),
            @ApiResponse(responseCode = "503", description = "If this endpoint cant find a document by id, then something is wrong with synchronizing Postgres and ES," +
                    "maybe use DELETE /documents/itself and delete all documents from Postgres," +
                    "because now all endpoints, corresponded with docs, may return errors and it could cause a lot of troubles")
    })
    @GetMapping
    public ResponseEntity<?> getAllDocs() {
        try {
            return ResponseEntity.ok(docService.getAllDocs());
        } catch (NoSuchElementException e) {
            // TODO переделать данную фигню
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Sorry, our database is corrupted (check ES)");
        }
    }

    @Operation(summary = "Get doc by id", description = "This endpoint gets you a document by its specified id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Searched document", content = @Content(schema = @Schema(implementation = DocDTO.class))),
            @ApiResponse(responseCode = "404", description = "There is no document with such id!")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(docService.getDocByIdAndTransformIntoDTO(id, true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No documents with such id!");
        }
    }

    @Operation(summary = "Get docs by search", description = "This endpoint gets you all documents by your filtration's and search's arguments." +
            "\nNull or empty filtration's argument always gets though the corresponding filtration check." +
            "\nNull or empty search body or search mode means that you want to use only filtration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Searched documents (annotations are not included)", content = @Content(schema = @Schema(implementation = SearchResultDTO.class))),
            @ApiResponse(responseCode = "404", description = "May appear dew to wrong id, while searching by id"),
            @ApiResponse(responseCode = "400", description = "There is no search modes with such name")
    })
    @PostMapping("/search")
    public ResponseEntity<?> getDocsBySearch(@RequestBody SearchDocDTO searchDocDTO) {
        try {
            User user;
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
                user = crosswordUserDetails.getUser();
            } catch (ClassCastException e) {
                user = null;
            }
            return ResponseEntity.ok(docService.searchDocsAndTransformIntoDTO(searchDocDTO, user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete doc by id", description = "This endpoint deletes document by id and destroys all connections with other models")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully deleted the document"),
            @ApiResponse(responseCode = "401", description = "You are trying to delete a document while unauthorized"),
            @ApiResponse(responseCode = "403", description = "You don't have enough rights to delete documents"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocById(@PathVariable Long id) {
        try {
            docService.deleteDocById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Rate doc by id", description = "This endpoint lets you rate a document, parameters can be null or numbers from 1 to 5, user must be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully rated a document"),
            @ApiResponse(responseCode = "401", description = "You are trying to rate a document while unauthorized"),
            @ApiResponse(responseCode = "400", description = "Rating's arguments must be in range of 1 to 5"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id")
    })
    @PatchMapping("/{id}/rate")
    public ResponseEntity<?> rateDocById(@PathVariable Long id, @RequestBody RateDocDTO rateDocDTO) {
        try {
            docService.rateDocById(id, rateDocDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no documents with such id!");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Edit doc by id", description = "This endpoint lets you edit a document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully updated the document"),
            @ApiResponse(responseCode = "401", description = "You are trying to updated a document while unauthorized"),
            @ApiResponse(responseCode = "403", description = "You don't have enough rights to updated documents"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id"),
            @ApiResponse(responseCode = "400", description = "Tags, sources and language must be correct and can't be null or empty"),
    })
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateDocById(@PathVariable Long id, @RequestBody EditDocDTO editDocDTO) {
        try {
            docService.editDocById(id, editDocDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Add doc into the package", description = "This endpoint lets add document by its id into the user's package, specified by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully added a document into the package"),
            @ApiResponse(responseCode = "401", description = "You are trying to add a doc into the package while not authenticated"),
            @ApiResponse(responseCode = "404", description = "This user doesn't have packages with such name or the document's id is incorrect")
    })
    @PostMapping("/{docId}/put_into/{packageName}")
    public ResponseEntity<?> addDocIntoPackage(@PathVariable Long docId, @PathVariable String packageName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.addDocByIdIntoPackageByName(crosswordUserDetails.getUser(), docId, packageName);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Remove a doc from the package", description = "This endpoint lets remove a document by its id from the user's package, specified by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully removed a doc from the package"),
            @ApiResponse(responseCode = "401", description = "You are trying to remove a doc from the package while not authenticated"),
            @ApiResponse(responseCode = "404", description = "This user doesn't have packages with such name or the document's id is incorrect")
    })
    @PostMapping("/{docId}/remove_from/{packageName}")
    public ResponseEntity<?> removeFromPackage(@PathVariable Long docId, @PathVariable String packageName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.removeDocByIdFromPackageByName(crosswordUserDetails.getUser(), docId, packageName);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Add doc to favourites", description = "This endpoint lets you add doc to favourites, user must be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully added a document to favourites"),
            @ApiResponse(responseCode = "401", description = "You are trying to add a doc while not authenticated"),
            @ApiResponse(responseCode = "404", description = "This user doesn't have packages with such name or the document's id is incorrect")
    })
    @PostMapping("/{id}/add_to_favourites")
    public ResponseEntity<?> addDocToFavouritesById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.addDocByIdIntoPackageByName(crosswordUserDetails.getUser(), id, Package.favouritesName);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Remove doc from favourites", description = "This endpoint lets you remove doc from favourites, user must be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully removed a document from favourites"),
            @ApiResponse(responseCode = "401", description = "You are trying to remove a doc while not authenticated")
    })
    @PostMapping("/{id}/remove_from_favourites")
    public ResponseEntity<?> removeDocFromFavouritesById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.removeDocByIdFromPackageByName(crosswordUserDetails.getUser(), id, Package.favouritesName);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Annotate doc by it's id", description = "This endpoint lets you annotate a certain document by it's id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully annotated a document and you can get a new annotation's id", content = @Content(schema = @Schema(implementation = AnnotationsIdDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to annotate a document while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id")
    })
    @PostMapping("/{id}/annotate")
    public ResponseEntity<?> annotateDocById(@PathVariable Long id, @RequestBody CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
        Long annotationId;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            annotationId = docService.annotateDocById(crosswordUserDetails.getUser(), id, createUpdateAnnotationDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(new AnnotationsIdDTO(annotationId));
    }

    @Operation(summary = "Delete annotation from the document", description = "This endpoint lets you delete document's annotation by theirs ids")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully deleted a document's annotation"),
            @ApiResponse(responseCode = "401", description = "You are trying to delete a document's annotation while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents or annotations with such id"),
            @ApiResponse(responseCode = "400", description = "You are trying to delete an annotate for a wrong document or the annotation that isn't yours")
    })
    @DeleteMapping("/{docId}/annotate/{annotationId}")
    public ResponseEntity<?> deleteAnnotationByIdFromDocById(@PathVariable Long docId, @PathVariable Long annotationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.deleteAnnotationByIdFromDocById(crosswordUserDetails.getUser(), docId, annotationId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Update user's annotation for the document", description = "This endpoint lets you update user's annotation for the document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully updated user's annotation for the document", content = @Content(schema = @Schema(implementation = AnnotationsIdDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to update user's annotation for the document while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents or annotations with such id"),
            @ApiResponse(responseCode = "400", description = "You are trying to delete an annotation for a wrong document or the annotation that isn't yours")
    })
    @PutMapping("/{docId}/annotate/{annotationId}")
    public ResponseEntity<?> updateAnnotationByIdForDocById(@PathVariable Long docId, @PathVariable Long annotationId, @RequestBody CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.updateAnnotationByIdForDocById(crosswordUserDetails.getUser(), docId, annotationId, createUpdateAnnotationDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(new AnnotationsIdDTO(annotationId));
    }

    @Operation(summary = "Comment doc by it's id", description = "This endpoint lets you comment a certain document by it's id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully commented a document", content = @Content(schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to comment a document while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id")
    })
    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentDocById(@PathVariable Long id, @RequestBody CreateUpdateCommentDTO createUpdateCommentDTO) {
        CommentDTO commentDTO = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            var comment = docService.commentDocById(crosswordUserDetails.getUser(), id, createUpdateCommentDTO);
            commentDTO = modelMapper.map(comment, CommentDTO.class);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(commentDTO);
    }

    @Operation(summary = "Delete comment from the document", description = "This endpoint lets you delete document's comment by theirs ids")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully deleted a document's comment"),
            @ApiResponse(responseCode = "401", description = "You are trying to delete a document's comment while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents or comments with such id"),
            @ApiResponse(responseCode = "400", description = "You are trying to delete a comment for a wrong document or the comment that isn't yours")
    })
    @DeleteMapping("/{docId}/comment/{commentId}")
    public ResponseEntity<?> deleteCommentByIdFromDocById(@PathVariable Long docId, @PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docService.deleteCommentByIdFromDocById(crosswordUserDetails.getUser(), docId, commentId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Get all user's comments from the document", description = "This endpoint lets you get all user's comments from the document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get all user's comments from the document", content = @Content(schema = @Schema(implementation = CommentsDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get all user's comments from the document while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id")
    })
    @GetMapping("/{docId}/comment")
    public ResponseEntity<?> getAllUsersCommentsFromDocById(@PathVariable Long docId) {
        CommentsDTO commentsDTO = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            List<Comment> comments = docService.getAllUsersCommentsFromDocById(crosswordUserDetails.getUser(), docId);
            commentsDTO = new CommentsDTO();
            commentsDTO.setComments(new ArrayList<>());
            for (var comment : comments) {
                commentsDTO.getComments().add(modelMapper.map(comment, CommentDTO.class));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(commentsDTO);
    }
    @Operation(summary = "Update user's comment for the document", description = "This endpoint lets you update user's comment for the document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully updated user's comment for the document", content = @Content(schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to update user's comment for the document while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents or comments with such id"),
            @ApiResponse(responseCode = "400", description = "You are trying to delete a comment for a wrong document or the comment that isn't yours")
    })
    @PutMapping("/{docId}/comment/{commentId}")
    public ResponseEntity<?> updateCommentByIdForDocById(@PathVariable Long docId, @PathVariable Long commentId, @RequestBody CreateUpdateCommentDTO createUpdateCommentDTO) {
        CommentDTO commentDTO = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            Comment comment = docService.updateCommentByIdForDocById(crosswordUserDetails.getUser(), docId, commentId, createUpdateCommentDTO);
            commentDTO = modelMapper.map(comment, CommentDTO.class);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(commentDTO);
    }

    @Operation(summary = "Get packages for a document", description = "This endpoint lets get all packages from a user and check if a certain doc is included in them")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get packages for a document", content = @Content(schema = @Schema(implementation = FoldersForDocDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get packages for a document while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no documents with such id")
    })
    @GetMapping("/{id}/packages")
    public ResponseEntity<?> getPackagesForDoc(@PathVariable Long id) {
        List<PackagesForDocDTO> docsPackages = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            docsPackages = docService.getPackagesForDocAndTransformIntoDTO(id, crosswordUserDetails.getUser());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(new FoldersForDocDTO(docsPackages));
    }
}
