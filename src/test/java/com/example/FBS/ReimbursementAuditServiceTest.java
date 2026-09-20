package com.example.FBS;

import com.example.FBS.entity.ReimbursementAudit;
import com.example.FBS.service.ReimbursementAuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ReimbursementAuditServiceTest {

    @Autowired
    private ReimbursementAuditService reimbursementAuditService;

    @Test
    public void testFindByReimbursementId() {

        // 查询报销ID为2的审核记录
        List<ReimbursementAudit> list =
                reimbursementAuditService.findByReimbursementId(2L);

        System.out.println("===== 报销ID为2的审核记录 =====");

        for (ReimbursementAudit audit : list) {
            System.out.println(
                    "审核ID：" + audit.getId()
                            + "，审核人ID：" + audit.getAuditorId()
                            + "，结果：" + audit.getResult()
                            + "，意见：" + audit.getComment()
            );
        }
    }

    @Test
    public void testAddAudit() {

        ReimbursementAudit audit = new ReimbursementAudit();

        // 给报销ID为1的待审核报销添加一条审核记录
        audit.setReimbursementId(1L);
        audit.setAuditorId(2L);
        audit.setResult("APPROVED");
        audit.setComment("报销材料完整，符合报销规定");

        int result = reimbursementAuditService.addAudit(audit);

        if (result > 0) {
            System.out.println("===== 审核记录添加成功 =====");
        } else {
            System.out.println("===== 审核记录添加失败 =====");
        }
    }
}