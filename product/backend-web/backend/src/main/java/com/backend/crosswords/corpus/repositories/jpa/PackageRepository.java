package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.models.DocMeta;
import com.backend.crosswords.corpus.models.Package;
import com.backend.crosswords.corpus.models.PackageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, PackageId> {
    List<Package> getAllByOwner(User user);
    @Query("SELECT p FROM Package p LEFT JOIN FETCH p.docs WHERE p.id = :id")
    Optional<Package> findByIdWithDocs(@Param("id") PackageId id);
}
