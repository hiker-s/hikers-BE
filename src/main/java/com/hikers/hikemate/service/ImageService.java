package com.hikers.hikemate.service;

import com.hikers.hikemate.entity.CrewPost;
import com.hikers.hikemate.entity.Image;
import com.hikers.hikemate.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private S3Service s3Service;
    @Autowired
    private ImageRepository imageRepository;

    public List<Image> saveImages(List<MultipartFile> images, CrewPost post) {
        List<Image> imageList = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                String imageUrl = s3Service.uploadImage(image);  // S3에 이미지 업로드
                Image newImage = new Image();
                newImage.setImageUrl(imageUrl);
                newImage.setCrewPost(post);
                imageList.add(newImage);  // 이미지 리스트에 추가
            } catch (IOException e) {
                // 예외 처리: 로깅 또는 적절한 오류 메시지 출력
                e.printStackTrace();
            }
        }

        return imageRepository.saveAll(imageList);
    }

    public void deleteImagesByPost(CrewPost post) {
        for (Image image : post.getImages()) {
            s3Service.deleteImage(image.getImageUrl());  // S3에서 이미지 삭제

        }
        imageRepository.deleteAll(post.getImages());
        post.getImages().clear();

    }
}
