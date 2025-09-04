package org.example.bankservice.controller;

import org.example.bankservice.dto.UserLevelDTO;
import org.example.bankservice.model.UserLevel;
import org.example.bankservice.service.UserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userlevel")
public class UserLevelController {
    @Autowired
    private UserLevelService userLevelService;

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody UserLevelDTO userLevelDTO){
        UserLevel userLevel = userLevelService.insert(userLevelDTO);
        return ResponseEntity.ok("Thêm thành công: "+ userLevel);
    }

    @GetMapping
    public List<UserLevel> getAllLevels() {
        return userLevelService.getAll();
    }

    @GetMapping("/{id}")
    public UserLevel getLevel(@PathVariable Long id) {
        return userLevelService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserLevelDTO userLevelDTO){
        UserLevel userLevel = userLevelService.update(id,userLevelDTO);
        return ResponseEntity.ok("Sửa thành công: "+ userLevel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        userLevelService.delete(id);
        return ResponseEntity.ok("Xóa thành công: ");
    }
}
