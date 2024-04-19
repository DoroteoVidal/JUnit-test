package com.dorot.test.springboot.app.repository;

import com.dorot.test.springboot.app.model.Account;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPerson(String person);
}
