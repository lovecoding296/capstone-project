package funix.tgcp.tour.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.tour.Tour;
import funix.tgcp.tour.TourRepository;
import funix.tgcp.util.FileUploadHelper;

@Service
public class TourImageService {
	
	@Autowired
	private TourImageRepository tourImageRepository;
	
	@Autowired
	private TourRepository tourRepository;
	
	@Autowired
	private FileUploadHelper fileUploadHelper;

	public boolean uploadImages(Long tourId, MultipartFile[] files) {
		Optional<Tour> tour = tourRepository.findById(tourId);
		if(tour.isPresent()) {
			for(MultipartFile file: files ) {
				TourImage image=  new TourImage();
				image.setTour(tour.get());				
				try {
					image.setUrl(fileUploadHelper.uploadFile(file));
					tourImageRepository.save(image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return true;
		}
		return false;
	}

	public void deleteById(Long id) {
		Optional<TourImage> imageToDelete = tourImageRepository.findById(id);		
		if(imageToDelete.isPresent()) {
			Path path = Paths.get(imageToDelete.get().getUrl());
			try {
				Files.deleteIfExists(path);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			tourImageRepository.delete(imageToDelete.get());
		}
		
	}

}
