package com.kafe.core.dto;

import java.util.List;

public class PagedResp<T> {
  public List<T> content;
  public int page;
  public int size;
  public long totalElements;
  public int totalPages;
}
