package teamzero.javaweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"teamzero.javaweb"})
public class JavaWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(JavaWebApplication.class, args);
    }
}
