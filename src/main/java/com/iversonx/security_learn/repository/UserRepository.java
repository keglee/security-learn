package com.iversonx.security_learn.repository;

import com.iversonx.security_learn.entity.DcUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 **/
@Repository
public interface UserRepository extends CrudRepository<DcUser, Long> {
    DcUser findByUserId(String UserID);
}
