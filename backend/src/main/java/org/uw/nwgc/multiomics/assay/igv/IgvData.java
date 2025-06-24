package org.uw.nwgc.multiomics.assay.igv;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
//@Entity
//@Table(name = "igv_data")
public class IgvData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "igv_data_seq")
    @SequenceGenerator(name = "igv_data_seq", sequenceName = "igv_data_seq", allocationSize = 1)
    private Integer id;

//    @ManyToOne
//    @JoinColumn(name = "sample_id", nullable = false)
//    private Sample sample;

    @JsonProperty("File")
    private String file;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("URL")
    private String url;

    @JsonProperty("sample")
    private String sample;

    @JsonProperty("project")
    private String project;

    public IgvData(String url, String file, String project) {
        this.url = url;
        this.project = project;
    }

    public static List<IgvData> getTestData() {
        List<IgvData> data = new ArrayList<>();

        return data;
    }
}
