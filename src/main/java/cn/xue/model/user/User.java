package cn.xue.model.user;
import cn.xue.model.diy.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone",nullable = false,unique = true, length = 32)
    private String phone;//手机号
    @Column(nullable = false, length = 33)
    private String password;//登录密码
    @Column(name = "nick_name",nullable = false, length = 32)
    private String nickName;
    @Column(name = "gender", columnDefinition = "ENUM('男','女') DEFAULT '男'", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "head_url")
    private String headUrl;
    @Column(name = "push_client_id", length = 65)
    private String pushClientId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "updated_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}
