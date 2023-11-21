package com.netcom.logintaptophone.db.key;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllowedKeysRepository extends JpaRepository<AllowedKeys, Integer> {
    Optional<AllowedKeys> findAllowedKeysByHostsContaining(String hosts);

}
