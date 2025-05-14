package br.insper.prova;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProvaApplicationTests {

    @Test
    void contextLoads() {
        // garante que o contexto sobe sem erros
    }

    @Test
    void mainRuns() {
        ProvaApplication.main(new String[]{});
    }
}

