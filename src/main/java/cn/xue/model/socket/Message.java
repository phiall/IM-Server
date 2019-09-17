package cn.xue.model.socket;

import cn.xue.common.Constants;
import cn.xue.model.diy.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "ENUM('text','picture','video','audio','system')", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private MessageType type;//消息类型
    @Column(name = "src_user_id")
    private Long srcUserId;//发送人ID
    @Column(name = "des_user_id")
    private Long desUserId;//接收人ID
    @Column(length = 512)
    private String content;//消息内容
    @Column(name = "already_sent")
    private boolean alreadySent;//是否已发送
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    public Message() {}
    public Message(MessageType type, Long srcUserId, Long desUserId, String content) {
        this.type = type;
        this.srcUserId = srcUserId;
        this.desUserId = desUserId;
        this.content = content;
    }
    public String buildTopic() {
        return Constants.WEBSOCKET_MESSAGE + srcUserId + ":" + desUserId;
    }
}
