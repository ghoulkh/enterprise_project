package com.enterprise.backend.service;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.exception.EnterpriseBackendException;
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

    private List<User> getUserAndPaging(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.getContent();
    }

    public List<UserResponse> getByType(int pageIndex, int pageSize, String type) {
        List<User> users;
        if ("BANNED".equals(type)) {
            Pageable paging = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
            users = userRepository.getAllByActive(false, paging);
        } else if ("USER".equals(type)) {
            Pageable paging = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "created_date"));
            users = userRepository.getAllByRole(paging);
        } else if (type != null) {
            Pageable paging = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "created_date"));
            users = userRepository.getAllByRole(type, paging);
        } else {
            Pageable paging = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
            users = getUserAndPaging(paging);
        }
        return users.stream().map(UserResponse::from).collect(Collectors.toList());
    }

    public List<UserResponse> searchByEmailOrPhone(String keyword) {
        List<User> users = userRepository.searchByEmailOrPhone(keyword);
        return users.stream().map(UserResponse::from).collect(Collectors.toList());
    }

    public void addAuthorityWithUsername(String username, Authority.Role role) {
        User userToAdd = userRepository.findById(username).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));
        Authority authority = new Authority();
        authority.setRole(role);
        authority.setUser(userToAdd);
        authorityRepository.save(authority);
    }

    public UserResponse banUser(String username) {
        User userToBan = userRepository.findById(username).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));
        userToBan.setActive(false);
        return UserResponse.from(userRepository.save(userToBan));
    }

}
