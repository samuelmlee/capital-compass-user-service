package org.capitalcompass.userservice.repository;

import org.capitalcompass.userservice.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchListRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserId(String userId);

    Boolean existsByNameAndUserId(String name, String userId);


}
