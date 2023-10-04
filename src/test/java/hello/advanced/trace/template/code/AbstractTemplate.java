package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTemplate {

    public void execute() {
        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        call(); // 변하는 부분은 자식클래스에서 구현하여 사용
        // 비즈니스 로직 종료
        long entTime = System.currentTimeMillis();
        long resultTime = entTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    protected abstract void call();

}
