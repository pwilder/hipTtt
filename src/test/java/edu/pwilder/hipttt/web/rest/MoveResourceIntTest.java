package edu.pwilder.hipttt.web.rest;

import edu.pwilder.hipttt.HipTttApp;

import edu.pwilder.hipttt.domain.Move;
import edu.pwilder.hipttt.repository.MoveRepository;
import edu.pwilder.hipttt.service.MoveService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static edu.pwilder.hipttt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MoveResource REST controller.
 *
 * @see MoveResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HipTttApp.class)
public class MoveResourceIntTest {

    private static final ZonedDateTime DEFAULT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_ROW = 0;
    private static final Integer UPDATED_ROW = 1;

    private static final Integer DEFAULT_COL = 0;
    private static final Integer UPDATED_COL = 1;

    private static final Integer DEFAULT_PLAYER = 0;
    private static final Integer UPDATED_PLAYER = 1;

    @Inject
    private MoveRepository moveRepository;

    @Inject
    private MoveService moveService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restMoveMockMvc;

    private Move move;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MoveResource moveResource = new MoveResource();
        ReflectionTestUtils.setField(moveResource, "moveService", moveService);
        this.restMoveMockMvc = MockMvcBuilders.standaloneSetup(moveResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Move createEntity(EntityManager em) {
        Move move = new Move()
                .timestamp(DEFAULT_TIMESTAMP)
                .row(DEFAULT_ROW)
                .col(DEFAULT_COL)
                .player(DEFAULT_PLAYER);
        return move;
    }

    @Before
    public void initTest() {
        move = createEntity(em);
    }

    @Test
    @Transactional
    public void createMove() throws Exception {
        int databaseSizeBeforeCreate = moveRepository.findAll().size();

        // Create the Move

        restMoveMockMvc.perform(post("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(move)))
            .andExpect(status().isCreated());

        // Validate the Move in the database
        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeCreate + 1);
        Move testMove = moveList.get(moveList.size() - 1);
        assertThat(testMove.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testMove.getRow()).isEqualTo(DEFAULT_ROW);
        assertThat(testMove.getCol()).isEqualTo(DEFAULT_COL);
        assertThat(testMove.getPlayer()).isEqualTo(DEFAULT_PLAYER);
    }

    @Test
    @Transactional
    public void createMoveWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = moveRepository.findAll().size();

        // Create the Move with an existing ID
        Move existingMove = new Move();
        existingMove.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMoveMockMvc.perform(post("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingMove)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkRowIsRequired() throws Exception {
        int databaseSizeBeforeTest = moveRepository.findAll().size();
        // set the field null
        move.setRow(null);

        // Create the Move, which fails.

        restMoveMockMvc.perform(post("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(move)))
            .andExpect(status().isBadRequest());

        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkColIsRequired() throws Exception {
        int databaseSizeBeforeTest = moveRepository.findAll().size();
        // set the field null
        move.setCol(null);

        // Create the Move, which fails.

        restMoveMockMvc.perform(post("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(move)))
            .andExpect(status().isBadRequest());

        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPlayerIsRequired() throws Exception {
        int databaseSizeBeforeTest = moveRepository.findAll().size();
        // set the field null
        move.setPlayer(null);

        // Create the Move, which fails.

        restMoveMockMvc.perform(post("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(move)))
            .andExpect(status().isBadRequest());

        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMoves() throws Exception {
        // Initialize the database
        moveRepository.saveAndFlush(move);

        // Get all the moveList
        restMoveMockMvc.perform(get("/api/moves?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(move.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(sameInstant(DEFAULT_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].row").value(hasItem(DEFAULT_ROW)))
            .andExpect(jsonPath("$.[*].col").value(hasItem(DEFAULT_COL)))
            .andExpect(jsonPath("$.[*].player").value(hasItem(DEFAULT_PLAYER)));
    }

    @Test
    @Transactional
    public void getMove() throws Exception {
        // Initialize the database
        moveRepository.saveAndFlush(move);

        // Get the move
        restMoveMockMvc.perform(get("/api/moves/{id}", move.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(move.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(sameInstant(DEFAULT_TIMESTAMP)))
            .andExpect(jsonPath("$.row").value(DEFAULT_ROW))
            .andExpect(jsonPath("$.col").value(DEFAULT_COL))
            .andExpect(jsonPath("$.player").value(DEFAULT_PLAYER));
    }

    @Test
    @Transactional
    public void getNonExistingMove() throws Exception {
        // Get the move
        restMoveMockMvc.perform(get("/api/moves/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMove() throws Exception {
        // Initialize the database
        moveService.save(move);

        int databaseSizeBeforeUpdate = moveRepository.findAll().size();

        // Update the move
        Move updatedMove = moveRepository.findOne(move.getId());
        updatedMove
                .timestamp(UPDATED_TIMESTAMP)
                .row(UPDATED_ROW)
                .col(UPDATED_COL)
                .player(UPDATED_PLAYER);

        restMoveMockMvc.perform(put("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMove)))
            .andExpect(status().isOk());

        // Validate the Move in the database
        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeUpdate);
        Move testMove = moveList.get(moveList.size() - 1);
        assertThat(testMove.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testMove.getRow()).isEqualTo(UPDATED_ROW);
        assertThat(testMove.getCol()).isEqualTo(UPDATED_COL);
        assertThat(testMove.getPlayer()).isEqualTo(UPDATED_PLAYER);
    }

    @Test
    @Transactional
    public void updateNonExistingMove() throws Exception {
        int databaseSizeBeforeUpdate = moveRepository.findAll().size();

        // Create the Move

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMoveMockMvc.perform(put("/api/moves")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(move)))
            .andExpect(status().isCreated());

        // Validate the Move in the database
        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMove() throws Exception {
        // Initialize the database
        moveService.save(move);

        int databaseSizeBeforeDelete = moveRepository.findAll().size();

        // Get the move
        restMoveMockMvc.perform(delete("/api/moves/{id}", move.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Move> moveList = moveRepository.findAll();
        assertThat(moveList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
