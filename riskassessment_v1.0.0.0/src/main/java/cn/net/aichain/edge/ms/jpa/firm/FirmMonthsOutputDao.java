package cn.net.aichain.edge.ms.jpa.firm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FirmMonthsOutputDao extends JpaRepository<FirmMonthsOutput,Long> {
    List<FirmMonthsOutput> findAll();
    List<FirmMonthsOutput> findByApplyId(String applyId);
}
