package com.vivek.java_testing.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@Log4j2
public class UploadServiceImpl implements UploadService{
    @Override
    public void saveImage(String fileName, String imageData) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(".")).getFile());
        byte[] bytesData = DatatypeConverter.parseBase64Binary(imageData);
        Path path=  Paths.get(file.getAbsolutePath(),"images");
        log.debug("file path : {}",path);
        Files.createDirectories(path);
        log.debug("is directory exist : {}", Files.isDirectory(path));
        Files.write(Paths.get(file.getAbsolutePath(),"images",fileName),bytesData);
        log.debug("saving image to the path {}",Paths.get(file.getAbsolutePath(),"images",fileName));
    }
}
