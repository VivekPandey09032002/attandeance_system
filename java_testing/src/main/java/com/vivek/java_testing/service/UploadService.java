package com.vivek.java_testing.service;

import java.io.IOException;

public interface UploadService {
    void saveImage(String fileName,String imageData) throws IOException;
}
