package io.kontakt.apps.anomaly.storage.mapper;

public interface Mapper <Model, DTO>{
    DTO toDTO(final Model model);
    Model toModel(final DTO dto);
}
