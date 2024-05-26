package ma.enset.backend.repositories;

import ma.enset.backend.entities.DocumentEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentEntryRepository extends JpaRepository<DocumentEntry, Long> {

    // Custom query methods
    List<DocumentEntry> findByName(String name);

    List<DocumentEntry> findByTimestampAfter(LocalDateTime timestamp);

    Optional<DocumentEntry> findByNameAndTimestamp(String name, LocalDateTime timestamp);

    // Example of JPQL query method
    @Query("SELECT d FROM DocumentEntry d WHERE d.timestamp BETWEEN :start AND :end")
    List<DocumentEntry> findByTimestampRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    long countByName(String name);

    void deleteByName(String name);

    // Example of custom native SQL query method
    @Query(value = "SELECT * FROM document_entry WHERE name = ?1", nativeQuery = true)
    List<DocumentEntry> findByCustomNameQuery(String name);

}
