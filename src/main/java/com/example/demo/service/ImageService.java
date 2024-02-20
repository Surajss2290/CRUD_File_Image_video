package com.example.demo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Repository.ImageRepository;
import com.example.demo.entity.ImageEntity;

@Service
public  class ImageService  {
	@org.springframework.beans.factory.annotation.Value(value = "")
    private String imagePath;

    @Autowired
    private ImageRepository imageRepository;
    
    
    //Upload Image to file system and store the path into db
    public ImageEntity saveImage(MultipartFile file) throws IllegalStateException, IOException {
    
    	String filepath="D:\\work\\CRUD_File_Image_video\\image\\"+file.getOriginalFilename();
    	file.transferTo(new File(filepath));
    	
    	String imagename=file.getOriginalFilename();
    	
    	ImageEntity imageEntity=new ImageEntity();
    	imageEntity.setImagepath(filepath);
        imageEntity.setImagename(imagename);
        imageEntity.setFilepath(filepath);
    	
    	return imageRepository.save(imageEntity);
    	
    }
    
    //------------------------------------------------------------------------------------------------
    public ImageEntity updateImage(Long imageId, MultipartFile newImageFile)   {
      
    	   // Check if the image exists
           Optional<ImageEntity> optionalImage = imageRepository.findById(imageId);
           if (optionalImage.isPresent()) {
               // Get the existing image
               ImageEntity image = optionalImage.get();

               // Update image in the file system
               updateImageInFileSystem(image, newImageFile);

               // Update the image data in the database
              return imageRepository.save(image);
           }
		return null;
	
	
	
	
    }

    private void updateImageInFileSystem(ImageEntity image, MultipartFile newImageFile) {
        // Specify the path to the directory where images are stored
        String directoryPath = "D:\\work\\CRUD_File_Image_video\\image\\";

        // Create a File object with the path to the existing image file
        File existingImageFile = new File(directoryPath + image.getImagename());

        // Generate a new unique file name for the updated image
        String newImageName = newImageFile.getOriginalFilename();
        File ImageFile = new File(directoryPath + newImageName);

        // Update the image name and path in the database
        image.setImagename(newImageName);
        image.setImagepath(directoryPath + newImageName);
        image.setFilepath(directoryPath + newImageName);

        // Update the file in the file system
        try {
            Path existingPath = existingImageFile.toPath();
            Path newPath = ImageFile.toPath();
            Files.copy(newImageFile.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(existingPath);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it or throw a custom exception)
        }
    }

    
    
   
 
    
    
//------------------------------------------------------------------------------------------------------
	//Find the image by using by its name

	public byte[] getImageData(String imageName) throws IOException {
    	String imagepath="D:\\work\\CRUD_File_Image_video\\image\\"+imageName;
    
    	 try (InputStream inputStream= new FileInputStream(imagepath)) {
             byte[] imageBytes = inputStream.readAllBytes();
             return imageBytes;
         }
        
		
		
	}

	   //Find the image by using by its id
	  public Optional<ImageEntity> getImageById(Long id) {
	        return imageRepository.findById(id);
	    }
	  

	 
	  public String deleteImage(Long imageId) {
	        // Check if the image exists
	        if (imageRepository.existsById(imageId)) {
	            // Get the image by ID
	            ImageEntity image = imageRepository.findById(imageId).orElse(null);
	            if (image != null) {
	            	
	                // Delete from file system
	                deleteImageFromFileSystem(image.getImagename());

	                // Delete from database
	                imageRepository.delete(image);
	            }
	            return "Image "+image.getImagename()+" Delete Successfully";
	        }
			
			return "Record not found";
	    }
	  
	  private void deleteImageFromFileSystem(String imageName) {
	        // Specify the path to the directory where images are stored
		  String filepath="D:\\work\\CRUD_File_Image_video\\image\\";

	        // Create a File object with the path to the image file
	        File fileToDelete = new File(filepath + imageName);

	        // Delete the file
	        if (fileToDelete.exists()) {
	            fileToDelete.delete();
	        }
	    }






}  
  

