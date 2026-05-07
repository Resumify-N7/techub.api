package com.techub.api.service;

import com.techub.api.domain.ADM;
import com.techub.api.dto.ADMCreateRequestDTO;
import com.techub.api.dto.ADMGetResponseDTO;
import com.techub.api.repository.ADMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ADMService {

    @Autowired
    private ADMRepository admRepository;

    public List<ADM> listar_adm(){
        return admRepository.findAll();
//                .stream()
//                .map(adm -> new ADMGetResponseDTO(
//                        adm.getId(),
//                        adm.getUsername()
//                ))
//                .toList();
    }

    public void desativar_adm(Long id){
        ADM adm = admRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao procurar adm"));

        adm.setAtivo(false);
    }

    public void ativar(Long id){
        ADM adm = admRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao procurar adm"));

        adm.setAtivo(true);
    }

}
