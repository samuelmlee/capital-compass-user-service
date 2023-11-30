package org.capitalcompass.capitalcompassusers.model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String userSub;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Date creationDate;

    @NotNull
    private Date lastUpdateDate;

    @ElementCollection
    @CollectionTable(name = "watchlist_tickers", joinColumns = @JoinColumn(name = "watchlist_id"))
    @Column(name = "ticker")
    private List<String> tickers;
}
