package arden.java.kudago.start.command;

import arden.java.kudago.dto.response.places.LocationDto;
import arden.java.kudago.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FillLocationCommand implements Command<LocationDto> {
    private final StorageRepository<String, LocationDto> locationStorage;

    @Override
    public void execute(List<LocationDto> data) {
        data.forEach(locationDto -> locationStorage.create(locationDto.slug(), locationDto));
    }
}
