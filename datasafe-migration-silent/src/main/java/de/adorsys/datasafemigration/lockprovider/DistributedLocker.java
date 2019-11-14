package de.adorsys.datasafemigration.lockprovider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DistributedLocker {
    private final LockProvider lockProvider;
    private Map<String, SimpleLock> lockmap = new HashMap<>();

    public DistributedLocker(final LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    /**
     * @param nameToLock    the name to be blocked. different names can run in parallel
     * @param milliSecondsToLock if the block has been received, it will durate till unlock is called or the number of milli seconds expired
     * @return true, if lock is available
     */
    @SneakyThrows
    public boolean lockOrFail(String nameToLock, int milliSecondsToLock) {
        LockConfiguration lc = new LockConfiguration(nameToLock,
                Instant.now().plus(Duration.ofMillis(milliSecondsToLock)));

        Optional<SimpleLock> lock = lockProvider.lock(lc);
        if (lock.isPresent()) {
            lockmap.put(nameToLock, lock.get());
            log.debug("successfully locked {}", nameToLock);
            return true;
        }
        log.debug("failed to lock {}", nameToLock);
        return false;
    }

    /**
     *
     * @param nameToLock the name that has been locked before
     */
    public void unlock(String nameToLock) {
        SimpleLock simpleLock = lockmap.get(nameToLock);
        lockmap.remove(nameToLock);
        simpleLock.unlock();
        log.debug("successfully unlocked {}", nameToLock);
    }
}
