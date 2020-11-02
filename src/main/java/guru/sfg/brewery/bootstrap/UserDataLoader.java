package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
class Loader implements CommandLineRunner {


    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(authorityRepository.count() == 0){
            loadSecurityData();
        }
    }

    private void loadSecurityData() {

        // Beer Auths
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().permission("beer.delete").build());

        // Customer Auths
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        // Brewery Auths
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());


        // Beer Order Auths
        Authority createOrder = authorityRepository.save(Authority.builder().permission("order.create").build());
        Authority readOrder = authorityRepository.save(Authority.builder().permission("order.read").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().permission("order.delete").build());

        // Customer Beer Order Auth
        Authority createOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.create").build());
        Authority readOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.delete").build());




        Role adminRole = roleRepository.save(Role.builder().roleName("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().roleName("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().roleName("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, readBeer, updateBeer, deleteBeer, createCustomer,
                readCustomer, updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery,
                deleteBrewery, createOrder, readOrder, updateOrder, deleteOrder)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readBrewery, readCustomer, createOrderCustomer, readOrderCustomer, updateOrderCustomer, deleteOrderCustomer)));
        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        createUser("spring", "guru", adminRole);
        createUser("user", "password", userRole);
        createUser("scott", "tiger", customerRole);

        //createUser("scott_15", "tiger", customerRole);

        log.debug("Users loaded: " + userRepository.count() + ".");
    }

    private User createUser(String username, String password, Role role){
        Role roleFromDb = roleRepository.findById(role.getId()).get();
        return userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(roleFromDb)
                .build());
    }

}
