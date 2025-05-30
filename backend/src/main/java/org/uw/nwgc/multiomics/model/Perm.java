package org.uw.nwgc.multiomics.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

public class Perm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perm_seq")
    @SequenceGenerator(name = "perm_seq", sequenceName = "perm_seq", allocationSize = 1)
    private Integer id;
    private String auth;

}