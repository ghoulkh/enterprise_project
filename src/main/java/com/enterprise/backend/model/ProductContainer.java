package com.enterprise.backend.model;

import com.enterprise.backend.model.entity.Product;
import lombok.Data;

@Data
public class ProductContainer {
  private Product before;
  private Product after;

  public ProductContainer(Product before, Product after) {
    this.before = before;
    this.after = after;
  }

}
