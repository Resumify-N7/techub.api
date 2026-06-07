package com.techub.api.dto;

public record DashboardStatsDTO(
    long usuariosAtivos,
    long usuariosDesativados,

    long resumosAtivos,
    long resumosDesativados,

    long cursosAtivos,
    long cursosDesativados,

    long tagsAtivas,
    long tagsDesativadas,

    long materiasAtivas,
    long materiasDesativadas,

    long selosAtivos,
    long selosDesativados,

    long avataresAtivos,
    long avataresDesativados,

    long biosAtivas,
    long biosDesativadas,

    long universidadesAtivas,
    long universidadesDesativadas,

    long alunosAtivos,
    long alunosDesativados,
    long admsAtivos
) {}
