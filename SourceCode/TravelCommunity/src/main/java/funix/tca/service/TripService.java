package funix.tca.service;

import funix.tca.entity.Trip;
import funix.tca.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    // Lưu một chuyến đi
    public Trip save(Trip trip) {
        return tripRepository.save(trip);
    }

    // Lấy một chuyến đi theo ID
    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    // Lấy tất cả các chuyến đi
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    // Lấy các chuyến đi do một người tổ chức
    public List<Trip> findByCreatorId(Long creatorId) {
        return tripRepository.findByCreatorId(creatorId);
    }

    // Lấy các chuyến đi mà người tham gia tham gia
    public List<Trip> findByParticipantsId(Long participantId) {
        return tripRepository.findByParticipantsId(participantId);
    }

    // Cập nhật chuyến đi
    public Trip update(Trip trip) {
        return tripRepository.save(trip);
    }

    // Xóa chuyến đi theo ID
    public void deleteById(Long id) {
        tripRepository.deleteById(id);
    }
}
