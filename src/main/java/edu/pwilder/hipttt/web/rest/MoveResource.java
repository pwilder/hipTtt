package edu.pwilder.hipttt.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.pwilder.hipttt.domain.Move;
import edu.pwilder.hipttt.service.MoveService;
import edu.pwilder.hipttt.web.rest.util.HeaderUtil;
import edu.pwilder.hipttt.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Move.
 */
@RestController
@RequestMapping("/api")
public class MoveResource {

    private final Logger log = LoggerFactory.getLogger(MoveResource.class);
        
    @Inject
    private MoveService moveService;

    /**
     * POST  /moves : Create a new move.
     *
     * @param move the move to create
     * @return the ResponseEntity with status 201 (Created) and with body the new move, or with status 400 (Bad Request) if the move has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/moves")
    @Timed
    public ResponseEntity<Move> createMove(@Valid @RequestBody Move move) throws URISyntaxException {
        log.debug("REST request to save Move : {}", move);
        if (move.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("move", "idexists", "A new move cannot already have an ID")).body(null);
        }
        Move result = moveService.save(move);
        return ResponseEntity.created(new URI("/api/moves/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("move", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /moves : Updates an existing move.
     *
     * @param move the move to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated move,
     * or with status 400 (Bad Request) if the move is not valid,
     * or with status 500 (Internal Server Error) if the move couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/moves")
    @Timed
    public ResponseEntity<Move> updateMove(@Valid @RequestBody Move move) throws URISyntaxException {
        log.debug("REST request to update Move : {}", move);
        if (move.getId() == null) {
            return createMove(move);
        }
        Move result = moveService.save(move);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("move", move.getId().toString()))
            .body(result);
    }

    /**
     * GET  /moves : get all the moves.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of moves in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/moves")
    @Timed
    public ResponseEntity<List<Move>> getAllMoves(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Moves");
        Page<Move> page = moveService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/moves");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /moves/:id : get the "id" move.
     *
     * @param id the id of the move to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the move, or with status 404 (Not Found)
     */
    @GetMapping("/moves/{id}")
    @Timed
    public ResponseEntity<Move> getMove(@PathVariable Long id) {
        log.debug("REST request to get Move : {}", id);
        Move move = moveService.findOne(id);
        return Optional.ofNullable(move)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /moves/:id : delete the "id" move.
     *
     * @param id the id of the move to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/moves/{id}")
    @Timed
    public ResponseEntity<Void> deleteMove(@PathVariable Long id) {
        log.debug("REST request to delete Move : {}", id);
        moveService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("move", id.toString())).build();
    }

}
