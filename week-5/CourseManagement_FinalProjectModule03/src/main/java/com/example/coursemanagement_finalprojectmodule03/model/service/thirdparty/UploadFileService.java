package com.example.coursemanagement_finalprojectmodule03.model.service.thirdparty;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UploadFileService {
    private final Cloudinary cloudinary;

    public String uploadFileToCloudinary(MultipartFile file,String folderName,String publicId) {
        try {
            //PHẢI CÓ "resource_type", "auto" THÌ NÓ MỚI NHẬN ĐƯỢC VIDEO VÀ PDF!
            Map uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folderName,
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return uploadResult.get("secure_url") != null
                    ? uploadResult.get("secure_url").toString()
                    : uploadResult.get("url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi trong quá trình upload file lên Cloudinary: " + e.getMessage());
        }
    }
}