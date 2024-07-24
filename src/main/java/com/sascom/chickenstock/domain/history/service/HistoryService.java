package com.sascom.chickenstock.domain.history.service;

import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    private HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository){
        this.historyRepository = historyRepository;
    }

    // 거래가 체결될때마다 실행되는 메서드 // 체결됐을 때 생기는 이벤트가 아래와 같은 내용을 전달해줘야함
    public void save(History request){
        History history = new History(
                request.getAccount(),
                request.getCompany(),
                request.getPrice(),
                request.getPrice(),
                request.getStatus() //이렇게 해도 되나..
        );
    }
}
