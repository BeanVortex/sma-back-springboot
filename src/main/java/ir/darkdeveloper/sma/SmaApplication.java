package ir.darkdeveloper.sma;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SmaApplication {

    public static void main(String[] args) {
        disableWarning();
        SpringApplication.run(SmaApplication.class, args);
    }

    public static void disableWarning() {
        System.err.close();
        System.setErr(System.out);
    }

}


