package com.techub.api.service;

import com.techub.api.domain.ADM;
import com.techub.api.repository.ADMRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ADMService {

    @Autowired
    private ADMRepository admRepository;

    public List<ADM> listar_adm(int limit){
        int pageSize = Math.max(1, limit);
        return admRepository.findActive(PageRequest.of(0, pageSize)).getContent();
//                .stream()
//                .map(adm -> new ADMGetResponseDTO(
//                        adm.getId(),
//                        adm.getUsername()
//                ))
//                .toList();
    }

    public void ativar(Long id){
        ADM adm = admRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao procurar adm"));
        adm.setAtivo(true);
        admRepository.save(adm);
    }
}
