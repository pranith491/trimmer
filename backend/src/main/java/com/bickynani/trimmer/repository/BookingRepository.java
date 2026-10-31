package com.bickynani.trimmer.repository;
import com.bickynani.trimmer.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserIdOrderByBookingDateDescBookingTimeDesc(Long userId);
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
    boolean existsByBookingDateAndBookingTimeAndStatus(LocalDate bookingDate, LocalTime bookingTime, String status);
}
