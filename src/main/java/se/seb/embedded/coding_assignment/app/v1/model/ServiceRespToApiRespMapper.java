/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.model;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.WARN;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import se.seb.embedded.coding_assignment.core.service.model.TransReportingServiceResponse;

@Mapper(componentModel = SPRING, unmappedSourcePolicy = WARN, unmappedTargetPolicy = WARN)
public interface ServiceRespToApiRespMapper
        extends Converter<TransReportingServiceResponse, TransactionReportingApiResponse> {

    TransactionReportingApiResponse convert(TransReportingServiceResponse serviceResponse);
}
