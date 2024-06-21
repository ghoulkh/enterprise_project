package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseCommonRepository<User, String> {
    @Query(value = "SELECT u.* FROM user u where u.is_active = :isActive AND (:keyword IS NULL OR MATCH (phone, email) AGAINST (:keyword))", nativeQuery = true)
    Page<User> getAllByActive(boolean isActive, String keyword, Pageable pageable);

    @Query(value = "SELECT u.* FROM user u INNER JOIN authority a ON a.user_id = u.id WHERE a.role = :role AND (:keyword IS NULL OR MATCH (phone, email) AGAINST (:keyword))", nativeQuery = true)
    Page<User> getAllByRole(String role, String keyword, Pageable pageable);

    @Query(value = "SELECT u.* FROM enterprise_project.user u INNER JOIN enterprise_project.authority a ON a.user_id = u.id " +
            "WHERE u.is_active = 1 AND (:keyword IS NULL OR MATCH (phone, email) AGAINST (:keyword)) GROUP BY a.user_id HAVING count(a.`role`) = 1", nativeQuery = true)
    Page<User> getAllByRole(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM enterprise_project.user WHERE (:keyword IS NULL OR MATCH (phone, email) AGAINST (:keyword))", nativeQuery = true)
    List<User> searchByEmailOrPhone(String keyword);

    @Query(value = "SELECT u.* FROM enterprise_project.user u INNER JOIN enterprise_project.authority a ON a.user_id = u.id " +
            "WHERE a.role = 'ROLE_ADMIN' OR a.role = 'ROLE_SUPER_ADMIN' GROUP BY a.user_id", nativeQuery = true)
    Optional<List<User>> findAllAdmin();

    @Query(value = "SELECT u.* FROM enterprise_project.user u " +
            "WHERE (:keyword IS NULL OR MATCH (phone, email) AGAINST (:keyword))", nativeQuery = true)
    Page<User> findByKeyWord(String keyword, Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}
