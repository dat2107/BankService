package org.example.bankservice.service;

import org.example.bankservice.dto.UserLevelDTO;
import org.example.bankservice.model.UserLevel;
import org.example.bankservice.repository.UserLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLevelService {
    @Autowired
    private UserLevelRepository userLevelRepository;

    public UserLevel insert(UserLevelDTO userLevelDTO){
        UserLevel userLevel = new UserLevel();
        userLevel.setLevelName(userLevelDTO.getLevelName());
        userLevel.setCardLimit(userLevelDTO.getCardLimit());
        userLevel.setDailyTransferLimit(userLevelDTO.getDailyTransferLimit());
        return userLevelRepository.save(userLevel);
    }

    public UserLevel update(Long id,UserLevelDTO userLevelDTO){
        return userLevelRepository.findById(id)
                .map(existing -> {
                    if(userLevelDTO.getLevelName() != null && !userLevelDTO.getLevelName().isEmpty()){
                        existing.setLevelName(userLevelDTO.getLevelName());
                    }
                    if(userLevelDTO.getCardLimit() != null ){
                        existing.setCardLimit(userLevelDTO.getCardLimit());
                    }
                    if(userLevelDTO.getDailyTransferLimit() != null){
                        existing.setDailyTransferLimit(userLevelDTO.getDailyTransferLimit());
                    }
                    return userLevelRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm người đề xuất với id: " + id));
    }

    public void delete(Long id) {
        UserLevel existing = userLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy UserLevel với id: " + id));
        userLevelRepository.delete(existing);
    }

    public List<UserLevel> getAll() {
        return userLevelRepository.findAll();
    }

    public UserLevel getById(Long id) {
        return userLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy UserLevel với id: " + id));
    }
}
