package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.repository.AuthorityRepository;
import com.enterprise.backend.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public Page<UserResponse> getByType(int pageIndex, int pageSize, String type, String keyword) {
        Pageable paging = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "created_date"));
        Page<User> userPage;

        if ("BANNED".equals(type)) {
            userPage = userRepository.getAllByActive(false, keyword, paging);
        } else if ("USER".equals(type)) {
            userPage = userRepository.getAllByRole(keyword, paging);
        } else if (type != null) {
            userPage = userRepository.getAllByRole(type, keyword, paging);
        } else {
            userPage = userRepository.findByKeyWord(keyword, paging);
        }

        return userPage.map(UserResponse::from);
    }

    public List<UserResponse> searchByEmailOrPhone(String keyword) {
        List<User> users = userRepository.searchByEmailOrPhone(keyword);
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public void addAuthorityWithUserId(String userId, Authority.Role role) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        if (role == Authority.Role.ROLE_USER) {
            deleteAdminWithUserId(userId);
            return;
        }

        Authority authority = new Authority();
        authority.setRole(role);
        authority.setUser(user);
        authorityRepository.save(authority);
    }

    public void deleteAdminWithUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        Authority authority = authorityRepository.findByUserAndRole(user, Authority.Role.ROLE_ADMIN).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.ROLE_NOT_FOUND));

        authorityRepository.delete(authority);
    }

    public UserResponse banUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        user.setActive(false);
        return UserResponse.from(userRepository.save(user));
    }
}
