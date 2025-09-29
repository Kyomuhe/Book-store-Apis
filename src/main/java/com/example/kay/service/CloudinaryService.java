package com.example.kay.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Autowired
    public  CloudinaryService (Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadFile(MultipartFile file) throws IOException {
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("file size exceeds 2mbs");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp")
                        )
        ) {
            throw new IllegalArgumentException("file content type not supported");
        }
        return cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
    }


    public String generateThumbnail(String publicId) {
        return cloudinary.url()
                .transformation(new Transformation()
                        .width(150)
                        .height(150)
                        .crop("fill")
                        .quality("auto"))
                .generate(publicId);
    }

}

