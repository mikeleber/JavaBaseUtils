package org.basetools.util.concurrent.worker;

import org.basetools.util.xml.Xml;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

class WorkServiceTest {

    @Test
    void shutdownAndExecute() throws ExecutionException, InterruptedException {
        WorkService<Xml> workService = new WorkService<Xml>();
        workService.startWork();
        for (int i = 0; i < 100; i++) {
            workService.pushWorkUnit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                System.out.println("work unit");
            });
        }
        System.out.println("executed:" + workService.shutdown(false));
    }
    @Test
    void shutdownAndExecuteList() throws ExecutionException, InterruptedException {
        WorkService<Xml> workService = new WorkService<Xml>();
        workService.startWork();
        List<Runnable> runnables = new ArrayList<Runnable>();
        final AtomicInteger counter= new AtomicInteger();
        for (int i = 0; i < 100001; i++) {
            runnables.add(() -> {
//                try {
//                    Thread.sleep(1);
//                } catch (Exception e) {
//                }
                System.out.println("work unit:"+counter.incrementAndGet()+" "+this.hashCode());
            });
        }
        workService.pushWorkUnit(runnables,null,200);
      //  System.out.println("executed:" + workService.shutdown(false));
    }
}