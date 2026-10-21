package ru.practicum.main.managedlocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.managedlocation.model.ManagedLocation;

@Repository
public interface ManagedLocationRepository extends JpaRepository<ManagedLocation, Long> {
}
