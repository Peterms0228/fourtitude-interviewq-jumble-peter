package asia.fourtitude.interviewq.jumble.controller;

import static org.junit.jupiter.api.Assertions.*;

import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import asia.fourtitude.interviewq.jumble.TestConfig;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

@WebMvcTest(GameApiController.class)
@Import(TestConfig.class)
class GameApiControllerTest {

    static final ObjectMapper OM = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    JumbleEngine jumbleEngine;

    @Autowired
    GameApiController gameApiController;

    /*
     * NOTE: Refer to "RootControllerTest.java", "GameWebControllerTest.java"
     * as reference. Search internet for resource/tutorial/help in implementing
     * the unit tests.
     *
     * Refer to "http://localhost:8080/swagger-ui/index.html" for REST API
     * documentation and perform testing.
     *
     * Refer to Postman collection ("interviewq-jumble.postman_collection.json")
     * for REST API documentation and perform testing.
     */

    @Test
    void whenCreateNewGame_thenSuccess() throws Exception {
        /*
         * Doing HTTP GET "/api/game/new"
         *
         * Input: None
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Created new game."
         * c) `id` is not null
         * d) `originalWord` is not null
         * e) `scrambleWord` is not null
         * f) `totalWords` > 0
         * g) `remainingWords` > 0 and same as `totalWords`
         * h) `guessedWords` is empty list
         */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/game/new");
        MockHttpServletResponse response = mvc.perform(request).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus(),"HTTP status == 200");

        ObjectMapper mapper = new ObjectMapper();
        GameGuessOutput gameGuessOutput = mapper.readValue(response.getContentAsString(), GameGuessOutput.class);

        assertEquals("Created new game.", gameGuessOutput.getResult(),
                "`result` equals \"Created new game.\"");
        assertNotNull(gameGuessOutput.getId(), "`id` is not null");
        assertNotNull(gameGuessOutput.getOriginalWord(), "`originalWord` is not null");
        assertNotNull(gameGuessOutput.getScrambleWord(), "`scrambleWord` is not null");
        assertTrue(gameGuessOutput.getTotalWords() > 0, "`totalWords` > 0");
        assertTrue(gameGuessOutput.getRemainingWords() > 0, "`remainingWords` > 0");
        assertEquals(gameGuessOutput.getRemainingWords(), gameGuessOutput.getTotalWords(),
                "`remainingWords` same as `totalWords`");
        assertTrue(gameGuessOutput.getGuessedWords().isEmpty(), "`guessedWords` is empty list");
    }

    @Test
    void givenMissingId_whenPlayGame_thenInvalidId() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is null or missing
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Invalid Game ID."
         */
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/game/guess");
        request.contentType(MediaType.APPLICATION_JSON_VALUE);
        request.accept(MediaType.APPLICATION_JSON_VALUE);
        request.content("{\"id\":null,\"word\":null}");

        MockHttpServletResponse responseIdNull = mvc.perform(request).andReturn().getResponse();
        GameGuessOutput outputIdNull = mapper.readValue(responseIdNull.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), responseIdNull.getStatus(),"HTTP status == 404");
        assertEquals("Invalid Game ID.", outputIdNull.getResult(),
                "`result` equals \"Invalid Game ID.\"");


        /*
        request.content("{\"id\":\"test\",\"word\":null}");

        MockHttpServletResponse responseWordNull = mvc.perform(request).andReturn().getResponse();
        GameGuessOutput outputWordNull = mapper.readValue(responseWordNull.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), responseWordNull.getStatus(),"HTTP status == 404");
        assertEquals("Invalid Word.", outputWordNull.getResult(),
                "`result` equals \"Invalid Word.\"");
         */
    }

    @Test
    void givenMissingRecord_whenPlayGame_thenRecordNotFound() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is some valid ID (but not exists in game system)
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Game board/state not found."
         */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/game/guess");
        request.contentType(MediaType.APPLICATION_JSON_VALUE);
        request.accept(MediaType.APPLICATION_JSON_VALUE);
        request.content("{\"id\":\"test\",\"word\":\"test\"}");

        MockHttpServletResponse response = mvc.perform(request).andReturn().getResponse();
        ObjectMapper mapper = new ObjectMapper();
        GameGuessOutput gameGuessOutput = mapper.readValue(response.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus(),"HTTP status == 404");
        assertEquals("Game board/state not found.", gameGuessOutput.getResult(),
                "`result` equals \"Game board/state not found.\"");
    }

    @Test
    void givenCreateNewGame_whenSubmitNullWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is null or missing
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` is equals to `input.word`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder requestNewGame = MockMvcRequestBuilders.get("/api/game/new");
        MockHttpServletResponse responseNewGame = mvc.perform(requestNewGame).andReturn().getResponse();
        GameGuessOutput outputNewGame = mapper.readValue(responseNewGame.getContentAsString(), GameGuessOutput.class);

        String gameId = outputNewGame.getId();
        String inputWord = "";

        MockHttpServletRequestBuilder requestGuess = MockMvcRequestBuilders.post("/api/game/guess");
        requestGuess.contentType(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.accept(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.content("{\"id\":\"" + gameId + "\",\"word\":\"" + inputWord + "\"}");

        MockHttpServletResponse responseGuess = mvc.perform(requestGuess).andReturn().getResponse();
        GameGuessOutput outputGuess = mapper.readValue(responseGuess.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.OK.value(), responseGuess.getStatus(),"HTTP status == 200");
        assertEquals("Guessed incorrectly.", outputGuess.getResult(),
                "`result` equals \"Guessed incorrectly.\"");
        assertEquals(outputNewGame.getId(), outputGuess.getId(),"`id` equals to `id` of this game");
        assertEquals(outputNewGame.getOriginalWord(), outputGuess.getOriginalWord(),
                "`originalWord` is equals to `originalWord` of this game");
        assertNotNull(outputGuess.getScrambleWord(),"`scrambleWord` is not null");
        assertEquals(inputWord, outputGuess.getGuessWord(),"guessWord` is equals to `input.word`");
        assertEquals(outputNewGame.getTotalWords(), outputGuess.getTotalWords(),
                "`totalWords` is equals to `totalWords` of this game");
        assertEquals(outputNewGame.getRemainingWords(), outputGuess.getRemainingWords(),
                "`remainingWords` is equals to `remainingWords` of previous game state (no change)");
        assertTrue(outputGuess.getGuessedWords().isEmpty(),
                "`guessedWords` is empty list (because this is first attempt)");
    }

    @Test
    void givenCreateNewGame_whenSubmitWrongWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is some value (that is not correct answer)
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder requestNewGame = MockMvcRequestBuilders.get("/api/game/new");
        MockHttpServletResponse responseNewGame = mvc.perform(requestNewGame).andReturn().getResponse();
        GameGuessOutput outputNewGame = mapper.readValue(responseNewGame.getContentAsString(), GameGuessOutput.class);

        String gameId = outputNewGame.getId();
        String inputWord = "incorrectAnswer";

        MockHttpServletRequestBuilder requestGuess = MockMvcRequestBuilders.post("/api/game/guess");
        requestGuess.contentType(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.accept(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.content("{\"id\":\"" + gameId + "\",\"word\":\"" + inputWord + "\"}");

        MockHttpServletResponse responseGuess = mvc.perform(requestGuess).andReturn().getResponse();
        GameGuessOutput outputGuess = mapper.readValue(responseGuess.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.OK.value(), responseGuess.getStatus(),"HTTP status == 200");
        assertEquals("Guessed incorrectly.", outputGuess.getResult(),
                "`result` equals \"Guessed incorrectly.\"");
        assertEquals(outputNewGame.getId(), outputGuess.getId(),"`id` equals to `id` of this game");
        assertEquals(outputNewGame.getOriginalWord(), outputGuess.getOriginalWord(),
                "`originalWord` is equals to `originalWord` of this game");
        assertNotNull(outputGuess.getScrambleWord(),"`scrambleWord` is not null");
        assertEquals(inputWord, outputGuess.getGuessWord(),"`guessWord` equals to input `guessWord`");
        assertEquals(outputNewGame.getTotalWords(), outputGuess.getTotalWords(),
                "`totalWords` is equals to `totalWords` of this game");
        assertEquals(outputNewGame.getRemainingWords(), outputGuess.getRemainingWords(),
                "`remainingWords` is equals to `remainingWords` of previous game state (no change)");
        assertTrue(outputGuess.getGuessedWords().isEmpty(),
                "`guessedWords` is empty list (because this is first attempt)");
    }

    @Test
    void givenCreateNewGame_whenSubmitFirstCorrectWord_thenGuessedCorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed correctly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords - 1` of previous game state (decrement by 1)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder requestNewGame = MockMvcRequestBuilders.get("/api/game/new");
        MockHttpServletResponse responseNewGame = mvc.perform(requestNewGame).andReturn().getResponse();
        GameGuessOutput outputNewGame = mapper.readValue(responseNewGame.getContentAsString(), GameGuessOutput.class);

        String gameId = outputNewGame.getId();

        List<String> subWords = gameApiController.readValue(gameId);
        String inputWord = subWords.get(0);

        MockHttpServletRequestBuilder requestGuess = MockMvcRequestBuilders.post("/api/game/guess");
        requestGuess.contentType(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.accept(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.content("{\"id\":\"" + gameId + "\",\"word\":\"" + inputWord + "\"}");

        MockHttpServletResponse responseGuess = mvc.perform(requestGuess).andReturn().getResponse();
        GameGuessOutput outputGuess = mapper.readValue(responseGuess.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.OK.value(), responseGuess.getStatus(),"HTTP status == 200");
        assertEquals("Guessed correctly.", outputGuess.getResult(),
                "`result` equals \"Guessed correctly.\"");
        assertEquals(outputNewGame.getId(), outputGuess.getId(),"`id` equals to `id` of this game");
        assertEquals(outputNewGame.getOriginalWord(), outputGuess.getOriginalWord(),
                "`originalWord` is equals to `originalWord` of this game");
        assertNotNull(outputGuess.getScrambleWord(),"`scrambleWord` is not null");
        assertEquals(inputWord, outputGuess.getGuessWord(),"`guessWord` equals to input `guessWord`");
        assertEquals(outputNewGame.getTotalWords(), outputGuess.getTotalWords(),
                "`totalWords` is equals to `totalWords` of this game");
        assertEquals(outputNewGame.getRemainingWords() - 1, outputGuess.getRemainingWords(),
                "`remainingWords` is equals to `remainingWords` of previous game state (no change)");
        assertFalse(outputGuess.getGuessedWords().isEmpty(),
                "`guessedWords` is not empty list");
        assertTrue(outputGuess.getGuessedWords().contains(inputWord),
                "`guessWords` contains input `guessWord`");
    }

    @Test
    void givenCreateNewGame_whenSubmitAllCorrectWord_thenAllGuessed() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         * b) has submit all correct answers, except the last answer
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of the last correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "All words guessed."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is 0 (no more remaining, game ended)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder requestNewGame = MockMvcRequestBuilders.get("/api/game/new");
        MockHttpServletResponse responseNewGame = mvc.perform(requestNewGame).andReturn().getResponse();
        GameGuessOutput outputNewGame = mapper.readValue(responseNewGame.getContentAsString(), GameGuessOutput.class);

        String gameId = outputNewGame.getId();

        List<String> subWords = gameApiController.readValue(gameId);
        String inputWord = "";

        MockHttpServletRequestBuilder requestGuess = MockMvcRequestBuilders.post("/api/game/guess");
        requestGuess.contentType(MediaType.APPLICATION_JSON_VALUE);
        requestGuess.accept(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse responseGuess = new MockHttpServletResponse();

        for(String w: subWords){
            inputWord = w;

            requestGuess.content("{\"id\":\"" + gameId + "\",\"word\":\"" + inputWord + "\"}");
            responseGuess = mvc.perform(requestGuess).andReturn().getResponse();
        }

        GameGuessOutput outputGuess = mapper.readValue(responseGuess.getContentAsString(), GameGuessOutput.class);

        assertEquals(HttpStatus.OK.value(), responseGuess.getStatus(),"HTTP status == 200");
        assertEquals("All words guessed.", outputGuess.getResult(),
                "`result` equals \"All words guessed.\"");
        assertEquals(outputNewGame.getId(), outputGuess.getId(),"`id` equals to `id` of this game");
        assertEquals(outputNewGame.getOriginalWord(), outputGuess.getOriginalWord(),
                "`originalWord` is equals to `originalWord` of this game");
        assertNotNull(outputGuess.getScrambleWord(),"`scrambleWord` is not null");
        assertEquals(inputWord, outputGuess.getGuessWord(),"`guessWord` equals to input `guessWord`");
        assertEquals(outputNewGame.getTotalWords(), outputGuess.getTotalWords(),
                "`totalWords` is equals to `totalWords` of this game");
        assertEquals(0, outputGuess.getRemainingWords(),
                "`remainingWords` is 0 (no more remaining, game ended)");
        assertFalse(outputGuess.getGuessedWords().isEmpty(),
                "`guessedWords` is not empty list");
        assertTrue(outputGuess.getGuessedWords().contains(inputWord),
                "`guessWords` contains input `guessWord`");
    }

}
