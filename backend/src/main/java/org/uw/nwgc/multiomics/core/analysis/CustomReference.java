package org.uw.nwgc.multiomics.core.analysis;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.resources.OmicResource;

import java.util.UUID;

@Getter
@Setter
@Entity
public class CustomReference {

	@Id
	@UuidGenerator
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID id;

	private String shortName;
	private String longName;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private OmicResource fastaURL;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private OmicResource indexURL;

//	private String cytobandURL; // optional
//	private List<TracksItem> tracks = new ArrayList<>();
//
//	@Data
//	public static class TracksItem {
//		private boolean indexed;
//		private String name;
//		private String url;
//		private int order;
//	}
}
