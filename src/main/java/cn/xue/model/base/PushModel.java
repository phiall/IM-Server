package cn.xue.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PushModel {
    private String title;
    private String content;
    private String payload;
}
