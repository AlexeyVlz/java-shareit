package ru.practicum.shareit.responses;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "responses")
public class Response {

   /* @Id
    @Column(name = "response_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name  = "item_id", nullable = false)
    private Long id;
    @Column(name  = "request_id", nullable = false)
    private Long requestId;

    @Column(name  = "item_name", nullable = false)
    private String itemName;
    @Column(name  = "owner_id", nullable = false)
    private Long ownerId;

    public Response(Long id, Long requestId, String itemName, Long ownerId) {
        this.id = id;
        this.requestId = requestId;
        this.itemName = itemName;
        this.ownerId = ownerId;
    }

    public Response() {
    }
}*/
