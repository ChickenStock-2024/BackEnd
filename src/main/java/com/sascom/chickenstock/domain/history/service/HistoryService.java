package com.sascom.chickenstock.domain.history.service;

import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class HistoryService {

    private HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository){
        this.historyRepository = historyRepository;
    }


    @Transactional
    public void save(History request){
        History history = new History(
                request.getAccount(),
                request.getCompany(),
                request.getPrice(),
                request.getPrice(),
                request.getStatus() //이렇게 해도 되나..
        );
        historyRepository.save(history);
    }
}
