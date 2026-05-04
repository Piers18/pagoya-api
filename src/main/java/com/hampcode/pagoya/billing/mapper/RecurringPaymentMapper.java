package com.hampcode.pagoya.billing.mapper;

import com.hampcode.pagoya.billing.dto.CreateRecurringPaymentRequest;
import com.hampcode.pagoya.billing.dto.RecurringPaymentResponse;
import com.hampcode.pagoya.billing.model.RecurringBillPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecurringPaymentMapper {

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "customer",  ignore = true)
    @Mapping(target = "provider",  ignore = true)
    @Mapping(target = "status",    ignore = true)
    @Mapping(target = "nextRunAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RecurringBillPayment toEntity(CreateRecurringPaymentRequest request);

    @Mapping(target = "providerName", source = "provider.name")
    @Mapping(target = "frequency", expression = "java(p.getFrequency() != null ? p.getFrequency().name() : null)")
    @Mapping(target = "status", expression = "java(p.getStatus() != null ? p.getStatus().name() : null)")
    RecurringPaymentResponse toResponse(RecurringBillPayment p);
}
