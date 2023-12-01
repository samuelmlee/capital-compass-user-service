package org.capitalcompass.capitalcompassusers.repository;

import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchListRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserId(String userId);


}
