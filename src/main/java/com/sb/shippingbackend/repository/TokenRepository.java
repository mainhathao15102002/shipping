package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
        select t from Token t inner join t.user u
        where u.id = :userId and t.loggedOut = false
    """)
    List<Token> findAllTokenByUser(Integer userId);


    Optional<Token> findByToken(String token);
}
