package fr.univcotedazur.simpletcfs.cli.commands;

import fr.univcotedazur.simpletcfs.cli.model.Cookies;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RecipeCommands.class)
public class recipeCommandsTest {

     @Autowired
     private RecipeCommands client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void recipesSetTest() {

        server
                .expect(requestTo(RecipeCommands.CMD_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"CHOCOLALALA\",\"DARK_TEMPTATION\",\"SOO_CHOCOLATE\"]", MediaType.APPLICATION_JSON));

        assertEquals(EnumSet.allOf(Cookies.class), client.recipes());
    }

}
