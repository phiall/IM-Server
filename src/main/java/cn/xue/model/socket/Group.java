package cn.xue.model.socket;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;//created by
    @Column(name = "group_name", nullable = false)
    private String groupName;
    @Column(columnDefinition = "BIT(1) DEFAULT true", nullable = false)
    private Boolean valid;//标识对话是否已被删除
}
