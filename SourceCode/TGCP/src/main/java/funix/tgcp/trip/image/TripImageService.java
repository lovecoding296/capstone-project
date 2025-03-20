package funix.tgcp.trip.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.trip.Trip;
import funix.tgcp.trip.TripRepository;
import funix.tgcp.util.FileUploadHelper;

@Service
public class TripImageService {
	
	@Autowired
	private TripImageRepository tripImageRepository;
	
	@Autowired
	private TripRepository tripRepository;
	
	@Autowired
	private FileUploadHelper fileUploadHelper;

	public boolean uploadImages(Long tripId, MultipartFile[] files) {
		Optional<Trip> trip = tripRepository.findById(tripId);
		if(trip.isPresent()) {
			for(MultipartFile file: files ) {
				TripImage image=  new TripImage();
				image.setTrip(trip.get());				
				try {
					image.setUrl(fileUploadHelper.uploadFile(file));
					tripImageRepository.save(image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return true;
		}
		return false;
	}

	public void deleteById(Long id) {
		Optional<TripImage> imageToDelete = tripImageRepository.findById(id);		
		if(imageToDelete.isPresent()) {
			Path path = Paths.get(imageToDelete.get().getUrl());
			try {
				Files.deleteIfExists(path);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			tripImageRepository.delete(imageToDelete.get());
		}
		
	}

}
