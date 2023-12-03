package org.capitalcompass.capitalcompassusers.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
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
    private String userId;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private Date creationDate;

    @NotNull
    private Date lastUpdateDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tickers = new HashSet<>();
}
