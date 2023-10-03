package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.ThreadLocalService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ThreadLocalServiceTest {

    private final ThreadLocalService threadLocalService = new ThreadLocalService();

    @Test
    void field() {
        log.info("main start");
        Runnable userA = () -> {
            threadLocalService.logic("userA");
        };
        Runnable userB = () -> {
            threadLocalService.logic("userB");
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start(); // 로직 동작 시 1초 걸림
//        sleep(2000); // 동시성 문제 발생 X
        sleep(100); // 기존 동시성 문제 발생 코드 -> 동시성 문제 발생 안함
        threadB.start(); // 로직 동작 시 1초 걸림
        sleep(2000); // 메인 쓰레드 종료 대기
        log.info("main exit");
        /** 동시성 문제 발생 X
         * 00:22:04.116 [Test worker] INFO hello.advanced.trace.threadlocal.ThreadLocalServiceTest - main start
         * 00:22:04.120 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userA -> nameStore=null
         * 00:22:05.131 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userA
         * 00:22:06.127 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userB -> nameStore=null
         * 00:22:07.136 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userB
         * 00:22:08.130 [Test worker] INFO hello.advanced.trace.threadlocal.ThreadLocalServiceTest - main exit
         */

        /** 기존 동시성 문제 발생하는 코드에서도 동시성 문제 발생 X
         * 00:22:37.108 [Test worker] INFO hello.advanced.trace.threadlocal.ThreadLocalServiceTest - main start
         * 00:22:37.112 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userA -> nameStore=null
         * 00:22:37.221 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userB -> nameStore=null
         * 00:22:38.127 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userA
         * 00:22:38.235 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userB
         * 00:22:39.230 [Test worker] INFO hello.advanced.trace.threadlocal.ThreadLocalServiceTest - main exit
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
