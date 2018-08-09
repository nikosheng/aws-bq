package com.aws.bq.contract.dao;


import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.vo.ContractRequestVO;

import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
public interface IContractDAO {
    int insert(Contract contract);
    int delete(String contractId);
    List<Contract> findByContract(ContractRequestVO contract);
    List<Contract> findAll();
}
