package com.gustavolopes.money_pilot.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroRespostaDTO(
        LocalDateTime timestamp,
        int status,
        String mensagem,
        List<String> erros
) {}