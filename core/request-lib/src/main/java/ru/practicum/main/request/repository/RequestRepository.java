package ru.practicum.main.request.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.status.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}