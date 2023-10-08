package hello.advanced.trace.strategy.code.template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeLogTemplate {

    public void execute(Callback callback) {
        long startTime = System.currentTimeMillis();

        callback.call();

        long entTime = System.currentTimeMillis();
        long resultTime = entTime - startTime;
        log.info("resultTime={}", resultTime);
    }

}
