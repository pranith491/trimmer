package com.bickynani.trimmer.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public final class BookingDtos {
    private BookingDtos() {}
    public record CreateBookingRequest(@NotBlank @Size(max=80) String serviceName,
        @NotNull LocalDate bookingDate, @NotNull LocalTime bookingTime,
        @Size(max=300) String notes) {}
    public record BookingView(Long id, String serviceName, LocalDate bookingDate,
        LocalTime bookingTime, String notes, String status) {}
    public record MessageResponse(String message) {}
}
