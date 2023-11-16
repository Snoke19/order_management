package com.example.order_management.dto.mapper;

import com.example.order_management.dto.GoodDto;
import com.example.order_management.dto.InfoGoodDto;
import com.example.order_management.models.Good;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoodsDtoMapper {

    GoodDto dtoToEntity(Good good);

    InfoGoodDto entityToDto(Good good);

    List<InfoGoodDto> entityToDto(List<Good> goods);

    Good dtoToEntity(GoodDto goodDto);
}
