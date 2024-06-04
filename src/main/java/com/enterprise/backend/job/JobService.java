package com.enterprise.backend.job;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.request.UserRequest;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.AdminService;
import com.enterprise.backend.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobService {
    final UserService userService;
    final AdminService adminService;

    @Value("${app.admin.username}")
    private String usernameAdmin;
    @Value("${app.admin.password}")
    private String passwordAdmin;
    @Value("${app.admin.mail}")
    private String emailAdmin;
    @Value("${app.admin.phone}")
    private String phoneAdmin;

    @PostConstruct
    public void jobService() {
        new Thread(this::registerAdmin).start();
        new Thread(this::createImageFolder).start();
    }

    private void registerAdmin() {
        try {
            if (userService.findByUsername(this.usernameAdmin).isPresent()) return;
            UserRequest request = new UserRequest();
            request.setEmail(emailAdmin);
            request.setPhone(phoneAdmin);
            request.setFullName("ADMIN");
            request.setPassword(this.passwordAdmin);
            UserResponse userResponse = userService.registrationUser(request);

            adminService.addAuthorityWithUsername(userResponse.getId(), Authority.Role.ROLE_SUPER_ADMIN);
            log.info("Register admin done!");
        } catch (Exception e) {
            log.info("Đã tạo tài khoản admin");
        }
    }

    private void createImageFolder() {
        File imageFolder = new File("./image");
        if (!imageFolder.exists()) {
            log.info("Create folder image done: {}", imageFolder.mkdir());
        }
    }

}
