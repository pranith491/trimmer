package com.bickynani.trimmer.controller;

import com.bickynani.trimmer.dto.BookingDtos.*;
import com.bickynani.trimmer.model.Booking;
import com.bickynani.trimmer.model.User;
import com.bickynani.trimmer.repository.BookingRepository;
import com.bickynani.trimmer.security.ApiException;
import com.bickynani.trimmer.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private static final Set<String> SERVICES=Set.of("Beard Trim","Hair + Beard","Precision Trim");
    private final BookingRepository bookings;
    private final CurrentUser currentUser;
    public BookingController(BookingRepository bookings, CurrentUser currentUser) {
        this.bookings=bookings; this.currentUser=currentUser;
    }
    @PostMapping
    public MessageResponse create(@Valid @RequestBody CreateBookingRequest req) {
        User user=currentUser.require();
        if (!SERVICES.contains(req.serviceName())) throw new ApiException(400, "Please choose a valid service.");
        if (req.bookingDate().isBefore(LocalDate.now())) throw new ApiException(400, "Please choose today or a future date.");
        if (bookings.existsByBookingDateAndBookingTimeAndStatus(req.bookingDate(), req.bookingTime(), "BOOKED"))
            throw new ApiException(409, "That time is already booked. Please select another time.");
        bookings.save(new Booking(user, req.serviceName(), req.bookingDate(), req.bookingTime(), req.notes()));
        return new MessageResponse("Your booking is confirmed!");
    }
    @GetMapping
    public List<BookingView> list() {
        User user=currentUser.require();
        return bookings.findAllByUserIdOrderByBookingDateDescBookingTimeDesc(user.getId())
            .stream().map(this::view).toList();
    }
    @PatchMapping("/{id}/cancel")
    public MessageResponse cancel(@PathVariable Long id) {
        User user=currentUser.require();
        Booking booking=bookings.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ApiException(404, "Booking not found."));
        if (!"BOOKED".equals(booking.getStatus())) throw new ApiException(400, "This booking is already cancelled.");
        booking.setStatus("CANCELLED"); bookings.save(booking);
        return new MessageResponse("Booking cancelled.");
    }
    private BookingView view(Booking b) {
        return new BookingView(b.getId(), b.getServiceName(), b.getBookingDate(), b.getBookingTime(), b.getNotes(), b.getStatus());
    }
}
