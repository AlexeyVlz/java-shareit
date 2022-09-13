package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemId(Long itemId);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingsByOwner(Long booker/*, PageRequest pageRequest*/);

    List<Booking> findBookingsByItemOwnerId(Long booker, PageRequest pageRequest);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingsByBooker(Long booker/*, PageRequest pageRequest*/);

    List<Booking> findBookingsByBookerId(Long booker, PageRequest pageRequest);

    @Modifying
    @Query("select b from Booking as b " +
            "where b.item.id = ?1 and b.booker.id = ?2 " +
            "order by b.start desc")
    List<Booking> findBookingsByBookerIdAndItemId(Long itemId, Long userId);
}
