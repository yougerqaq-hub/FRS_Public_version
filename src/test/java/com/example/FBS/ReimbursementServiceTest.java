package com.example.FBS;

import com.example.FBS.entity.Reimbursement;
import com.example.FBS.service.ReimbursementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class ReimbursementServiceTest {

    @Autowired
    private ReimbursementService reimbursementService;

    @Test
    public void testFindByUserId() {

        // 测试报销记录
        List<Reimbursement> list = reimbursementService.findByUserId(4L);

        System.out.println("===== 张三的报销记录 =====");

        for (Reimbursement reimbursement : list) {
            System.out.println(
                    "报销ID：" + reimbursement.getId()
                            + "，金额：" + reimbursement.getAmount()
                            + "，类型：" + reimbursement.getCategory()
                            + "，状态：" + reimbursement.getStatus()
            );
        }
    }

    @Test
    public void testAddReimbursement() {

        Reimbursement reimbursement = new Reimbursement();

        reimbursement.setUserId(4L);
        reimbursement.setAmount(new BigDecimal("880.00"));
        reimbursement.setCategory("差旅费");
        reimbursement.setDescription("上海出差交通费用");

        int result = reimbursementService.addReimbursement(reimbursement);

        if (result > 0) {
            System.out.println("===== 报销申请提交成功 =====");
        } else {
            System.out.println("===== 报销申请提交失败 =====");
        }
    }
}