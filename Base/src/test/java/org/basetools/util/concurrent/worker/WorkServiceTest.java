package org.basetools.util.concurrent.worker;

import org.basetools.util.xml.Xml;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

class WorkServiceTest {

    @Test
    void shutdownAndExecute() throws ExecutionException, InterruptedException {
        WorkService<Xml> workService = new WorkService<Xml>();
        workService.startWork();
        for (int i = 0; i < 1000000; i++) {
            workService.pushWorkUnit(() -> System.out.println("work unit"));
        }
        System.out.println("executed:"+ workService.shutdownAndExecute(false).get());
    }
}