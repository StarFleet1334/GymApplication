package com.demo.folder.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.management.*;

@Component
public class SystemHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long maxMemory = heapMemoryUsage.getMax();
        long usedMemory = heapMemoryUsage.getUsed();

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();

        boolean memoryStatus = (double) usedMemory / maxMemory < 0.9; // Less than 90% of memory used
        boolean cpuStatus = systemLoad < osBean.getAvailableProcessors();

        File disk = new File(".");
        long freeSpace = disk.getUsableSpace();
        long totalSpace = disk.getTotalSpace();
        boolean diskSpaceStatus = ((double) freeSpace / totalSpace) > 0.1; // At least 10% disk space free

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadMXBean.getThreadCount();

        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        int loadedClassCount = classLoadingMXBean.getLoadedClassCount();

        if (memoryStatus && cpuStatus && diskSpaceStatus) {
            return Health.up()
                    .withDetail("memoryUsage", String.format("%d%% of %d MB", (usedMemory * 100) / maxMemory, maxMemory / (1024 * 1024)))
                    .withDetail("cpuLoad", String.format("Current system load: %.2f", systemLoad))
                    .withDetail("diskSpace", String.format("Free: %d GB out of %d GB", freeSpace / (1024 * 1024 * 1024), totalSpace / (1024 * 1024 * 1024)))
                    .withDetail("threadCount", threadCount)
                    .withDetail("loadedClasses", loadedClassCount)
                    .build();
        } else {
            return Health.down()
                    .withDetail("memoryUsage", String.format("%d%% of %d MB", (usedMemory * 100) / maxMemory, maxMemory / (1024 * 1024)))
                    .withDetail("cpuLoad", String.format("Current system load: %.2f", systemLoad))
                    .withDetail("diskSpace", String.format("Free: %d GB out of %d GB", freeSpace / (1024 * 1024 * 1024), totalSpace / (1024 * 1024 * 1024)))
                    .withDetail("threadCount", threadCount)
                    .withDetail("loadedClasses", loadedClassCount)
                    .build();
        }
    }
}