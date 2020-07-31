package org.rimand.doc.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String docName;
    @NotBlank
    private String docId;
    @Size(max = 1024)
    private String description;
    private boolean publicDocument = false;
    @ManyToMany(mappedBy = "taggedDocs", fetch = FetchType.EAGER)
    private Set<Tag> tags = new HashSet<>();
    @NotNull
    private LocalDateTime uploadDateTime;
    private LocalDateTime lastEditDateTime;
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User uploader;
    @NotBlank
    private String filename;
    @NotBlank
    private String mediaType;
    private long size;
}