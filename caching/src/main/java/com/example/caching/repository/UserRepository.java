package com.example.caching.repository;


import com.example.caching.UserView;
import com.example.caching.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findAllByDeletedAndScopeOrderByCreationDateDesc(boolean deleted, String scope);

    Optional<Users> findUserByUsernameAndScope(String Username, String scope);

    Optional<Users> findUserByUsernameAndScopeAndDeletedAndDisabled(String Username, String scope, boolean deleted, boolean disabled);

    Optional<Users> findByIdAndScopeAndDisabledAndDeleted(long id, String scope, boolean disabled, boolean deleted);

    Optional<Users> findByIdAndScopeAndDeleted(long id, String scope, boolean deleted);

    Optional<Users> findByClientIdAndScopeAndDeleted(String clientId, String scope, boolean deleted);

    @Modifying
    @Query(value = "update Users u set u.deleted =:deleted where  u.id=:id")
    void updateState(@Param("deleted") boolean deleted, @Param("id") long id);

    @Query(value = "select u.ID ,U.DISABLED from Users u inner join provider p on u.ID=p.User_ID and u.DELETED = 0 and p.code= :providerCode", nativeQuery = true)
    Optional<UserView> findUserByProviderCode(String providerCode);
}