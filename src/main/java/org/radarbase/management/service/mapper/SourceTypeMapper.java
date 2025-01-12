package org.radarbase.management.service.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.radarbase.management.domain.SourceData;
import org.radarbase.management.domain.SourceType;
import org.radarbase.management.service.dto.MinimalSourceTypeDTO;
import org.radarbase.management.service.dto.SourceDataDTO;
import org.radarbase.management.service.dto.SourceTypeDTO;

/**
 * Mapper for the entity SourceType and its DTO SourceTypeDTO.
 */
@Mapper(componentModel = "spring", uses = {SourceDataMapper.class,})
public interface SourceTypeMapper {
    @Named("sourceTypeToSourceTypeDTO")
    SourceTypeDTO sourceTypeToSourceTypeDTO(SourceType sourceType);

    @Named("sourceTypeToSourceTypeDTOReduced")
    @Mapping(target = "sourceData", ignore = true)
    @Mapping(target = "assessmentType", ignore = true)
    @Mapping(target = "appProvider", ignore = true)
    @Mapping(target = "description", ignore = true)
    SourceTypeDTO sourceTypeToSourceTypeDTOReduced(SourceType sourceType);

    @IterableMapping(qualifiedByName = "sourceTypeToSourceTypeDTOReduced")
    List<SourceTypeDTO> sourceTypesToSourceTypeDTOs(List<SourceType> sourceTypes);

    @Mapping(target = "projects", ignore = true)
    SourceType sourceTypeDTOToSourceType(SourceTypeDTO sourceTypeDto);

    List<SourceType> sourceTypeDTOsToSourceTypes(List<SourceTypeDTO> sourceTypeDtos);

    MinimalSourceTypeDTO sourceTypeToMinimalSourceTypeDetailsDTO(SourceType sourceType);

    List<MinimalSourceTypeDTO> sourceTypesToMinimalSourceTypeDetailsDTOs(
            List<SourceType> sourceTypes);

    @IterableMapping(qualifiedByName = "sourceDataReducedDTO")
    Set<SourceDataDTO> map(Set<SourceData> sourceData);

    @Mapping(target = "sourceTypeScope", ignore = true)
    @Mapping(target = "sourceData", ignore = true)
    @Mapping(target = "canRegisterDynamically", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "assessmentType", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "appProvider", ignore = true)
    SourceType minimalDTOToSourceType(MinimalSourceTypeDTO minimalSourceTypeDetailsDto);

    List<SourceType> minimalDTOsToSourceTypes(List<MinimalSourceTypeDTO> minimalProjectDetailsDtos);

    /**
     * Generating the fromId for all mappers if the databaseType is sql, as the class has
     * relationship to it might need it, instead of creating a new attribute to know if the entity
     * has any relationship from some other entity.
     *
     * @param id id of the entity
     * @return the entity instance
     */

    default SourceType sourceTypeFromId(Long id) {
        if (id == null) {
            return null;
        }
        SourceType sourceType = new SourceType();
        sourceType.setId(id);
        return sourceType;
    }


}
