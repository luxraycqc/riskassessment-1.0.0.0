package cn.net.aichain.edge.ms.jpa.firm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface FirmCreditInfoDao extends JpaRepository<FirmCreditInfo, String> {

    List<FirmCreditInfo> findAll();

    FirmCreditInfo findByApplyId(String applyId);
}
