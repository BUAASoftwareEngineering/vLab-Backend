package org.nocturne.vslab.backend;

import org.junit.jupiter.api.Test;
import org.nocturne.vslab.backend.controller.UtilController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VslabFrontserverApplicationTests {
    @Autowired
    UtilController utilController;

    @Test
    public void test() {
        utilController.sendCaptchaEmail("853172766@qq.com");
    }
}
