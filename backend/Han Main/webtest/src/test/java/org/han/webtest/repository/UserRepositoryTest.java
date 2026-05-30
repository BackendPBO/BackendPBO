package org.han.webtest.repository;

import org.han.webtest.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testSimpanUser() {
        UserModel user = new UserModel("Budi", "budi@mail.com", "password123");
        userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void testCariUserByEmail() {
        UserModel user = new UserModel("Ani", "ani@mail.com", "password123");
        userRepository.save(user);

        Optional<UserModel> found = userRepository.findByEmail("ani@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("Ani");
    }

    @Test
    void testEmailTidakAda() {
        Optional<UserModel> found = userRepository.findByEmail("tidakada@mail.com");
        assertThat(found).isEmpty();
    }
}
