/* (C)2024 */
package se.seb.embedded.coding_assignment.core.service.model.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.converter.Converter;
import se.seb.embedded.coding_assignment.core.service.model.TransReportingServiceResponse;
import se.seb.embedded.coding_assignment.core.writer.model.FileSystemWriterServiceResponse;

@Mapper(
        componentModel = SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileWriterServiceRespToTransReportServiceRespMapper
        extends Converter<FileSystemWriterServiceResponse, TransReportingServiceResponse> {

    public FileWriterServiceRespToTransReportServiceRespMapper INSTANCE =
            Mappers.getMapper(FileWriterServiceRespToTransReportServiceRespMapper.class);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(source = "fileNames", target = "fileNames")
    public TransReportingServiceResponse convert(FileSystemWriterServiceResponse fileSystemResponse);
}
