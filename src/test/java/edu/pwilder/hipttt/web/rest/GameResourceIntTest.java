package edu.pwilder.hipttt.web.rest;

import edu.pwilder.hipttt.HipTttApp;

import edu.pwilder.hipttt.domain.Game;
import edu.pwilder.hipttt.repository.GameRepository;
import edu.pwilder.hipttt.service.GameService;

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
 * Test class for the GameResource REST controller.
 *
 * @see GameResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HipTttApp.class)
public class GameResourceIntTest {

    private static final ZonedDateTime DEFAULT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private GameRepository gameRepository;

    @Inject
    private GameService gameService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restGameMockMvc;

    private Game game;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GameResource gameResource = new GameResource();
        ReflectionTestUtils.setField(gameResource, "gameService", gameService);
        this.restGameMockMvc = MockMvcBuilders.standaloneSetup(gameResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Game createEntity(EntityManager em) {
        Game game = new Game()
                .timestamp(DEFAULT_TIMESTAMP);
        return game;
    }

    @Before
    public void initTest() {
        game = createEntity(em);
    }

    @Test
    @Transactional
    public void createGame() throws Exception {
        int databaseSizeBeforeCreate = gameRepository.findAll().size();

        // Create the Game

        restGameMockMvc.perform(post("/api/games")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(game)))
            .andExpect(status().isCreated());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeCreate + 1);
        Game testGame = gameList.get(gameList.size() - 1);
        assertThat(testGame.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    public void createGameWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = gameRepository.findAll().size();

        // Create the Game with an existing ID
        Game existingGame = new Game();
        existingGame.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGameMockMvc.perform(post("/api/games")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingGame)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGames() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        // Get all the gameList
        restGameMockMvc.perform(get("/api/games?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(game.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(sameInstant(DEFAULT_TIMESTAMP))));
    }

    @Test
    @Transactional
    public void getGame() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        // Get the game
        restGameMockMvc.perform(get("/api/games/{id}", game.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(game.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(sameInstant(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void getNonExistingGame() throws Exception {
        // Get the game
        restGameMockMvc.perform(get("/api/games/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGame() throws Exception {
        // Initialize the database
        gameService.save(game);

        int databaseSizeBeforeUpdate = gameRepository.findAll().size();

        // Update the game
        Game updatedGame = gameRepository.findOne(game.getId());
        updatedGame
                .timestamp(UPDATED_TIMESTAMP);

        restGameMockMvc.perform(put("/api/games")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGame)))
            .andExpect(status().isOk());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
        Game testGame = gameList.get(gameList.size() - 1);
        assertThat(testGame.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void updateNonExistingGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();

        // Create the Game

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restGameMockMvc.perform(put("/api/games")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(game)))
            .andExpect(status().isCreated());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteGame() throws Exception {
        // Initialize the database
        gameService.save(game);

        int databaseSizeBeforeDelete = gameRepository.findAll().size();

        // Get the game
        restGameMockMvc.perform(delete("/api/games/{id}", game.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
