package mycode.onlineshopspring.common.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaginationUtilsTest {

    @Test
    void sanitizeClampsInvalidValuesAndPreservesSort() {
        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PaginationUtils.sanitize(-10, 0, sort);

        assertEquals(0, pageable.getPageNumber());
        assertEquals(1, pageable.getPageSize());
        assertEquals(sort, pageable.getSort());
    }

    @Test
    void sanitizeFallsBackToIdSortWhenSortIsNull() {
        Pageable pageable = PaginationUtils.sanitize(2, 5, null);

        assertEquals(Sort.by("id"), pageable.getSort());
        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
    }

    @Test
    void fetchPageFallsBackToLastPageWhenRequestedPageIsEmpty() {
        AtomicInteger invocation = new AtomicInteger();
        Function<Pageable, Page<Integer>> pageFetcher = pageable -> {
            int call = invocation.getAndIncrement();
            if (call == 0) {
                // First call simulates an empty page beyond range
                return new PageImpl<>(List.of(), pageable, 6);
            }
            assertEquals(2, pageable.getPageNumber());
            assertEquals(2, pageable.getPageSize());
            return new PageImpl<>(List.of(42), pageable, 6);
        };

        Pageable initial = PageRequest.of(5, 2, Sort.by("id"));

        Page<Integer> result = PaginationUtils.fetchPage(pageFetcher, initial);

        assertEquals(2, result.getNumber());
        assertEquals(1, result.getContent().size());
        assertEquals(42, result.getContent().get(0));
        assertNotNull(result.getSort());
        assertEquals(Sort.by("id"), result.getSort());
        assertEquals(2, invocation.get());
    }
}
