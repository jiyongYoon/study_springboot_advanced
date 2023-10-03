package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FieldServiceTest {

    private final FieldService fieldService = new FieldService();

    @Test
    void field() {
        log.info("main start");
        Runnable userA = () -> {
            fieldService.logic("userA");
        };
        Runnable userB = () -> {
            fieldService.logic("userB");
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start(); // 로직 동작 시 1초 걸림
//        sleep(2000); // 동시성 문제 발생 X
        sleep(100); // 동시성 문제 발생 O
        threadB.start(); // 로직 동작 시 1초 걸림
        sleep(2000); // 메인 쓰레드 종료 대기
        log.info("main exit");
        /** 동시성 문제 발생 X
         * 00:00:18.469 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main start
         * 00:00:18.475 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
         * 00:00:19.493 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userA
         * 00:00:20.479 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
         * 00:00:21.488 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
         * 00:00:22.489 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main exit
         */

        /** 동시성 문제 발생 O
         * 00:02:50.327 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main start
         * 00:02:50.337 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
         * 00:02:50.438 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
         * 00:02:51.358 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
         * 00:02:51.449 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
         * 00:02:52.443 [Test worker] INFO hello.advanced.trace.threadlocal.FieldServiceTest - main exit
         */
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
