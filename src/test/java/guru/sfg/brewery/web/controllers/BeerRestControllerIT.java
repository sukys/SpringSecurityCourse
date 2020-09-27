package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by jt on 6/12/20.
 */
// @WebMvcTest
@SpringBootTest
public class BeerRestControllerIT extends BaseIT{


    @Test
    void deleteBeerFromUrlParameter() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/8f0d99df-62f6-4e70-958f-d0b5b1a07b7e")
                        .param("username", "spring")
                        .param("password", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    void unauthorizedDeleteBeerFromUrlParameter() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/8f0d99df-62f6-4e70-958f-d0b5b1a07b7e")
                        .param("username", "spring")
                        .param("password", "guru_not"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void deleteBeerBadCredential() throws Exception{
        mockMvc.perform(
                delete("/api/v1/beer/8f0d99df-62f6-4e70-958f-d0b5b1a07b7e")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guru_not"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void deleteBeer() throws Exception{
        mockMvc.perform(
                delete("/api/v1/beer/8f0d99df-62f6-4e70-958f-d0b5b1a07b7e")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("spring")
    void findBeers() throws Exception{
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception{
        mockMvc.perform(get("/api/v1/beer/8f0d99df-62f6-4e70-958f-d0b5b1a07b7e"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception{
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }

}