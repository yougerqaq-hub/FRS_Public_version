package com.example.FBS.service.impl;

import com.example.FBS.entity.Reimbursement;
import com.example.FBS.mapper.ReimbursementMapper;
import com.example.FBS.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReimbursementServiceImpl implements ReimbursementService {

    @Autowired
    private ReimbursementMapper reimbursementMapper;

    @Override
    public List<Reimbursement> findByUserId(Long userId) {

        // 用户ID不能为空
        if (userId == null) {
            return List.of();
        }

        return reimbursementMapper.findByUserId(userId);
    }

    @Override
    public List<Reimbursement> findAll() {
        return reimbursementMapper.findAll();
    }

    @Override
    public int addReimbursement(Reimbursement reimbursement) {

        // 报销对象不能为空
        if (reimbursement == null) {
            return 0;
        }

        // 用户ID不能为空
        if (reimbursement.getUserId() == null) {
            return 0;
        }

        // 报销金额不能为空
        if (reimbursement.getAmount() == null) {
            return 0;
        }

        // 报销金额必须大于0
        if (reimbursement.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 报销类型不能为空
        if (reimbursement.getCategory() == null
                || reimbursement.getCategory().trim().isEmpty()) {
            return 0;
        }

        // 说明为空时统一设置为空字符串
        if (reimbursement.getDescription() == null) {
            reimbursement.setDescription("");
        }

        return reimbursementMapper.addReimbursement(reimbursement);
    }
}
