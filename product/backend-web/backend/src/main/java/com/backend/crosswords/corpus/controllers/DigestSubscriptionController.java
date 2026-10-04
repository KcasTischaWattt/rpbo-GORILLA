package com.backend.crosswords.corpus.controllers;

import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.config.Pair;
import com.backend.crosswords.corpus.dto.*;
import com.backend.crosswords.corpus.services.DigestSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/subscriptions")
@Tag(name = "Subscription controller", description = "Controller for all operations with subscriptions")
public class DigestSubscriptionController {
    private final DigestSubscriptionService digestSubscriptionService;

    public DigestSubscriptionController(DigestSubscriptionService digestSubscriptionService) {
        this.digestSubscriptionService = digestSubscriptionService;
    }

    @Operation(summary = "Create a digest subscription", description = "This endpoint lets you create a new subscription, owner will be extracted from auth, he also will be added into subscribers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully created a new subscription and subscribed users on it"),
            @ApiResponse(responseCode = "401", description = "You are trying to create a digest subscription while not authenticated"),
            @ApiResponse(responseCode = "400", description = "At least one source or tag has an incorrect name and the lists of them can't be null or empty"),
            @ApiResponse(responseCode = "404", description = "At least one subscriber cant be found in the DB")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createDigestSubscription(@RequestBody CreateDigestSubscriptionDTO createDigestSubscriptionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            digestSubscriptionService.createDigestSubscription(crosswordUserDetails.getUser(), createDigestSubscriptionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(summary = "Update a digest subscription", description = "This endpoint lets you update a digest subscription, owner must be included in the followers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully updated a digest subscription and managed subscribers on it"),
            @ApiResponse(responseCode = "401", description = "You are trying to updated a digest subscription while not authenticated"),
            @ApiResponse(responseCode = "400", description = "At least one source has an incorrect name, or sources or tags are null"),
            @ApiResponse(responseCode = "404", description = "At least one subscriber, one digest subscription or one subscription's settings, cant be found in the DB"),
            @ApiResponse(responseCode = "403", description = "You are trying to updated a digest subscription while not owning it")
    })
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateDigestSubscription(@PathVariable Long id, @RequestBody UpdateDigestSubscriptionDTO updateDigestSubscriptionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            digestSubscriptionService.updateDigestSubscription(crosswordUserDetails.getUser(), id, updateDigestSubscriptionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(summary = "Update a digest subscription's settings", description = "This endpoint lets you update a digest subscription's settings, remember that after no followers remains the subscription is being deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully updated a digest subscription's settings", content = @Content(schema = @Schema(implementation = UpdatedDigestSubscriptionSettingsDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to updated a digest subscription's settings while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no such digest subscription or you are trying to edit private subscription while not its follower"),
    })
    @PutMapping("/{id}/settings/update")
    public ResponseEntity<?> updateDigestSubscriptionSettingsForUser(@PathVariable Long id, @RequestBody DigestSubscriptionSettingsDTO subscriptionSettingsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        Pair<DigestSubscriptionSettingsDTO, Boolean> result;
        try {
            result = digestSubscriptionService.updateDigestSubscriptionSettingsForUser(id, subscriptionSettingsDTO, crosswordUserDetails.getUser());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        var newSubscriptionSettingsDTO = result.getFirst();
        var deleted = result.getSecond();
        return ResponseEntity.ok(new UpdatedDigestSubscriptionSettingsDTO(newSubscriptionSettingsDTO.getSubscribed(), newSubscriptionSettingsDTO.getSendToMail(), newSubscriptionSettingsDTO.getMobileNotifications(), deleted));
    }
    @Operation(summary = "Get all users from the subscription", description = "This endpoint lets you get all users from the subscription except the owner")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get all users from the subscription", content = @Content(schema = @Schema(implementation = FollowersUsernamesDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get all users from the subscription while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no subscriptions with such id"),
    })
    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getAllDigestSubscriptionsUsersByDigestSubscriptionId(@PathVariable Long id) {
        FollowersUsernamesDTO usersUsernames = new FollowersUsernamesDTO();
        try {
            usersUsernames.setFollowers(digestSubscriptionService.getAllDigestSubscriptionsUsersExceptOwner(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(usersUsernames);
    }
    @Operation(summary = "Get all user's subscriptions", description = "This endpoint lets you get all user's subscriptions, followers are not included in response")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get all user's subscriptions", content = @Content(schema = @Schema(implementation = UsersDigestSubscriptionsDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get all user's subscriptions while not authenticated"),
    })
    @GetMapping
    public ResponseEntity<?> getAllUsersDigestSubscriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        UsersDigestSubscriptionsDTO usersDigestSubscriptionsDTO = digestSubscriptionService.getAllUsersDigestSubscriptionsAndTransformIntoUsersDigestSubscriptionsDTO(crosswordUserDetails.getUser());
        return ResponseEntity.ok(usersDigestSubscriptionsDTO);
    }
    @Operation(summary = "Get all available user's subscriptions", description = "This endpoint lets you get all available user's subscriptions (public or those on which he is subscribed)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get all available user's subscriptions", content = @Content(schema = @Schema(implementation = UsersDigestSubscriptionsDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get all available user's subscriptions while not authenticated"),
    })
    @GetMapping("/available")
    public ResponseEntity<?> getAllUsersAvailableDigestSubscriptions() {
        User user;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            user = crosswordUserDetails.getUser();
        } catch (ClassCastException e) {
            user = null;
        }
        UsersDigestSubscriptionsDTO usersDigestSubscriptionsDTO = digestSubscriptionService.getAllUsersAvailableDigestSubscriptionsAndTransformIntoUsersDigestSubscriptionsDTO(user);
        return ResponseEntity.ok(usersDigestSubscriptionsDTO);
    }

    @Operation(summary = "Get digest subscription by id", description = "This endpoint lets you get digest subscription by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get digest subscription by id", content = @Content(schema = @Schema(implementation = DigestSubscriptionDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get digest subscription by id while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no subscriptions with such id"),
            @ApiResponse(responseCode = "403", description = "This subscription is private and this user isn't its subscriber")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDigestSubscriptionById(@PathVariable Long id) {
        User user;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            user = crosswordUserDetails.getUser();
        } catch (ClassCastException e) {
            user = null;
        }
        DigestSubscriptionDTO subscription;
        try {
            subscription = digestSubscriptionService.getDigestSubscriptionByIdAndTransformIntoDTO(id, user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        return ResponseEntity.ok(subscription);
    }
    @Operation(summary = "Check if a user is the digest subscription's owner", description = "This endpoint lets you check if a user is the digest subscription's owner")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully checked if a user is the digest subscription's owner", content = @Content(schema = @Schema(implementation = CheckOwnershipDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to check if a user is the digest subscription's owner while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no subscriptions with such id")
    })
    @GetMapping("/{id}/check_ownership")
    public ResponseEntity<?> checkOwnership(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(new CheckOwnershipDTO(digestSubscriptionService.checkUsersOwnershipOfDigestSubscriptionById(id, crosswordUserDetails.getUser())));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @Operation(summary = "Change digest subscription's owner", description = "This endpoint lets you change digest subscription's owner")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully changed digest subscription's owner"),
            @ApiResponse(responseCode = "401", description = "You are trying to change digest subscription's owner while not authenticated"),
            @ApiResponse(responseCode = "400", description = "A user must be a follower of a digest subscription to become a new owner of it and he can't be its old owner!"),
            @ApiResponse(responseCode = "404", description = "There is no such digest subscriptions or such users that you are trying to give an ownership"),
            @ApiResponse(responseCode = "403", description = "You are trying to set a new digest subscription's owner while not owning it")
    })
    @PatchMapping ("/{id}/change_owner")
    public ResponseEntity<?> changeDigestSubscriptionsOwner(@PathVariable Long id, @RequestBody ChangeDigestSubscriptionsOwnerDTO changeDigestSubscriptionOwnerDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            digestSubscriptionService.changeDigestSubscriptionsOwner(crosswordUserDetails.getUser(), id, changeDigestSubscriptionOwnerDTO.getOwner());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(summary = "Get subscription's digests", description = "This endpoint lets you get subscription's digests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get subscription's digests", content = @Content(schema = @Schema(implementation = SubscriptionWithDigestsWrapperDTO.class))),
            @ApiResponse(responseCode = "403", description = "You are trying to get private subscription's digests but you are not its subscriber"),
            @ApiResponse(responseCode = "404", description = "There is no subscriptions with such id")
    })
    @GetMapping("/{id}/digests")
    public ResponseEntity<?> getDigestSubscriptionsDigests(
            @PathVariable Long id,
            @RequestParam(required = false, name = "next_page") Integer pageNumber,
            @RequestParam(required = false, name = "matches_per_page") Integer matchesPerPage) {
        User user;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            user = crosswordUserDetails.getUser();
        } catch (ClassCastException e) {
            user = null;
        }
        try {
            return ResponseEntity.ok(digestSubscriptionService.getDigestSubscriptionsDigestsByIdAndConvertIntoDTO(id, user, pageNumber, matchesPerPage));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @Operation(summary = "Check an access to the digest subscription", description = "This endpoint lets check an access to the digest subscription")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully checked an access to the digest subscription", content = @Content(schema = @Schema(implementation = CheckAccessDTO.class))),
            @ApiResponse(responseCode = "404", description = "There is no subscriptions with such id")
    })
    @GetMapping("/{id}/check_access")
    public ResponseEntity<?> checkUsersAccessToDigestSubscription(@PathVariable Long id) {
        User user;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            user = crosswordUserDetails.getUser();
        } catch (ClassCastException e) {
            user = null;
        }
        try {
            return ResponseEntity.ok(digestSubscriptionService.checkUsersAccessToSubscriptionByIdAndConvertIntoDTO(id, user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/most_rated")
    public ResponseEntity<?> getMostRatedSubscriptions(@RequestParam Integer amount) {
        return ResponseEntity.ok(digestSubscriptionService.getMostRatedSubscriptionsAndTransformIntoDTO(amount));
    }
}
