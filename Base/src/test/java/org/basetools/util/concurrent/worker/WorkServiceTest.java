package org.basetools.util.concurrent.worker;

import org.basetools.util.xml.Xml;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class WorkServiceTest {

    @Test
    void shutdownAndExecute() throws ExecutionException, InterruptedException {
        WorkService<Xml> workService = new WorkService<Xml>();
        workService.startWork();

        workService.shutdownAndExecute(false).get();
    }
}