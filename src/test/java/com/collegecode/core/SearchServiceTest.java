package com.collegecode.core;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchServiceTest {

    @Test
    public void testSearchFound() {
        SearchService service = new SearchService();
        String text = "Hello world, this is a test.";
        List<Integer> indices = service.search(text, "world");
        assertEquals(1, indices.size());
        assertEquals((Integer) 6, indices.get(0));
    }

    @Test
    public void testSearchNotFound() {
        SearchService service = new SearchService();
        String text = "Hello world";
        List<Integer> indices = service.search(text, "java");
        assertTrue(indices.isEmpty());
    }

    @Test
    public void testCaseInsensitive() {
        SearchService service = new SearchService();
        String text = "Hello World";
        List<Integer> indices = service.search(text, "world");
        assertEquals(1, indices.size());
    }
}
