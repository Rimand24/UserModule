package org.example.auth.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "document")
public class Document implements Serializable {
    private static final long serialVersionUID = -905008395112162104L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String docId;
    @Column(nullable = false)
    private String filename;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User createdBy;

}