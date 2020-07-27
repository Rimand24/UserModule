package org.example.auth.domain;

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
//    private boolean publicDocument;

    @ManyToMany(mappedBy = "taggedDocs", fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "document_tag",
//            joinColumns = @JoinColumn(name ="document_id" ),
//            inverseJoinColumns = @JoinColumn(name ="tag_id" ))
    private Set<Tag> tags = new HashSet<>();

    @NotNull
    private LocalDateTime uploadDateTime;
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User uploader;
//    @ManyToMany //fixme
//    private Set<User> allowedUsers;

    @NotBlank
    private String filename;
    @NotBlank
    private String mediaType;

    private long size;


}