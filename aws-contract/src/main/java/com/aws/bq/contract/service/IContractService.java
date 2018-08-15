package com.aws.bq.contract.service;


import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.ContractResponseVO;

import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
public interface IContractService {
    int insert(Contract contract);
    int delete(String contractId);
    List<ContractResponseVO> findByContract(ContractRequestVO contract);
    List<ContractResponseVO> findAll();
}
