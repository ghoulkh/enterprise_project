package com.enterprise.backend.service;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.Product;
import com.enterprise.backend.model.entity.Review;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.exception.BannedUserException;
import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ReviewRequest;
import com.enterprise.backend.model.request.UserRequest;
import com.enterprise.backend.model.request.UpdateUserRequest;
import com.enterprise.backend.model.response.ReviewResponse;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.AuthorityRepository;
import com.enterprise.backend.service.repository.ProductRepository;
import com.enterprise.backend.service.repository.ReviewRepository;
import com.enterprise.backend.service.repository.UserRepository;
import com.enterprise.backend.security.SecurityUtil;
import com.enterprise.backend.service.transfomer.ReviewTransformer;
import com.enterprise.backend.service.transfomer.UserTransformer;
import com.enterprise.backend.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService<User, String, UserRepository, UserTransformer, UserRequest, UserResponse> implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final ReviewTransformer reviewTransformer;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final AuthorityRepository authorityRepository;

    protected UserService(UserRepository repo,
                          UserTransformer transformer,
                          EntityManager em,
                          PasswordEncoder passwordEncoder,
                          ReviewTransformer reviewTransformer,
                          ReviewRepository reviewRepository,
                          ProductRepository productRepository,
                          AuthorityRepository authorityRepository) {
        super(repo, transformer, em);
        this.passwordEncoder = passwordEncoder;
        this.reviewTransformer = reviewTransformer;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public UserResponse registrationUser(UserRequest request) {
        if (repo.findByEmail(request.getEmail()).isPresent())
            throw new EnterpriseBackendException(ErrorCode.CONFLICT_EMAIL);
        if (repo.findByPhone(request.getPhone()).isPresent())
            throw new EnterpriseBackendException(ErrorCode.CONFLICT_PHONE);

        User userToSave = transformer.toEntity(request);
        userToSave.setId(UUID.randomUUID().toString());
        userToSave.setPassword(passwordEncoder.encode(request.getPassword()));

        User result = repo.save(userToSave);
        Authority authority = new Authority();
        authority.setUser(result);
        authority.setRole(Authority.Role.ROLE_USER);
        authorityRepository.save(authority);
        return transformer.toResponse(result);
    }

    public User getByUsername(String username) {
        return getOrElseThrow(username, "Username not found!");
    }

    public User getByUsernameOrEmailOrPhone(String dataToGet) {
        Optional<User> userOptional = repo.findById(dataToGet);
        if (userOptional.isEmpty())
            userOptional = repo.findByEmail(dataToGet);
        if (userOptional.isEmpty())
            userOptional = repo.findByPhone(dataToGet);
        if (userOptional.isEmpty())
            throw new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND);
        return userOptional.get();
    }

    public Optional<User> findByUsername(String username) {
        return repo.findById(username);
    }

    public UserResponse getProfile() {
        String currentUser = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(currentUser)) {
            throw new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND);
        }
        User user = getByUsername(currentUser);
        return UserResponse.from(user);
    }


    public UserResponse getProfileByUsername(String username) {
        User user = getByUsername(username);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(UpdateUserRequest request) {
        String currentUser = SecurityUtil.getCurrentUsername();
        User userToUpdate = getByUsername(currentUser);
        if (request.getPhone() != null && !userToUpdate.getPhone().equals(request.getPhone()) &&
                repo.findByPhone(request.getPhone()).isPresent())
            throw new EnterpriseBackendException(ErrorCode.CONFLICT_PHONE);
        if (request.getEmail() != null && !userToUpdate.getEmail().equals(request.getEmail()) &&
                repo.findByEmail(request.getEmail()).isPresent())
            throw new EnterpriseBackendException(ErrorCode.CONFLICT_PHONE);
        request.updateUser(userToUpdate);
        return transformer.toResponse(repo.save(userToUpdate));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.findById(username);
        if (user.isEmpty())
            user = repo.findByPhone(username);
        if (user.isEmpty())
            user = repo.findByEmail(username);
        if (user.isEmpty()) {
            throw new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND);
        }
        if (!user.get().isActive()) {
            throw new BannedUserException("Banded perform action!");
        }
        List<String> roles = user.get().getAuthorities().stream().map(authority ->
                authority.getRole().getValue()).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.get().getId(),
                user.get().getPassword() != null ? user.get().getPassword() : "",
                getAuthorities(roles));
    }

    private List<SimpleGrantedAuthority> getAuthorities(List<String> roles) {
        List<SimpleGrantedAuthority> result = new ArrayList<>();
        for (String role : roles) {
            result.add(new SimpleGrantedAuthority(role));
        }
        return result;
    }

    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        Review review = reviewTransformer.toEntity(request);
        List<Integer> rates = reviewRepository.findAllByProduct(product)
                .stream().map(Review::getRate).collect(Collectors.toList());
        rates.add(request.getRate());
        product.setRate(Utils.tbc(rates));
        productRepository.save(product);

        review.setProduct(product);
        String userId = SecurityUtil.getCurrentUsername();
        if (StringUtils.isEmpty(userId)) {
            userId = request.getUserId();
        }
        if (StringUtils.isNotEmpty(userId)) {
            get(userId).ifPresent(review::setUser);
        }
        return reviewTransformer.toResponse(reviewRepository.save(review));
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }

    @Override
    protected String notFoundMessage() {
        return "Not found user";
    }
}
