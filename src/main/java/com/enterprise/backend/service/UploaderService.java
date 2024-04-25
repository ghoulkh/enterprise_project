package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.error.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Log4j2
public class UploaderService {


    @Value("${images.base.path}")
    private String basePath;
    @Value("${images.domain}")
    private String cdnDomain;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public String saveImage(InputStream inputStream) {
        String pathFile = makeImageName();
       try {
           BufferedImage img = ImageIO.read(inputStream);
           img = scale(img, 1);
           ImageIO.write(img, "jpg", new File(basePath + pathFile));
       } catch (Exception e) {
           log.error(e.getMessage(), e);
           throw new EnterpriseBackendException(ErrorCode.INTERNAL_SERVER);
       }
       return cdnDomain + pathFile;
    }

    private String makeImageName() {
        return getPath() + UUID.randomUUID() + ".jpg";
    }

    private String getPath() {
        Date currentDate = new Date();
        String path = basePath;
        String result = "";
        String[] dateString = dateFormat.format(currentDate).split("/");
        for (int i =0; i < dateString.length; i ++) {
            path = path  + "/" + dateString[i];
            result = result + "/" + dateString[i];
            File file = new File(path);
            if (!file.exists())
                file.mkdir();
        }
        path = path + "/";
        return result + "/";
    }

    private BufferedImage scale(BufferedImage source, double ratio) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        Graphics2D g2d = source.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale,yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return source;
    }
}
