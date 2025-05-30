package org.uw.nwgc.multiomics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.uw.nwgc.multiomics.core.sample.Sample;

@Entity
@Data
public class DataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private Class<?> clazz;
    private String uri;
    private String visualizationComponentName;

    @ManyToOne
    @JoinColumn(name = "sample_id")
    private Sample sample;
}
