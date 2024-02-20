package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.ImageEntity;
import com.example.demo.service.ImageService;



import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController

public class ImageController {

    @Autowired
    private ImageService imageService;

    
    //Upload the image to file system and path will be stored in db
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file){
    	try {
			ImageEntity savedImage=imageService.saveImage(file);
			return ResponseEntity.ok("File Uploaded Successfully. Image Id: "+ savedImage.getId());
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail to upload file");
		}
    	 
    	
    }
  
    //---------------------------------------------------------------------------------------------------
    
    
    @PutMapping("/images/{imageId}")
    public  ResponseEntity<String> updateImage(
            @PathVariable Long imageId,
            @RequestParam("file") MultipartFile newImageFile) {
    	 ImageEntity update=imageService.updateImage(imageId, newImageFile);
    
    	
                       if (update!=null) {
   	               return ResponseEntity.ok("File update ss Successfully");
   	
                    }
	
	
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Record not found");
	
	
   


    }
    
    
    
    //---------------------------------------------------------------------------------------------------
    //Get the image from file system using path which is stored in database
    @GetMapping("getimage/{imageName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String imageName) throws IOException {
    	byte[] imageData=imageService.getImageData(imageName);
    	
    if (imageData!=null) {
    	InputStreamResource resource=new InputStreamResource(new ByteArrayInputStream(imageData));
    	return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
		
	}else {
		return ResponseEntity.notFound().build();
     
    }

   
}
    
    //Get image By using id
    @GetMapping("getimageid/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Optional<ImageEntity> imageEntityOptional = imageService.getImageById(id);

        if (imageEntityOptional.isPresent()) {
            ImageEntity imageEntity = imageEntityOptional.get();
            try {
                Path imagePath = Paths.get(imageEntity.getFilepath());
                byte[] imageBytes = Files.readAllBytes(imagePath);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // Adjust content type as needed

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                // Handle the exception appropriately (e.g., log it)
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    

   //Delete the image by id
    @DeleteMapping("/delete/{imageId}")
    public String deleteImage(@PathVariable Long imageId) {
        String pojo=imageService.deleteImage(imageId);
		return pojo;
    }
    

   
}


