package funix.tgcp.tour.image;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.tour.Tour;
import funix.tgcp.tour.TourRepository;
import funix.tgcp.util.FileHelper;

@Service
public class TourImageService {
	
	@Autowired
	private TourImageRepository tourImageRepository;
	
	@Autowired
	private TourRepository tourRepository;
	
	@Autowired
	private FileHelper fileHelper;

	public boolean uploadImages(Long tourId, MultipartFile[] files) {
		Optional<Tour> tour = tourRepository.findById(tourId);
		if(tour.isPresent()) {
			for(MultipartFile file: files ) {
				TourImage image=  new TourImage();
				image.setTour(tour.get());				
				try {
					image.setUrl(fileHelper.uploadFile(file));
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
			try {
				fileHelper.deleteFile(imageToDelete.get().getUrl());
			} catch (IOException e) {
				e.printStackTrace();
			} 			
			tourImageRepository.delete(imageToDelete.get());
		}
		
	}

}
