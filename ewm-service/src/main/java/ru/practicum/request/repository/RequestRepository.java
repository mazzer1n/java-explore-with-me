package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.*;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    boolean existsByEventIdAndRequesterId(long eventId, long requesterId);

    ParticipationRequest findByIdAndRequesterId(long id, long requesterId);

    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByRequesterId(long requesterId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventIdInAndStatus(List<Long> eventsIds, RequestStatus status);

    List<ParticipationRequest> findAllByIdInAndEventIdAndStatus(
        List<Long> requestIds,
        Long eventId,
        RequestStatus status
    );
}