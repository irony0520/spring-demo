package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath,String originalFileName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        File targetDir = new File(uploadPath);
        if(!targetDir.exists()) {
           if(!targetDir.mkdirs()) {
               log.info("이미지 저장경로 생성 문제");
               throw new RuntimeException();
           }
        }
        //이미지 파일 서버에 등록
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        //이미지 데이터를 fileData에 넣음
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);

        //java 에서 제공하는 file.delete 메소드
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일이 삭제됐습니다.");
        }else{
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
