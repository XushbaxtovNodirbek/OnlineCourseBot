package com.example.OnlineCourse.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Admins implements Serializable {
    @Id
    Long chatId;
    String name;
    String step;
    String isActive;

    @CreationTimestamp
    Date createdAt;

    @UpdateTimestamp
    Date updateAt;
}
