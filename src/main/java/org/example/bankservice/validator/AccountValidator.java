package org.example.bankservice.validator;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.model.UserLevel;
import org.example.bankservice.repository.UserLevelRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountValidator implements Validator<AccountDTO>{
    private final UserLevelRepository userLevelRepository;

    @Override
    public void validate(AccountDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự");
        }
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
    }

    public UserLevel validateAndGetLevel(Long levelId) {
        if (levelId == null) {
            throw new IllegalArgumentException("UserLevelId không được để trống");
        }
        return userLevelRepository.findById(levelId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Level với id: " + levelId));
    }
}
