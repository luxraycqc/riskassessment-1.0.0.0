package cn.net.aichain.edge.ms.jpa.firm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessRequirementDao extends JpaRepository<AccessRequirement,String> {
    List<AccessRequirement> findAll();

    AccessRequirement findByApplyId(String applyId);
}
