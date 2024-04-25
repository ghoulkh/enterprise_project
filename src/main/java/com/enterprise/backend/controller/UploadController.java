package com.enterprise.backend.controller;

import com.enterprise.backend.service.UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/uploader")
@RequiredArgsConstructor
public class UploadController {

    private final UploaderService uploaderService;

    @PostMapping
    public ResponseEntity<Map<String, String>> upload(@RequestBody MultipartFile multipartFile) throws Exception {
        String fileName = multipartFile.getOriginalFilename();

        String url = uploaderService.saveImage(multipartFile.getInputStream());
        Map<String, String> resp = new HashMap<>();
        resp.put("url", url);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
