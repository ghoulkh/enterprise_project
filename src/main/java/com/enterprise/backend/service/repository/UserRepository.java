package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseCommonRepository<User, String> {
    @Query("SELECT u FROM User u where u.isActive = ?1")
    Page<User> getAllByActive(boolean isActive, Pageable pageable);

    @Query(value = "SELECT u.* FROM user u INNER JOIN authority a ON a.username = u.username WHERE a.role = ?1", nativeQuery = true)
    Page<User> getAllByRole(String role, Pageable pageable);

    @Query(value = "SELECT u.* FROM enterprise_project.user u INNER JOIN enterprise_project.authority a ON a.username = u.username " +
            "WHERE u.is_active = 1 GROUP BY a.username HAVING count(a.`role`) = 1", nativeQuery = true)
    Page<User> getAllByRole(Pageable pageable);

    @Query(value = "SELECT * FROM enterprise_project.user WHERE MATCH (phone, email) AGAINST (?1)", nativeQuery = true)
    List<User> searchByEmailOrPhone(String keyword);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}
