package com.api.mithai.base.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedBaseResponse<T>{
    List<T> list;
    Long totalRecords;
    Long currentPage;
}
