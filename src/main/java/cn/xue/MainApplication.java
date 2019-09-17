package cn.xue;

import cn.xue.common.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MainApplication
{
    public static void main( String[] args )
    {
        ApplicationContext app = SpringApplication.run(MainApplication.class, args);
        SpringContextUtil.setApplicationContext(app);
    }
}
