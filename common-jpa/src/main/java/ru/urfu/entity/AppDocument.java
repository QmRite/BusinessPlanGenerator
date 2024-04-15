package ru.urfu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_document")
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String docName;
    @OneToOne(cascade = CascadeType.ALL)
    private BinaryContent binaryContent;
    //private byte[] fileContent;

    private String mimeType;

    private Date createdAt;

//    @ManyToOne
//    private AppUser user;
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
