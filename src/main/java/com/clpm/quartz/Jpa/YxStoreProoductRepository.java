package com.clpm.quartz.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface YxStoreProoductRepository extends JpaRepository<YxStoreProduct, Integer>, JpaSpecificationExecutor<YxStoreProduct> {


    YxStoreProduct findYxStoreById(Integer id);


}
