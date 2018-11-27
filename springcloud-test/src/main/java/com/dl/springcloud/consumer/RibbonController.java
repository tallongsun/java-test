package com.dl.springcloud.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dl.springcloud.provider.User;

@RestController
public class RibbonController {
	
  @Autowired
  private RibbonService ribbonService;
  
  @GetMapping("/ribbon/{id}")
  public User findById(@PathVariable Long id) {
    return this.ribbonService.findById(id);
  }
}
