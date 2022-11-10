package com.example.OnlineCourse.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity(name = "tmp_admins")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminTmp {
    @Id
    Long chatId;
    String name;
    String userName;

    @CreationTimestamp
    Date createdAt;

    @UpdateTimestamp
    Date updateAt;

    public AdminTmp(String name, String userName, Long chatId) {
        this.chatId=chatId;
        this.name=name;
        this.userName=userName;
    }
}
