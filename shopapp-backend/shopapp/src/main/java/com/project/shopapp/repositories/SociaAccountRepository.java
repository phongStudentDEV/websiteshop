package com.project.shopapp.repositories;

import com.project.shopapp.models.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SociaAccountRepository extends JpaRepository<SocialAccount, Long> {
}
