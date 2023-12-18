package org.capitalcompass.capitalcompassusers.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    private Date creationDate;

    @NotNull
    private Date lastUpdateDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            joinColumns = @JoinColumn(name = "watchlist_id"),
            inverseJoinColumns = @JoinColumn(name = "ticker_id")
    )
    private Set<Ticker> tickers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Watchlist watchlist = (Watchlist) o;
        return Objects.equals(userId, watchlist.userId) && Objects.equals(name, watchlist.name) && Objects.equals(creationDate, watchlist.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, creationDate);
    }
}
