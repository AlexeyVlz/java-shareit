package ru.practicum.shareit.requests;

import org.apache.coyote.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    @Modifying
    @Query("select ir from ItemRequest as ir " +
            "where ir.userId = ?1 " +
            "order by ir.creationTime desc ")
    List<ItemRequest> findAllByUserId(Long userId);
}
