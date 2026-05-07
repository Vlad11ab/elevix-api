package mycode.onlineshopspring.common.pagination;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.function.Function;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable sanitize(int page, int size, Sort sort) {
        int sanitizedPage = Math.max(page, 0);
        int sanitizedSize = Math.max(size, 1);
        Sort sanitizedSort = Objects.requireNonNullElseGet(sort, () -> Sort.by("id"));
        return PageRequest.of(sanitizedPage, sanitizedSize, sanitizedSort);
    }

    public static <T> Page<T> fetchPage(Function<Pageable, Page<T>> pageFetcher, Pageable pageable) {
        Page<T> page = pageFetcher.apply(pageable);
        if (page.getTotalPages() > 0
                && page.getContent().isEmpty()
                && pageable.getPageNumber() >= page.getTotalPages()) {
            Pageable lastPageable = PageRequest.of(page.getTotalPages() - 1, pageable.getPageSize(), pageable.getSort());
            page = pageFetcher.apply(lastPageable);
        }
        return page;
    }
}
