package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?2 and ?1 between b.start and b.end and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getCurrentBookingByBooker(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?2 and ?1 > b.end and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getPastBookingByBooker(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?2 and ?1 < b.start and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getFutureBookingByBooker(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?2 and ?1 < b.start and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> getWaitingBookingByBooker(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> getRejectedBookingByBooker(Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingsByBooker(Long booker);


    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?2 and ?1 between b.start and b.end and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getCurrentBookingByOwner(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?2 and ?1 > b.end and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getPastBookingByOwner(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?2 and ?1 < b.start and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getFutureBookingByOwner(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?2 and ?1 < b.start and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> getWaitingBookingByOwner(LocalDateTime time, Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> getRejectedBookingByOwner(Long userId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingsByOwner(Long booker);

    List<Booking> findByItemId(Long itemId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.bookerId = ?1 and b.item.id = ?2")
    List<Booking> findBookingItemByBooker(Long userId, Long itemId);
}

