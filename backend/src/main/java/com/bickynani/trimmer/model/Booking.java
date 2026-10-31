package com.bickynani.trimmer.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 80) private String serviceName;
    @Column(nullable = false) private LocalDate bookingDate;
    @Column(nullable = false) private LocalTime bookingTime;
    @Column(length = 300) private String notes;
    @Column(nullable = false, length = 20) private String status = "BOOKED";
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public Booking() {}
    public Booking(User user, String serviceName, LocalDate bookingDate, LocalTime bookingTime, String notes) {
        this.user=user; this.serviceName=serviceName; this.bookingDate=bookingDate;
        this.bookingTime=bookingTime; this.notes=notes; this.status="BOOKED";
    }
    public Long getId(){return id;}
    public User getUser(){return user;}
    public String getServiceName(){return serviceName;}
    public LocalDate getBookingDate(){return bookingDate;}
    public LocalTime getBookingTime(){return bookingTime;}
    public String getNotes(){return notes;}
    public String getStatus(){return status;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setStatus(String status){this.status=status;}
}
