package com.aws.bq.dao;


import com.aws.bq.model.Contract;

import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
public interface IContractDAO {
    int insert(Contract contract);
    int delete(String contractId);
    List<Contract> findByContract(Contract contract);
    List<Contract> findAll();
}
