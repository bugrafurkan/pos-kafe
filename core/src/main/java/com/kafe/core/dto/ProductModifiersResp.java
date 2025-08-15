package com.kafe.core.dto;

import java.util.List;

public class ProductModifiersResp {
  public Long productId;
  public List<ModifierGroupWithOptions> groups;
  
  public static class ModifierGroupWithOptions {
    public Long groupId; 
    public String groupName; 
    public boolean required; 
    public int maxSelect;
    public List<ModifierOptionResp> options;
  }
}
