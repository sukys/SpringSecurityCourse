package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by jt on 6/12/20.
 */
// @WebMvcTest
@SpringBootTest
public class BeerRestControllerIT extends BaseIT{

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {

        public Beer beerToDelete() {
            Random rand = new Random();

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete me beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build() );
        }

        @Test
        void deleteBeerHttpBasic() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerHttpBasicUserRole() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }


        @Test
        void deleteBeerHttpBasicCustomerRole() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }


        @Test
        void deleteBeerFromUrlParameter() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .param("username", "spring")
                            .param("password", "guru"))
                    .andExpect(status().isOk());
        }


        @Test
        void unauthorizedDeleteBeerFromUrlParameter() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .param("username", "spring")
                            .param("password", "guru_not"))
                    .andExpect(status().isUnauthorized());
        }


        @Test
        void deleteBeerBadCredential() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-Key", "spring")
                            .header("Api-Secret", "guru_not"))
                    .andExpect(status().isUnauthorized());
        }


        @Test
        void deleteBeer() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-Key", "spring")
                            .header("Api-Secret", "guru"))
                    .andExpect(status().isOk());
        }

    }

    @Test
    @WithMockUser("spring")
    void findBeers() throws Exception{
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception{
        Beer beer = beerRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception{
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }

}