package org.example.auth.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "document")
public class Document implements Serializable {
    private static final long serialVersionUID = -905008395112162104L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String docId;
    @NotBlank
    private String filename;
    @NotBlank
    private String mediaType;
    private long size;
    @NotNull
    private LocalDateTime uploadDateTime;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User uploader;

}