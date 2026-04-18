package ru.vladmz.books.DTOs;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.vladmz.books.etc.pageSorting.DefaultSort;
import ru.vladmz.books.etc.pageSorting.EntitySort;

public record PageParams(
        int pageNumber,
        int pageSize,
        EntitySort sortBy,
        Sort.Direction sortDirection
) {

    private static final PageParams firstPage = new PageParams(0, 50, DefaultSort.TIME, Sort.Direction.DESC);

    public PageParams{
        if (pageNumber < 0) pageNumber = 0;
        if (pageSize <= 0 || pageSize > 50) pageSize = 20;
    }

    public Pageable toPageable(){
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortBy.getFieldName()));
    }

    public static PageParams of(int pageNumber, int pageSize, EntitySort sortBy, Sort.Direction direction){
        return new PageParams(pageNumber, pageSize, sortBy, direction);
    }

    public static PageParams firstPage(){
        return firstPage;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private int pageNumber = 0;
        private int pageSize = 20;
        private EntitySort sortBy = DefaultSort.TIME;
        private Sort.Direction sortDirection = Sort.Direction.DESC;

        private Builder(){}

        public PageParams build(){
            return new PageParams(pageNumber, pageSize, sortBy, sortDirection);
        }

        public Builder number(int pageNumber){
            this.pageNumber = pageNumber;
            return this;
        }

        public Builder size(int pageSize){
            this.pageSize = pageSize;
            return this;
        }

        public Builder sortBy(EntitySort sortBy){
            this.sortBy = sortBy;
            return this;
        }

        public Builder sortDirection(Sort.Direction direction){
            this.sortDirection = direction;
            return this;
        }
    }
}