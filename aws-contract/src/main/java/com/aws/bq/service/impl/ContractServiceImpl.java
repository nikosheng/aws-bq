package com.aws.bq.service.impl;


import com.aws.bq.dao.IContractDAO;
import com.aws.bq.model.Contract;
import com.aws.bq.service.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
@Service
public class ContractServiceImpl implements IContractService {
    @Autowired
    private IContractDAO contractDAO;

    @Override
    public int insert(Contract contract) {
        return contractDAO.insert(contract);
    }

    @Override
    public int delete(String contractId) {
        return contractDAO.delete(contractId);
    }

    @Override
    public List<Contract> findByContract(Contract contract) {
        List<Contract> contracts = contractDAO.findByContract(contract);
        return contracts.size() > 0 ? contracts : new ArrayList<>();
    }

    @Override
    public List<Contract> findAll() {
        List<Contract> contracts = contractDAO.findAll();
        return contracts.size() > 0 ? contracts : new ArrayList<>();
    }
}
