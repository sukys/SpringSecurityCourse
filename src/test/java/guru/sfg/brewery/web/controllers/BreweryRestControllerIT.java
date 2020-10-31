package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by jt on 6/12/20.
 */
// @WebMvcTest
@SpringBootTest
public class BreweryRestControllerIT extends BaseIT{

    @Nested
    @DisplayName("List Breweries")
    class ListBreweries{

        @ParameterizedTest(name="#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAdminAndCustomer")
        void listBreweriesAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/brewery/breweries")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void listBreweriesUSER() throws Exception {
            mockMvc.perform(get("/brewery/breweries")
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void listBreweriesNOAUTH() throws Exception {
            mockMvc.perform(
                    get("/brewery/breweries"))
                    .andExpect(status().isUnauthorized());
        }




        @ParameterizedTest(name="#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAdminAndCustomer")
        void listBreweriesJson(String user, String pwd) throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void listBreweriesJsonUSER() throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries")
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void listBreweriesJsonNOAUTH() throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries"))
                    .andExpect(status().isUnauthorized());
        }


    }


}