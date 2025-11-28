package com.collegecode.core;

import org.junit.Test;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ProjectManagerTest {

    @Test
    public void testSearchAll() throws InterruptedException {
        ProjectManager manager = new ProjectManager();
        // We can't easily mock OCRService without dependency injection or mocking
        // framework.
        // So we will test the async logic with an empty list or assume no matches if
        // OCR fails/no files.

        List<File> files = Collections.emptyList();
        CountDownLatch latch = new CountDownLatch(1);

        manager.searchAll(files, "query",
                file -> fail("Should not match anything"),
                latch::countDown);

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue("Search should complete", completed);
    }
}
